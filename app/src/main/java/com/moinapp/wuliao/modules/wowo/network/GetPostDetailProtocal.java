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
import com.moinapp.wuliao.modules.wowo.model.CommentInfo;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;

import java.util.List;
import java.util.Map;

/**
* Created by liujiancheng on 15/6/9.
* 获取帖子详情的服务器接口协议
*/
public class GetPostDetailProtocal extends HttpPostProtocal implements HttpObserver {
    private static ILogger MyLog = LoggerFactory.getLogger("GetPostDetailProtocal");

    // ===========================================================
    // Constructors
    // ===========================================================
    public GetPostDetailProtocal(String url, Map data, Object tag) {
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
        MyLog.i("GetPostDetailProtocal.onHttpResult() resultCode=" + resultCode + ", response1=" + respone);
        if (resultCode == AbsHttpOperation.ERROR_NONE) {
            EventBus.getDefault().post(new GetPostDetailSuccessEvent(respone, getTag()));
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
    public static class GetPostDetailSuccessEvent extends AbsProtocolEvent {
        private String mResponse;

        public GetPostDetailSuccessEvent(String resp, Object tag) {
            setTag(tag);
            mResponse = resp;
        }

        public String getResponse() {
            return mResponse;
        }
    }

    /**
     * 获取帖子详情的response
     */
    public static class GetPostDetailResponse {
        private PostInfo post;
        private List<CommentInfo> comments;

        public List<CommentInfo> getComments() {
            return comments;
        }

        public void setComments(List<CommentInfo> comments) {
            this.comments = comments;
        }

        public PostInfo getPost() {
            return post;
        }

        public void setPost(PostInfo post) {
            this.post = post;
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
