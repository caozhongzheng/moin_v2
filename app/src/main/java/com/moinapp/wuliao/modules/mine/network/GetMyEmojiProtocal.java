package com.moinapp.wuliao.modules.mine.network;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.net.HttpGetProtocal;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.modules.ipresource.model.EmojiResource;
import com.moinapp.wuliao.modules.mine.MineModule;
import com.moinapp.wuliao.modules.mine.listener.MyEmojiListener;
import com.moinapp.wuliao.modules.mine.model.BaseResponse;

import java.util.List;

/**
* Created by liujiancheng on 15/6/23.
*/
public class GetMyEmojiProtocal extends HttpGetProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger(MineModule.MODULE_NAME);

    // ===========================================================
    // Constructors
    // ===========================================================
    public GetMyEmojiProtocal(String url, Object tag) {
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
        MyLog.i("GetMyEmojiProtocal.onHttpResult()... resultCode=" + resultCode + ", response=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new GetMyEmojiSuccessEvent(respone, getTag()));
        } else {
            Object object = getTag();
            if (object != null) {
                if (resultCode == NetConstants.NONE_NETWORK) {
                    ((MyEmojiListener) object).onNoNetwork();
                } else {
                    ((MyEmojiListener) object).onErr(resultCode);
                }
            }
        }
    }

    // ===========================================================
    // inner class
    // ===========================================================
    public static class GetMyEmojiSuccessEvent extends AbsProtocolEvent {
        private String mResponse;

        public GetMyEmojiSuccessEvent(String resp, Object tag) {
            setTag(tag);
            mResponse = resp;
        }

        public String getResponse() {
            return mResponse;
        }
    }

    /**
     * 服务器返回的我的表情列表
     */
    public static class GetMyEmojiResponse extends BaseResponse {
        private List<EmojiResource> emojilist;

        public List<EmojiResource> getEmojilist() {
            return emojilist;
        }

        public void setEmojilist(List<EmojiResource> emojilist) {
            this.emojilist = emojilist;
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
