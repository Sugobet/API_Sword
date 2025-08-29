package burp;

import burp.api.montoya.http.handler.HttpResponseReceived;
import burp.api.montoya.http.message.HttpRequestResponse;

public class SuperHttpReqAndRes {
    private final HttpRequestResponse req_res;
    private final HttpResponseReceived res;

    SuperHttpReqAndRes(HttpRequestResponse req_res, HttpResponseReceived res)
    {
        this.req_res = req_res;
        this.res = res;
    }

    public HttpRequestResponse getReq_res()
    {
        return req_res;
    }

    public HttpResponseReceived getRes()
    {
        return res;
    }
}
