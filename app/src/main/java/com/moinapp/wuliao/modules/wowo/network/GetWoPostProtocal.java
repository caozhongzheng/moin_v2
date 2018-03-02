package com.moinapp.wuliao.modules.wowo.network;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.net.HttpPostProtocal;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.modules.wowo.IPostListener;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;
import com.moinapp.wuliao.modules.wowo.model.WowoInfo;

import java.util.List;
import java.util.Map;

/**
* Created by liujiancheng on 15/6/9.
 * 获取窝中的帖子列表的服务器接口协议
*/
public class GetWoPostProtocal extends HttpPostProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger("public class GetWoPostProtocal extends HttpPostProtocal implements HttpObserver {\n");
    private int mFilter;//表示请求的是全部，精华还是表情贴
    // ===========================================================
    // Constructors
    // ===========================================================
    public GetWoPostProtocal(String url, Map data, int filter, Object tag) {
        super(url, data);
        setTag(tag);
        mFilter = filter;
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
        MyLog.i("GetWoPostProtocal.onHttpResult() resultCode=" + resultCode + ", response1=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new GetWoPostSuccessEvent(respone, mFilter,getTag()));
        } else {
            Object object = getTag();
            if (object != null) {
                if (resultCode == NetConstants.NONE_NETWORK) {
                    ((IPostListener) object).onNoNetwork();
                } else {
                    ((IPostListener) object).onErr(resultCode);
                }
            }
        }
    }

    // ===========================================================
    // inner class
    // ===========================================================
    public static class GetWoPostSuccessEvent extends AbsProtocolEvent {
        private String mResponse;
        private int mFilter;

        public GetWoPostSuccessEvent(String resp, int filter, Object tag) {
            setTag(tag);
            mResponse = resp;
            mFilter = filter;
        }

        public String getResponse() {
            return mResponse;
        }

        public int getFilter() {
            return mFilter;
        }

        public void setFilter(int mFilter) {
            this.mFilter = mFilter;
        }
    }

    /**
     * 获取窝中帖子列表的response
     */
    public static class GetWoPostListResponse {
        private List<PostInfo> posts;
        private WowoInfo wowo;

        public List<PostInfo> getPostInfos() {
            return posts;
        }

        public void setPostInfos(List<PostInfo> postInfos) {
            this.posts = postInfos;
        }

        public WowoInfo getWowo() {
            return wowo;
        }

        public void setWowo(WowoInfo wowo) {
            this.wowo = wowo;
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