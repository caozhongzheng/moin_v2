package com.moinapp.wuliao.modules.login.network;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.net.HttpPostProtocal;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.modules.login.LoginConstants;
import com.moinapp.wuliao.modules.login.LoginModule;

import java.util.Map;

/**
 * Created by liujiancheng on 15/5/4.
 * 用户邮箱注册时唯一性验证的服务器接口
 */
public class UserCheckEmailProtocal extends HttpPostProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger(LoginModule.MODULE_NAME);

    // ===========================================================
    // Constructors
    // ===========================================================
    public UserCheckEmailProtocal(String url, Map data, Object tag) {
        super(url, data);
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
        MyLog.i("UserCheckEmailProtocal.onHttpResult()... resultCode=" + resultCode + ", response=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new GetCheckEmailSuccessEvent(respone, getTag()));
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
    public static class GetCheckEmailSuccessEvent extends AbsProtocolEvent {
        private String mResponse;

        public GetCheckEmailSuccessEvent(String resp, Object tag) {
            setTag(tag);
            mResponse = resp;
        }

        public String getResponse() {
            return mResponse;
        }
    }

    // ===========================================================
    // interface
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