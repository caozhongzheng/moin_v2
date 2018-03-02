package com.moinapp.wuliao.modules.cosplay.network;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.net.HttpPostProtocal;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.modules.cosplay.listener.GetCosplayResListener;
import com.moinapp.wuliao.modules.cosplay.model.CosplayResource;

import java.util.List;
import java.util.Map;

/**
 * Created by liujiancheng on 15/6/30.
 */
public class GetCosplayResProtocal extends HttpPostProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger("GetCosplayResProtocal");

    // ===========================================================
    // Constructors
    // ===========================================================
    public GetCosplayResProtocal(String url, Map data, Object tag) {
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
        MyLog.i("GetCosplayResProtocal.onHttpResult()... resultCode=" + resultCode + ", response=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new GetCosplayResSuccessEvent(respone, getTag()));
        } else {
            Object object = getTag();
            if (object != null) {
                if (resultCode == NetConstants.NONE_NETWORK) {
                    ((GetCosplayResListener) object).onNoNetwork();
                } else {
                    ((GetCosplayResListener) object).onErr(resultCode);
                }
            }
        }
    }

    // ===========================================================
    // inner class
    // ===========================================================
    public static class GetCosplayResSuccessEvent extends AbsProtocolEvent {
        private String mResponse;

        public GetCosplayResSuccessEvent(String resp, Object tag) {
            setTag(tag);
            mResponse = resp;
        }

        public String getResponse() {
            return mResponse;
        }
    }

    /**
     * 服务器返回的大咖秀资源列表
     */
    public static class GetCosplayResResponse {
        private List<CosplayResource> cosplaylist;

        public List<CosplayResource> getCosplaylist() {
            return cosplaylist;
        }

        public void setCosplaylist(List<CosplayResource> cosplaylist) {
            this.cosplaylist = cosplaylist;
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
