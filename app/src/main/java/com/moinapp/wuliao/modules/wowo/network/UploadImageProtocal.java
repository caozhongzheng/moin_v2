package com.moinapp.wuliao.modules.wowo.network;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.net.HttpUploadProtocal;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.modules.wowo.IPostListener;
import com.moinapp.wuliao.modules.wowo.WowoContants;
import com.moinapp.wuliao.modules.wowo.WowoModule;

/**
* Created by liujiancheng on 15/6/9.
 * 用户在窝窝里面上传图片的接口协议
*/
public class UploadImageProtocal extends HttpUploadProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger(WowoModule.MODULE_NAME);

    // ===========================================================
    // Constructors
    // ===========================================================
    public UploadImageProtocal(String url, String filePath, Object tag) {
        super(url, WowoContants.UPLOAD_PIC, filePath);
        setTag(tag);
    }

    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    protected HttpObserver getCallback() {
        return this;
    }

    @Override
    public void onHttpResult(int resultCode, String respone) {
        MyLog.i("UploadImageProtocal.onHttpResult()... resultCode=" + resultCode + ", response=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new GetUploadImageSuccessEvent(respone, getTag()));
        } else {
            Object object = getTag();
            if (object != null) {
                if (resultCode == NetConstants.NONE_NETWORK) {
                    ((IPostListener) object).onNoNetwork();
                } else {
                    ((IPostListener) object).onErr(resultCode);
                }
            }
        }
    }

    // ===========================================================
    // inner class
    // ===========================================================
    public static class GetUploadImageSuccessEvent extends AbsProtocolEvent {
        private String mResponse;

        public GetUploadImageSuccessEvent(String resp, Object tag) {
            setTag(tag);
            mResponse = resp;
        }

        public String getResponse() {
            return mResponse;
        }
    }

    public static class UploadImageResult extends BaseResponse {
        private String picid;

        public String getPicid() {
            return picid;
        }

        public void setPicid(String picid) {
            this.picid = picid;
        }
    }

    // ===========================================================
    // interface of super class
    // ===========================================================
    @Override
    protected void beforeCall() {

    }

    @Override
    protected void afterCall() {

    }

    @Override
    protected void onError() {

    }
}
