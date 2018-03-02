package com.moinapp.wuliao.commons.net;

import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

/**
 * Created by liujiancheng on 15/4/30.
 */
public abstract class HttpDeleteProtocal extends AbsProtocol {
    private static ILogger MyLog = LoggerFactory.getLogger("HttpDeleteProtocal");
    private String mUrl;

    public HttpDeleteProtocal(String url) {
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
            MyLog.i("HttpDeleteProtocal.process().....");
            AbsHttpOperation http = new AbsHttpOperation(mContext, getCallback());
            http.doDelete(mUrl);
        } catch (Exception e) {
            MyLog.e(e.getMessage());
        }
    }

    protected abstract HttpObserver getCallback();
}
