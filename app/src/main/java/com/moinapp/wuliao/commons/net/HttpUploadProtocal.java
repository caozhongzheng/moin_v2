package com.moinapp.wuliao.commons.net;

import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

/**
 * Created by liujiancheng on 15/5/13.
 */
public abstract class HttpUploadProtocal extends AbsProtocol {
    private static ILogger MyLog = LoggerFactory.getLogger("HttpUploadProtocal");
    private String mUrl;
    private String mFilePath;
    private String mKey;

    public HttpUploadProtocal(String url, String key, String data) {
        super();
        mUrl = url;
        mFilePath = data;
        mKey = key;
    }

    @Override
    protected int getProtocolId() {
        return 0;
    }

    @Override
    protected void process() {
        try {
            MyLog.i("HttpUploadProtocal.process().....");
            AbsHttpOperation http = new AbsHttpOperation(mContext, getCallback());
            http.uploadFile(mUrl, mKey, mFilePath);
        } catch (Exception e) {
            MyLog.e(e);
        }
    }

    protected abstract HttpObserver getCallback();
}
