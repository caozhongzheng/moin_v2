package com.moinapp.wuliao.commons.net;


import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

import java.util.Map;

/**
 * Created by liujiancheng on 15/5/4.
 */
public abstract class HttpPutProtocal extends AbsProtocol {
    private static ILogger MyLog = LoggerFactory.getLogger("HttpPutProtocal");
    private String mUrl;
    private Map mData;

    public HttpPutProtocal(String url, Map data) {
        super();
        mUrl = url;
        mData = data;
    }

    @Override
    protected int getProtocolId() {
        return 0;
    }

    @Override
    protected void process() {
        try {
            MyLog.i("HttpPutProtocal.process().....");
            AbsHttpOperation http = new AbsHttpOperation(mContext, getCallback());
            http.doPut(mUrl, mData);
        } catch (Exception e) {
            MyLog.e(e.getMessage());
        }
    }

    protected abstract HttpObserver getCallback();
}