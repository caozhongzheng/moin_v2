package com.moinapp.wuliao.modules.mine.network;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.net.HttpPostProtocal;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.modules.mine.MineModule;
import com.moinapp.wuliao.modules.mine.listener.MyPostListener;
import com.moinapp.wuliao.modules.mine.listener.UserFeedbackListener;
import com.moinapp.wuliao.modules.mine.model.BaseResponse;

import java.util.Map;

/**
* Created by liujiancheng on 15/6/23.
*/
public class UserFeedbackProtocal extends HttpPostProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger(MineModule.MODULE_NAME);

    // ===========================================================
    // Constructors
    // ===========================================================
    public UserFeedbackProtocal(String url, Map data, Object tag) {
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
        MyLog.i("UserFeedbackProtocal.onHttpResult()... resultCode=" + resultCode + ", response=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new UserFeedbackSuccessEvent(respone, getTag()));
        } else {
            Object object = getTag();
            if (object != null) {
                if (resultCode == NetConstants.NONE_NETWORK) {
                    ((UserFeedbackListener) object).onNoNetwork();
                } else {
                    ((UserFeedbackListener) object).onErr(resultCode);
                }
            }
        }
    }

    // ===========================================================
    // inner class
    // ===========================================================
    public static class UserFeedbackSuccessEvent extends AbsProtocolEvent {
        private String mResponse;

        public UserFeedbackSuccessEvent(String resp, Object tag) {
            setTag(tag);
            mResponse = resp;
        }

        public String getResponse() {
            return mResponse;
        }
    }

    /**
     * 服务器返回的用户反馈的response
     */
    public static class UserFeedbackResponse extends BaseResponse {

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
