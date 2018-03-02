package com.moinapp.wuliao.modules.wowo.network;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.net.HttpPostProtocal;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.modules.wowo.IWoListener;
import com.moinapp.wuliao.modules.wowo.model.WowoInfo;

import java.util.List;
import java.util.Map;

/**
* Created by liujiancheng on 15/6/8.
 * 获取未关注的窝列表
*/
public class GetSuggestWoListProtocal extends HttpPostProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger("GetSuggestWoListProtocal");

    // ===========================================================
    // Constructors
    // ===========================================================
    public GetSuggestWoListProtocal(String url, Map data, Object tag) {
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
        MyLog.i("GetSuggestWoListProtocal.onHttpResult() resultCode=" + resultCode + ", response1=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new GetSuggestWoListSuccessEvent(respone, getTag()));
        } else {
            Object object = getTag();
            if (object != null) {
                if (resultCode == NetConstants.NONE_NETWORK) {
                    ((IWoListener) object).onNoNetwork();
                } else {
                    ((IWoListener) object).onErr(resultCode);
                }
            }
        }
    }

    // ===========================================================
    // inner class
    // ===========================================================
    public static class GetSuggestWoListSuccessEvent extends AbsProtocolEvent {
        private String mResponse;

        public GetSuggestWoListSuccessEvent(String resp, Object tag) {
            setTag(tag);
            mResponse = resp;
        }

        public String getResponse() {
            return mResponse;
        }
    }

    /**
     * 获取已经关注的窝列表的response
     */
    public static class GetMyWoListResponse {
        private List<WowoInfo> wowos;

        public List<WowoInfo> getWowos() {
            return wowos;
        }

        public void setWowos(List<WowoInfo> wowos) {
            this.wowos = wowos;
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
