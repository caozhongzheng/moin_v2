package com.moinapp.wuliao.modules.login.network;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.net.HttpPostProtocal;
import com.moinapp.wuliao.commons.net.HttpUploadProtocal;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.modules.login.LoginConstants;
import com.moinapp.wuliao.modules.login.LoginModule;
import com.moinapp.wuliao.modules.login.model.BaseLoginResult;

import java.util.Map;

/**
 * Created by liujiancheng on 15/5/4.
 * 用户数据更新的服务器接口
 */
public class UserUploadImageProtocal extends HttpUploadProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger(LoginModule.MODULE_NAME);

    // ===========================================================
    // Constructors
    // ===========================================================
    public UserUploadImageProtocal(String url, String filePath, Object tag) {
        super(url, LoginConstants.AVATAR, filePath);
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
        MyLog.i("UserUploadImageProtocal.onHttpResult()... resultCode=" + resultCode + ", response=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new GetUploadImageSuccessEvent(respone, getTag()));
        } else {
            Object object = getTag();
            if (object != null) {
                if (resultCode == NetConstants.NONE_NETWORK) {
                    ((IListener) object).onNoNetwork();
                } else {
                    ((IListener) object).onErr(resultCode);
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

    public static class UploadImageResult extends BaseLoginResult {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
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
