package burp;


import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.extension.ExtensionUnloadingHandler;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.*;


// import static burp.api.montoya.ui.editor.EditorOptions.READ_ONLY;

// import burp.ui;


public class Extension implements BurpExtension {
    private MontoyaApi api;
    private SwordMain sM;
    private MyTableModel tableModel;

    @Override
    public void initialize(MontoyaApi montoyaApi) {
        this.api = montoyaApi;

        montoyaApi.extension().setName("NSFOCUS-API_Sword");
        montoyaApi.logging().logToOutput("[Main]: NSFOCUS API_Sword v1.0.2 loaded!");
        montoyaApi.logging().logToOutput("[Main]: author：NSFOCUS/APT250 冯家鸣(M1n9K1n9)");
        montoyaApi.logging().logToOutput("[Main]: github: https://github.com/Sugobet/API_Sword");

        // 注册上下文菜单
        montoyaApi.userInterface().registerContextMenuItemsProvider(new ContextMenuItemsProvider()
        {
            @Override
            public List<Component> provideMenuItems(ContextMenuEvent event)
            {
                if (event.isFromTool(ToolType.TARGET)) {
                    JMenuItem menuItem = new JMenuItem("API Scan");
                    menuItem.addActionListener(l -> {
                        montoyaApi.logging().logToOutput("[Context Menu]: API Scan");

                        apiScan();
                    });
                    return List.of(menuItem);
                }
                return null;
            }
        });

        // 注册选项卡
        tableModel = new MyTableModel();
        sM = new SwordMain();
        api.userInterface().registerSuiteTab("API Sword", sM.InitRootComponent(api, tableModel));

        // 注册http监听
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        api.http().registerHttpHandler(new MyHttpHandler(tableModel, sM.getScopeList(), api, sM, executor));

        // 注册卸载插件处理
        api.extension().registerUnloadingHandler(new ExtensionUnloadingHandler() {
            @Override
            public void extensionUnloaded() {
                sM.isStop.setSelected(true);
                sM.isReqAPI.setSelected(false);
                tableModel.removeAll();
            }
        });
    }

    void apiScan()
    {

    }
}