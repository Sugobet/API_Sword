/*
 * Copyright (c) 2023. PortSwigger Ltd. All rights reserved.
 *
 * This code may be used to extend the functionality of Burp Suite Community Edition
 * and Burp Suite Professional, provided that this usage does not violate the
 * license terms for those products.
 */

package burp;

import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.message.HttpRequestResponse;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class MyTableModel extends AbstractTableModel
{
    private final List<HttpRequestResponse> resLog;
    private final List<SuperHttpReqAndRes> _resLog;
    private final List<HttpResponseReceived> orgLog;

    public MyTableModel()
    {
        this.resLog = new ArrayList<>();
        this._resLog = new ArrayList<>();
        this.orgLog = new ArrayList<>();
    }

    @Override
    public synchronized int getRowCount()
    {
        return resLog.size();
    }

    @Override
    public int getColumnCount()
    {
        return 5;
    }

    @Override
    public String getColumnName(int column)
    {
        return switch (column)
                {
                    case 0 -> "Status Code";
                    case 1 -> "URL";
                    case 2 -> "Query";
                    case 3 -> "Length";
                    case 4 -> "Comment";
                    default -> "";
                };
    }

    @Override
    public synchronized Object getValueAt(int rowIndex, int columnIndex)
    {
        HttpRequestResponse responseReceived = resLog.get(rowIndex);

        return switch (columnIndex)
                {
                    case 0 -> responseReceived.response().statusCode();
                    case 1 -> responseReceived.request().url();
                    case 2 -> responseReceived.request().query();
                    case 3 -> responseReceived.response().body().length();
                    case 4 -> responseReceived.annotations().notes();
                    default -> "";
                };
    }

    private synchronized void add(HttpRequestResponse responseReceived)
    {
        // int index = resLog.size();
        resLog.add(responseReceived);
        // fireTableRowsInserted(index, index);
    }

    public synchronized void add1(SuperHttpReqAndRes responseReceived)
    {
        // int index = _resLog.size();
        _resLog.add(responseReceived);
        // fireTableRowsInserted(index, index);
    }

    public synchronized HttpRequestResponse getRes(int rowIndex)
    {
        return resLog.get(rowIndex);
    }

    public synchronized void orgAdd(HttpResponseReceived responseReceived) {
        // int index = orgLog.size();
        orgLog.add(responseReceived);
        // fireTableRowsInserted(index, index);
    }

    public synchronized HttpResponseReceived getOrgRes(int rowIndex)
    {
        return orgLog.get(rowIndex);
    }

    public synchronized void pushDisplayData(List<SuperHttpReqAndRes> data)
    {
        this.resLog.clear();
        this.orgLog.clear();

        for (SuperHttpReqAndRes hrr : data)
        {
            add(hrr.getReq_res());
            orgAdd(hrr.getRes());
        }

        fireTableDataChanged();
    }

    public synchronized void pushDisplayData1(SuperHttpReqAndRes data)
    {
        int index = resLog.size();

        add(data.getReq_res());
        orgAdd(data.getRes());

        fireTableRowsInserted(index, index);
    }

    public synchronized List<SuperHttpReqAndRes> getResList()
    {
        return this._resLog;
    }

    public synchronized List<HttpRequestResponse> getResLogList()
    {
        return this.resLog;
    }

    public void removeAll()
    {
        this.resLog.clear();
        this.orgLog.clear();
        this._resLog.clear();
        fireTableDataChanged();
    }

}
