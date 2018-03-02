package com.moinapp.wuliao.modules.mine.network;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.net.HttpGetProtocal;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.modules.mine.listener.CosplayListener;
import com.moinapp.wuliao.modules.mine.MineModule;
import com.moinapp.wuliao.modules.mine.model.BaseResponse;
import com.moinapp.wuliao.modules.mine.model.EmojiCosPlay;

import java.util.List;

/**
 * Created by liujiancheng on 15/6/23.
 * 获取我的大咖秀表情的服务器接口
 */
public class GetCosplayProtocal extends HttpGetProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger(MineModule.MODULE_NAME);

    // ===========================================================
    // Constructors
    // ===========================================================
    public GetCosplayProtocal(String url, Object tag) {
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
        MyLog.i("GetCosplayProtocal.onHttpResult()... resultCode=" + resultCode + ", response=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new GetCosplaySuccessEvent(respone, getTag()));
        } else {
            Object object = getTag();
            if (object != null) {
                if (resultCode == NetConstants.NONE_NETWORK) {
                    ((CosplayListener) object).onNoNetwork();
                } else {
                    ((CosplayListener) object).onErr(resultCode);
                }
            }
        }
    }

    // ===========================================================
    // inner class
    // ===========================================================
    public static class GetCosplaySuccessEvent extends AbsProtocolEvent {
        private String mResponse;

        public GetCosplaySuccessEvent(String resp, Object tag) {
            setTag(tag);
            mResponse = resp;
        }

        public String getResponse() {
            return mResponse;
        }
    }

    /**
     * 服务器返回的大咖秀模型类，主要用于做json解析，具体业务逻辑层不用这个对象
     */
    public static class GetCosplayResponse extends BaseResponse {
        private List<EmojiCosPlay> cosplaylist;

        public List<EmojiCosPlay> getCosplaylist() {
            return cosplaylist;
        }

        public void setCosplaylist(List<EmojiCosPlay> cosplaylist) {
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
