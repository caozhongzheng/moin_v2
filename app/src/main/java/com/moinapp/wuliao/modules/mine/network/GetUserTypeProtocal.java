package com.moinapp.wuliao.modules.mine.network;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.net.HttpGetProtocal;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.modules.mine.MineModule;
import com.moinapp.wuliao.modules.mine.listener.UserTypeListener;
import com.moinapp.wuliao.modules.mine.model.BaseResponse;

import java.util.List;

/**
* Created by liujiancheng on 15/6/23.
*/
public class GetUserTypeProtocal extends HttpGetProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger(MineModule.MODULE_NAME);

    // ===========================================================
    // Constructors
    // ===========================================================
    public GetUserTypeProtocal(String url, Object tag) {
        super(url);
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
        MyLog.i("GetUserTypeProtocal.onHttpResult()... resultCode=" + resultCode + ", response=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new GetUserTypeSuccessEvent(respone, getTag()));
        } else {
            Object object = getTag();
            if (object != null) {
                if (resultCode == NetConstants.NONE_NETWORK) {
                    ((UserTypeListener) object).onNoNetwork();
                } else {
                    ((UserTypeListener) object).onErr(resultCode);
                }
            }
        }
    }

    // ===========================================================
    // inner class
    // ===========================================================
    public static class GetUserTypeSuccessEvent extends AbsProtocolEvent {
        private String mResponse;

        public GetUserTypeSuccessEvent(String resp, Object tag) {
            setTag(tag);
            mResponse = resp;
        }

        public String getResponse() {
            return mResponse;
        }
    }

    /**
     * 服务器返回的用户登陆类型信息
     */
    public static class GetUserTypeResponse extends BaseResponse {
        private int type;
        private List<String> namelist;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<String> getNamelist() {
            return namelist;
        }

        public void setNamelist(List<String> namelist) {
            this.namelist = namelist;
        }
    }

    public static ILogger getMyLog() {
        return MyLog;
    }

    public static void setMyLog(ILogger myLog) {
        MyLog = myLog;
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

