package com.moinapp.wuliao.modules.ipresource.network;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.net.HttpPostProtocal;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.modules.ipresource.IPResourceModule;

import java.util.Map;

/**
* Created by liujiancheng on 15/6/24.
*/
public class FavoriateIPProtocal extends HttpPostProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger(IPResourceModule.MODULE_NAME);

    // ===========================================================
    // Constructors
    // ===========================================================
    public FavoriateIPProtocal(String url, Map data, Object tag) {
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
        MyLog.i("FavoriateIPProtocal.onHttpResult()... resultCode=" + resultCode + ", response=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new FavoriateIPSuccessEvent(respone, getTag()));
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
    public static class FavoriateIPSuccessEvent extends AbsProtocolEvent {
        private String mResponse;

        public FavoriateIPSuccessEvent(String resp, Object tag) {
            setTag(tag);
            mResponse = resp;
        }

        public String getResponse() {
            return mResponse;
        }
    }

    public static class FavoriateIPResponse {
        private int result;

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
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

