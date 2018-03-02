package com.moinapp.wuliao.modules.ipresource.network;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.net.HttpPostProtocal;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.modules.ipresource.IPEmojiListListener;
import com.moinapp.wuliao.modules.ipresource.IPResourceModule;
import com.moinapp.wuliao.modules.ipresource.IPresListListener;
import com.moinapp.wuliao.modules.ipresource.model.EmojiResource;
import com.moinapp.wuliao.modules.ipresource.model.IPResource;

import java.util.List;
import java.util.Map;

/**
 * Created by liujiancheng on 15/5/25.
 * 获取IP相关表情专题的服务器接口
 */
public class GetEmojiListProtocal extends HttpPostProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger(IPResourceModule.MODULE_NAME);

    // ===========================================================
    // Constructors
    // ===========================================================
    public GetEmojiListProtocal(String url, Map data, Object tag) {
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
        MyLog.i("GetEmojiListProtocal.onHttpResult()... resultCode=" + resultCode + ", response=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new GetEmojiListSuccessEvent(respone, getTag()));
        } else {
            Object object = getTag();
            if (object != null) {
                if (resultCode == NetConstants.NONE_NETWORK) {
                    ((IPEmojiListListener) object).onNoNetwork();
                } else {
                    ((IPEmojiListListener) object).onErr(resultCode);
                }
            }
        }
    }

    // ===========================================================
    // inner class
    // ===========================================================
    public static class GetEmojiListSuccessEvent extends AbsProtocolEvent {
        private String mResponse;

        public GetEmojiListSuccessEvent(String resp, Object tag) {
            setTag(tag);
            mResponse = resp;
        }

        public String getResponse() {
            return mResponse;
        }
    }

    /**
     * 获取表情专辑列表的response
     */
    public static class GetEmojiListResponse {
        private int result;
        public List<EmojiResource> emojilist;//某个ip相关的表情专辑列表
        public List<EmojiResource> suggest;//推荐的相关表情专题

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public List<EmojiResource> getEmojilist() {
            return emojilist;
        }

        public void setEmojilist(List<EmojiResource> emojilist) {
            this.emojilist = emojilist;
        }

        public List<EmojiResource> getSuggest() {
            return suggest;
        }

        public void setSuggest(List<EmojiResource> suggest) {
            this.suggest = suggest;
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