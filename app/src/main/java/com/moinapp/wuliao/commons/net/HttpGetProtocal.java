package com.moinapp.wuliao.commons.net;

import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

/**
 * Created by liujiancheng on 15/4/30.
 */
public abstract class HttpGetProtocal extends AbsProtocol {
    private static ILogger MyLog = LoggerFactory.getLogger("HttpGetProtocal");
    private String mUrl;

    public HttpGetProtocal(String url) {
        super();
        mUrl = url;
    }

    @Override
    protected int getProtocolId() {
        return 0;
    }

    @Override
    protected void process() {
        try {
            MyLog.i("HttpGetProtocal.process().....");
            AbsHttpOperation http = new AbsHttpOperation(mContext, getCallback());
            http.doGet(mUrl);
        } catch (Exception e) {
            MyLog.e(e.getMessage());
        }
    }

    protected abstract HttpObserver getCallback();
}
