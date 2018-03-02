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
import com.moinapp.wuliao.modules.ipresource.model.EmojiResource;
import com.moinapp.wuliao.modules.ipresource.model.IPResource;
import com.moinapp.wuliao.modules.mine.model.EmojiCosPlay;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;
import com.moinapp.wuliao.modules.wowo.model.WowoInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by liujiancheng on 15/8/11.
 * 搜索结果的服务器接口
 */
public class SearchTagProtocal extends HttpPostProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger(IPResourceModule.MODULE_NAME);
    private int mType = -1;

    // ===========================================================
    // Constructors
    // ===========================================================
    public SearchTagProtocal(String url, Map data, int type,Object tag) {
        super(url, data);
        mType = type;
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
        MyLog.i("SearchTagProtocal.onHttpResult()... resultCode=" + resultCode + ", response=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new SearchTagSuccessEvent(respone, mType, getTag()));
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
    public static class SearchTagSuccessEvent extends AbsProtocolEvent {
        private String mResponse;
        private int mType;

        public SearchTagSuccessEvent(String resp, int type, Object tag) {
            setTag(tag);
            mType = type;
            mResponse = resp;
        }

        public String getResponse() {
            return mResponse;
        }

        public int getType() {
            return mType;
        }
    }

    public static class SearchTagResponse {
        private int result;
        private List<IPResource> iplist;
        private List<EmojiResource> emojilist;
        private List<EmojiCosPlay> cosplaylist;
        private List<PostInfo> posterlist;
        private List<WowoInfo> wowolist;

        public int getResult() {
            return result;
        }
        public void setResult(int result) {
            this.result = result;
        }

        public List<IPResource> getIplist() {
            return iplist;
        }

        public void setIplist(List<IPResource> iplist) {
            this.iplist = iplist;
        }

        public List<EmojiResource> getEmojilist() {
            return emojilist;
        }

        public void setEmojilist(List<EmojiResource> emojilist) {
            this.emojilist = emojilist;
        }

        public List<EmojiCosPlay> getCosplaylist() {
            return cosplaylist;
        }

        public void setCosplaylist(List<EmojiCosPlay> cosplaylist) {
            this.cosplaylist = cosplaylist;
        }

        public List<PostInfo> getPosterlist() {
            return posterlist;
        }

        public void setPosterlist(List<PostInfo> posterlist) {
            this.posterlist = posterlist;
        }

        public List<WowoInfo> getWowolist() {
            return wowolist;
        }

        public void setWowolist(List<WowoInfo> wowolist) {
            this.wowolist = wowolist;
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

