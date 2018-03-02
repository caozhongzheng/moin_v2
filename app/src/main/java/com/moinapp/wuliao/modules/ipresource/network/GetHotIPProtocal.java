package com.moinapp.wuliao.modules.ipresource.network;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.net.HttpGetProtocal;
import com.moinapp.wuliao.commons.net.HttpPostProtocal;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.modules.ipresource.IPResourceModule;
import com.moinapp.wuliao.modules.ipresource.IPresListListener;
import com.moinapp.wuliao.modules.ipresource.model.IPResource;

/**
 * Created by liujiancheng on 15/5/12.
 * 获取热门IP的服务器接口
 */
public class GetHotIPProtocal extends HttpPostProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger(IPResourceModule.MODULE_NAME);

    // ===========================================================
    // Constructors
    // ===========================================================
    public GetHotIPProtocal(String url, Object tag) {
        super(url, null);
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
        MyLog.i("GetHotIPProtocal.onHttpResult()... resultCode=" + resultCode + ", response=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new GetHotIPSuccessEvent(respone, getTag()));
        } else {
            Object object = getTag();
            if (object != null) {
                if (resultCode == NetConstants.NONE_NETWORK) {
                    ((IPresListListener) object).onNoNetwork();
                } else {
                    ((IPresListListener) object).onErr(resultCode);
                }
            }
        }
    }

    // ===========================================================
    // inner class
    // ===========================================================
    public static class GetHotIPSuccessEvent extends AbsProtocolEvent {
        private String mResponse;

        public GetHotIPSuccessEvent(String resp, Object tag) {
            setTag(tag);
            mResponse = resp;
        }

        public String getResponse() {
            return mResponse;
        }
    }

    /**
     * Created by liujiancheng on 15/5/15.
     * 服务器返回的热门ip模型类，主要用于做json解析，具体业务逻辑层不用这个对象
     */
    public static class HotIPResponse {
        private int result;
        public IPResource hotip;

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public IPResource getHotip() {
            return hotip;
        }

        public void setHotip(IPResource hotip) {
            this.hotip = hotip;
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