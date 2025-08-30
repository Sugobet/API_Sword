/*
 * Copyright (c) 2023. PortSwigger Ltd. All rights reserved.
 *
 * This code may be used to extend the functionality of Burp Suite Community Edition
 * and Burp Suite Professional, provided that this usage does not violate the
 * license terms for those products.
 */

package burp;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MyHttpHandler implements HttpHandler
{
    private final MyTableModel tableModel;
    private final JTextArea scopeList;
    private final MontoyaApi api;
    private final List<String> ur_list;
    private final AtomicInteger _sign;
    private final Pattern pattern = Pattern.compile("(?:\"|'|`)(((?:[a-zA-Z]{1,10}://|//)[^\"'`/]{1,}\\.[a-zA-Z]{2,}[^\"'`]{0,})|((?:/|\\.\\./|\\./)[^\"'`><,;|*()%^/\\\\\\[\\]][^\"'`><,;|()]{1,})|([a-zA-Z0-9_\\-/]{1,}/[a-zA-Z0-9_\\-/]{1,}\\.(?:[a-zA-Z]{1,4}|action)(?:[\\?|#][^\"|'|`]{0,}|))|([a-zA-Z0-9_\\-/]{1,}/[a-zA-Z0-9_\\-/]{3,}(?:[\\?|#][^\"|'|`]{0,}|))|([a-zA-Z0-9_\\-]{1,}\\.(?:\\w)(?:[\\?|#][^\"|'|`]{0,}|)))(?:\"|'|`)");

    private final SwordMain sM;
    private final DefaultMutableTreeNode TreeRoot;

    private final ThreadPoolExecutor executorService;

    public MyHttpHandler(MyTableModel tableModel, JTextArea scopeList, MontoyaApi api, SwordMain sM, ThreadPoolExecutor executorService)
    {

        this.tableModel = tableModel;
        this.scopeList = scopeList;
        this.api = api;
        this.ur_list = Collections.synchronizedList(new ArrayList<>());
        this._sign = new AtomicInteger(0);

        this.sM = sM;
        this.TreeRoot = sM.getTreeRoot();

        this.executorService = executorService;

        sM.useTPool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 自定义进程数
                setExecutorService(Integer.parseInt(sM.threadNum.getText()));
                JOptionPane.showMessageDialog(sM.useTPool.getRootPane(), "ok！", "Tip", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    public void setExecutorService(int n)
    {
        executorService.setMaximumPoolSize(n);
        executorService.setCorePoolSize(n);
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent)
    {
        return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived)
    {
        try {
            executorService.submit(() ->
            {
                _sign.incrementAndGet();
                try {
                    this.get(responseReceived);
                } finally
                {
                   if (_sign.decrementAndGet() <= 0)
                   {
                       ur_list.clear();
                   }
                }
            });
            // this.get(responseReceived);
        } catch (Exception e) {
            api.logging().logToError(e.toString());
        }

        return ResponseReceivedAction.continueWith(responseReceived);
    }

    // 处理函数
    private void get(HttpResponseReceived responseReceived)
    {
        if (sM.isStop.isSelected())
        {
            return;
        }
        // api.logging().logToOutput(scope.toString());

        String url = responseReceived.initiatingRequest().url().strip();

        // 判断范围
        // api.logging().logToOutput(url);
        boolean sign = isInScope(url);

        if (!sign)
        {
            return;
        }

        // 处理http
        String path = responseReceived.initiatingRequest().pathWithoutQuery();
        // api.logging().logToOutput(path);
        var pathList = path.split("/");
        // api.logging().logToOutput(Arrays.toString(pathList));
        if (pathList.length <= 1)
        {
            return;
        }

        // 过滤资源文件
        if (isResource(Arrays.stream(pathList).toList().getLast()))
        {
            return;
        }

        // 匹配js、html、json、xml，html还要单独从响应体判断
        // 记录访问过的文件，防环

        List<String> baseURLList = new ArrayList<>();

        // 提取api
        List<String> apiList = new ArrayList<>();
        if (isJss(Arrays.stream(pathList).toList().getLast()) || responseReceived.body().toString().contains("<html"))
        {
            // 提取link正则
            List<String> extractedLinks = new ArrayList<>();
            try {
                Matcher matcher = pattern.matcher(responseReceived.bodyToString());

                while (matcher.find()) {
                    for (int i = 2; i <= matcher.groupCount(); i++) {
                        if (matcher.group(i) != null) {
                            String link = matcher.group(i).strip();
                            // 提取api
                            extractedLinks.add(link);
                            // api.logging().logToOutput(link);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                return;
            }

            // api.logging().logToOutput(extractedLinks.toString());
            // 清洗api
            if (!extractedLinks.isEmpty())
            {
                for (String lnk : extractedLinks) {
                    // 过滤资源文件
                    if (isResource(lnk))
                    {
                        continue;
                    }

                    // 如果提取的lnk符合要求 进apiList
                    if (lnk.matches("^(?:https?:\\/\\/|\\/\\/)([a-zA-Z0-9.-]++(?::\\d+)?\\/)(?!\\s*$)[^\\s?#]+(?:[?#][^\\s]*)?$"))
                    {
                        // 二次范围判断
                        boolean sign11 = isInScope(lnk);
                        if (!sign11) {continue;}
                        // api.logging().logToOutput(lnk);
                        apiList.add(lnk);

                        // 是否从响应提取的url中添加为baseurl，判断lnk是否纯协议+domain/ip+port/+路径，无? 无参数
                        if (sM.isBaseURLFind.isSelected())
                        {
                            if (lnk.matches("^(?:https?:\\/\\/|\\/\\/)([a-zA-Z0-9.-]+(?::\\d+)?\\/)(?!\\s*$)[^\\s?#]*\\/$"))
                            {
                                baseURLList.add(lnk);
                            }
                        }
                        continue;
                    }

                    // 匹配正常路径(可选参数)
                    // 拼接api
                    // api.logging().logToOutput(lnk);
                    if (lnk.matches("^(?:/{0,}[a-zA-Z][a-zA-Z0-9\\._/-]*)?(?:\\?[^\\s\"']*)?$")) {

                        // url只要协议域名端口/
                        String regex2 = "((?:[^/]*/){3}).*";
                        String result1 = url.replaceFirst(regex2, "$1");
                        if (lnk.startsWith("/"))
                        {
                            lnk = lnk.replaceFirst("/", "");
                        }
                        apiList.add(result1 + lnk);

                        // 判断自定义fuzz路径
                        if (isPathFuzzing())
                        {
                            for (String pf : sM.getPathFuzzingList())
                            {
                                apiList.add(result1 + pf.strip() + lnk);
                            };
                        }

                        // 判断baseURLList，给它拼接上一起fuzz
                        if (sM.isBaseURLFind.isSelected())
                        {
                            for (String _url : baseURLList)
                            {
                                apiList.add(_url + lnk);
                                // 判断自定义fuzz路径
                                if (isPathFuzzing())
                                {
                                    for (String pf : sM.getPathFuzzingList())
                                    {
                                        apiList.add(_url + pf.strip() + lnk);
                                    };
                                }
                            }
                        }

                        // 判断是否在参数前添加自定义路径
                        if (sM.isBackCustomPath.isSelected())
                        {
                            String[] _lnk1 = lnk.split("\\?", 2);
                            if (_lnk1.length == 2)
                            {
                                for (String pf : sM.getBackCustomPathList())
                                {
                                    apiList.add(result1 + _lnk1[0] + pf.strip() + "?" + _lnk1[1]);
                                }
                            } else
                            {
                                for (String pf : sM.getBackCustomPathList())
                                {
                                    apiList.add(result1 + _lnk1[0] + pf.strip());
                                }
                            }

                        }
                    }
                }
            }

            // api.logging().logToOutput(apiList.toString());
        }

        // 请求速率限制
        long rate = -1;
        if (sM.isReqAPI.isSelected())
        {
            rate = Long.parseLong(sM.sleepTime.getText());
        }

        // 向api发起请求
        var model = ((DefaultTreeModel)sM.getSiteMapTreeRoot().getModel());
        String[] wrnList = sM.getWarnList();
        for (String apii : apiList)
        {
            if (!sM.isReqAPI.isSelected()) {break;}
            // 二次范围判断
            boolean sign1 = isInScope(apii.strip());

            if (!sign1)
            {
                continue;
            }

            // 防环
            if (ur_list.contains(apii.strip()))
            {
                continue;
            }
            ur_list.add(apii);

            // 判断危险接口跳过
            if (sM.isBypassWarn.isSelected())
            {
                boolean _sign0 = false;
                for (String ws : wrnList)
                {
                    if (apii.contains(ws)) {
                        _sign0 = true;
                        break;
                    }
                }
                if (_sign0)
                {
                    api.logging().logToOutput("[Log]: 从 " + responseReceived.initiatingRequest().url() + " 获取 " + apii.strip() + "  [危险接口已跳过]");
                    continue;
                }
            }

            api.logging().logToOutput("[Log]: 从 " + responseReceived.initiatingRequest().url() + " 获取 " + apii.strip());

            if (rate > 0)
            {
                try {
                    Thread.sleep(rate);
                } catch (InterruptedException ignored) {
                }
            }

            HttpRequestResponse res = null;
            HttpRequest hr;
            try {
                hr = HttpRequest.httpRequestFromUrl(apii.strip());
            } catch (Exception e) {
                continue;
            }
            HttpHeader hd = hr.header("Host");

            // 携带原headers
            if (sM.isUseHeader.isSelected())
            {
                hr = hr.withRemovedHeaders(responseReceived.initiatingRequest().headers());
                hr = hr.withAddedHeaders(responseReceived.initiatingRequest().headers());
                hr = hr.withUpdatedHeader(hd);
            }
            hr = hr.withRemovedHeader("Content-Length");
            hr = hr.withAddedHeader("Content-Length", "0");

            // 自定义headers
            if (sM.isSetHeaders.isSelected())
            {
                for (String kv : sM.getSetHeaderList())
                {
                    var kvl = kv.strip().split(":");
                    String k = kvl[0];
                    String v = kvl[1];
                    if (v.startsWith(" "))
                    {
                        v = v.replaceFirst(" ", "");
                    }
                    hr = hr.withUpdatedHeader(k, v);
                }
            }

            try {
                if (!sM.isReqAPI.isSelected()) {break;}
                res = api.http().sendRequest(hr);
            } catch (Exception ignored) {
                continue;
            }
            if (res == null || !res.hasResponse()){continue;}
            // 如果访问不到，则尝试POST
            if (res.response().statusCode() != 200)
            {
                try {
                    res = api.http().sendRequest(hr.withMethod("POST"));
                } catch (Exception ignored) {
                    continue;
                }
            }

            boolean _sign1 = false;
            try {
                for (String sc : sM.getStatusCodeFilterList())
                {
                    if (res.response().statusCode() == Integer.parseInt(sc.strip())) {_sign1 = true;}
                }
            } catch (NumberFormatException e) {
                continue;
            }
            if (_sign1) {continue;}

            api.siteMap().add(res);
            // 非资源文件才添加进table model并进行下一步tree list操作
            if (isResource(apii) || isJss(apii))
            {
                continue;
            }
            // tableModel.orgAdd1(responseReceived);

            tableModel.add1(new SuperHttpReqAndRes(res, responseReceived));

            // 添加到tree list ui
            // api.logging().logToOutput(String.valueOf(TreeRoot.getChildCount()));
            DefaultMutableTreeNode urlNameNode = null;
            // 获取url，判断url node是否存在，再添加新node
            // url只要协议域名端口/
            String regex2 = "((?:[^/]*/){3}).*";
            String result1 = hr.url().replaceFirst(regex2, "$1");
            for (int i = 0; i < TreeRoot.getChildCount(); i++)
            {
                var uNode = (DefaultMutableTreeNode)TreeRoot.getChildAt(i);
                if (uNode.getUserObject().equals(result1))
                {
                    urlNameNode = uNode;
                    break;
                }
            }


            // api.logging().logToOutput(TreeRoot.getChildAt(i).toString());
            if (urlNameNode == null)
            {
                // 添加url node
                DefaultMutableTreeNode site1 = new DefaultMutableTreeNode(result1);
                // TreeRoot.add(site1);

                // 更新site map, 与add二选一
                SwingUtilities.invokeLater(() -> model.insertNodeInto(site1, TreeRoot, TreeRoot.getChildCount()));

                urlNameNode = site1;
            }

            // 添加路径node到list
            // 循环判断路径node是否存在
            var pl = hr.pathWithoutQuery().split("/");
            // var smtr = (DefaultTreeModel)SiteMapTreeRoot.getModel();
            for (int i1 = 0; i1 < pl.length - 1; i1++) {
                pl[i1] = "/" + pl[i1];

                boolean nodeExists = false;
                DefaultMutableTreeNode nextParentNode = null;
                for (int j = 0; j < urlNameNode.getChildCount(); j++)
                {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) urlNameNode.getChildAt(j);
                    if (child.getUserObject().equals(pl[i1])) {
                        nodeExists = true;
                        nextParentNode = child;
                        break;
                    }
                }

                // api.logging().logToOutput(pl[i1]);
                // 如果不存在，创建新节点并添加
                if (!nodeExists)
                {
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(pl[i1]);
                    // urlNameNode.add(newNode);

                    // 更新site map, 与add二选一
                    DefaultMutableTreeNode finalUrlNameNode = urlNameNode;
                    DefaultMutableTreeNode finalUrlNameNode1 = urlNameNode;
                    SwingUtilities.invokeLater(() -> model.insertNodeInto(newNode, finalUrlNameNode, finalUrlNameNode1.getChildCount()));

                    nextParentNode = newNode;

                }
                urlNameNode = nextParentNode;

                // 通知事件
                SwingUtilities.invokeLater(() -> sM.nodeChanged(tableModel));
            }
            // sM.setSiteMapPaneView();
        }

//        if (this._sign.get() <= 1)
//        {
//            ur_list.clear();
//        }
    }

    boolean isJss(String str)
    {
        return str.matches(".*\\.(?:js|mjs|json|xml|html|htm|shtml|map)(?:\\?[^#\\s]*)?(?:#[^\\s]*)?$");
    }

    boolean isResource(String str)
    {
        return str.matches(".*\\.(?:jpe?g|png|gif|svg|webp|ico|bmp|avif|css|mjs|swf|flv|woff2?|ttf|eot|mov|webm)(?:\\?[^#\\s]*)?(?:#[^\\s]*)?$");
    }

    boolean isInScope(String url)
    {
        var scope = scopeList.getText().lines().toList();
        for (String s : scope) {
            if (s.strip().equals("*")) {
                return true;
            }
            if (url.contains(s.strip())) {
                return true;
            }
        }
        return false;
    }

    boolean isPathFuzzing()
    {
        return sM.isAddPathFuzzing.isSelected();
    }
}
