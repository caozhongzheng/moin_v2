package com.moinapp.wuliao.modules.login.network;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.net.HttpPutProtocal;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.commons.net.NetworkingListener;
import com.moinapp.wuliao.modules.login.LoginModule;
import com.moinapp.wuliao.modules.login.model.BaseLoginResult;
import com.moinapp.wuliao.modules.login.model.UserInfo;

import java.util.Map;

/**
 * Created by liujiancheng on 15/5/4.
 * 用户数据更新的服务器接口
 */
public class UserUpdateProtocal extends HttpPutProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger(LoginModule.MODULE_NAME);

    // ===========================================================
    // Constructors
    // ===========================================================
    public UserUpdateProtocal(String url, Map data, Object tag) {
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
        MyLog.i("UserUpdateProtocal.onHttpResult()... resultCode=" + resultCode + ", response=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new GetUpdateSuccessEvent(respone, getTag()));
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
    public static class GetUpdateSuccessEvent extends AbsProtocolEvent {
        private String mResponse;

        public GetUpdateSuccessEvent(String resp, Object tag) {
            setTag(tag);
            mResponse = resp;
        }

        public String getResponse() {
            return mResponse;
        }
    }

    /**
     * 当用户更新成功后服务器会返回用户的最新信息
     */
    public static class UserUpdateResult extends BaseLoginResult{
        private UserInfo user;

        public UserInfo getUser() {
            return user;
        }

        public void setUser(UserInfo user) {
            this.user = user;
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
