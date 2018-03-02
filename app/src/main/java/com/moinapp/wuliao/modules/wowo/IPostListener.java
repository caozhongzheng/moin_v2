package com.moinapp.wuliao.modules.wowo;

import com.moinapp.wuliao.commons.net.NetworkingListener;
import com.moinapp.wuliao.modules.wowo.model.CommentInfo;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;
import com.moinapp.wuliao.modules.wowo.model.WowoInfo;

import java.util.List;

/**
 * Created by liujiancheng on 15/6/8.
 */
public interface IPostListener extends NetworkingListener {
    /**
     * 获取推荐帖子列表成功的回调，column目前没有用到，暂时保留，以后没用的话再去掉
     */
    public void onGetPostListSucc(List<PostInfo> postInfoList, int column);

    /**
     * 获取窝中帖子列表成功的回调, woinfo 窝的基本信息，column（0，1，2）表示请求的是全部贴，精华贴还是表情贴
     */
    public void onGetWoPostListSucc(List<PostInfo> postInfoList, WowoInfo woinfo, int column);

    /**
     * 获取ip相关帖子列表成功的回调
     */
    public void onGetIPPostSucc(List<PostInfo> postInfoList);

    /**
     * 发帖成功的回调
     */
    public void onNewPostSucc(int woid, String postid);

    /**
     * 回帖成功的回调
     */
    public void onReplyPostSucc(int commentid);

    /**
     * 获取帖子信息的回调
     */
    public void onGetPostSucc(PostInfo postInfo, List<CommentInfo> commentInfos);

    /**
     * 上传图片成功的回调
     */
    public void onUploadImageSucc(String picid);

}
