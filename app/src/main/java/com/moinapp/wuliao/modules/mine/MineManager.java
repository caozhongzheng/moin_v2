package com.moinapp.wuliao.modules.mine;

import android.content.Context;

import com.google.gson.Gson;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moduleframework.AbsManager;
import com.moinapp.wuliao.modules.login.LoginUtils;
import com.moinapp.wuliao.modules.mine.listener.CosplayListener;
import com.moinapp.wuliao.modules.mine.listener.FavoriateIPListener;
import com.moinapp.wuliao.modules.mine.listener.MyEmojiListener;
import com.moinapp.wuliao.modules.mine.listener.MyPostListener;
import com.moinapp.wuliao.modules.mine.listener.MyReplyListener;
import com.moinapp.wuliao.modules.mine.listener.UserFeedbackListener;
import com.moinapp.wuliao.modules.mine.listener.UserTypeListener;
import com.moinapp.wuliao.modules.mine.model.PostId;
import com.moinapp.wuliao.modules.mine.network.CreateCosplayProtocal;
import com.moinapp.wuliao.modules.mine.network.DelCosplayProtocal;
import com.moinapp.wuliao.modules.mine.network.DelFavoriateIPProtocal;
import com.moinapp.wuliao.modules.mine.network.DelMyEmojiProtocal;
import com.moinapp.wuliao.modules.mine.network.DelMyPostProtocal;
import com.moinapp.wuliao.modules.mine.network.DelMyReplyProtocal;
import com.moinapp.wuliao.modules.mine.network.GetCosplayProtocal;
import com.moinapp.wuliao.modules.mine.network.GetFavoriateIPProtocal;
import com.moinapp.wuliao.modules.mine.network.GetMyEmojiProtocal;
import com.moinapp.wuliao.modules.mine.network.GetMyPostProtocal;
import com.moinapp.wuliao.modules.mine.network.GetMyReplyProtocal;
import com.moinapp.wuliao.modules.mine.network.GetUserTypeProtocal;
import com.moinapp.wuliao.modules.mine.network.MineService;
import com.moinapp.wuliao.modules.mine.network.UserFeedbackProtocal;
import com.moinapp.wuliao.utils.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liujiancheng on 15/6/23.
 */
public class MineManager extends AbsManager {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final long CACHE_MAX_TIME = 24L * 60 * 60 * 1000;


    // ===========================================================
    // Fields
    // ===========================================================
    private static ILogger MyLog = LoggerFactory.getLogger(MineModule.MODULE_NAME);
    private static MineManager mInstance;
    private Context mContext;
    private Gson mGson = new Gson();

    // ===========================================================
    // Constructors
    // ===========================================================
    private MineManager() {
        mContext = ApplicationContext.getContext();
        init();
    }

    public static synchronized MineManager getInstance() {
        if (mInstance == null) {
            mInstance = new MineManager();
        }

        return mInstance;
    }

    // ===========================================================
    // public methods or interface
    // ===========================================================
    @Override
    public void init() {
        EventBus.getDefault().register(this);
    }

    /**
     * 创建我的大咖秀表情的接口
     * @param icon： 表情缩略图上传后的图片id
     * @param picture： 表情大图上传后的图片id
     * @param ipid： 表情所对应的IP的id
     * @param listener： callback
     */
    public void createMyCosplay(String icon, String picture, String ipid, CosplayListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(MineConstants.COSPLAY_ICON, icon);
        map.put(MineConstants.COSPLAY_PICTURE, picture);
        map.put(MineConstants.COSPLAY_IPID, ipid);
        new MineService().createCosplayService(MineConstants.CREATE_COSPLAY_URL, map, listener);
    }

    /**
     * 获取我的大咖秀表情的接口
     * @param listener： callback
     */
    public void getMyCosplay(CosplayListener listener) {
        new MineService().getCosplayService(MineConstants.GET_COSPLAY_URL, listener);
    }

    /**
     * 删除我的大咖秀表情的接口
     * @param cosplayList： 要删除的大咖秀表情id集合
     * @param listener： callback
     */
    public void delMyCosplay(List<String> cosplayList, CosplayListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(MineConstants.COSPLAY_ID, StringUtil.list2String(cosplayList));
        new MineService().delCosplayService(MineConstants.DEL_COSPLAY_URL, map, listener);
    }

    /**
     * 获取我的系统表情的接口
     * @param listener： callback
     */
    public void getMyEmoji(MyEmojiListener listener) {
        new MineService().getMyEmojiService(MineConstants.GET_EMOJI_URL, listener);
    }

    /**
     * 删除我的系统表情的接口
     * @param emojiList： 要删除的系统表情id集合
     * @param listener： callback
     */
    public void delMyEmoji(List<String> emojiList, MyEmojiListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(MineConstants.EMOJI_ID, StringUtil.list2String(emojiList));
        new MineService().delMyemojiService(MineConstants.DEL_EMOJI_URL, map, listener);
    }

    /**
     * 获取我收藏IP的接口
     * @param listener： callback
     */
    public void getFavoriateIP(FavoriateIPListener listener) {
        new MineService().getFavoriateIPService(MineConstants.GET_FAVORIATE_IP, listener);
    }

    /**
     * 删除我收藏IP的接口
     * @param ipList： 要删除的收藏IPid集合
     * @param listener： callback
     */
    public void delFavoriateIP(List<String> ipList, FavoriateIPListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(MineConstants.IP_ID, StringUtil.list2String(ipList));
        new MineService().delFavoriateIPService(MineConstants.DEL_FAVORIATE_IP, map, listener);
    }

    /**
     * 获取我发表的帖子的接口
     * @param listener： callback
     */
    public void getMyPost(MyPostListener listener) {
        new MineService().getMyPostService(MineConstants.GET_MY_POST, listener);
    }

    /**
     * 删除我发表帖子的接口
     * @param postIdList： 要删除的帖子id集合
     * @param listener： callback
     */
    public void delMyPost(List<PostId> postIdList, MyPostListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(MineConstants.POST_ID, mGson.toJson(postIdList));
        new MineService().delMyPostService(MineConstants.DEL_MY_POST, map, listener);
    }

    /**
     * 获取我评论（回复）的帖子的接口
     * @param listener： callback
     */
    public void getMyReply(MyReplyListener listener) {
        new MineService().getMyReplyService(MineConstants.GET_MY_REPLY, listener);
    }

    /**
     * 删除我评论（回复）的帖子的接口
     * @param postIdList： 要删除的帖子id集合
     * @param listener： callback
     */
    public void delMyReply(List<PostId> postIdList, MyReplyListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(MineConstants.POST_ID, mGson.toJson(postIdList));
        new MineService().delMyReplyService(MineConstants.DEL_MY_REPLY, map, listener);
    }

    /**
     * 获取用户登陆类型的接口
     * @param listener： callback
     */
    public void getUserType(UserTypeListener listener) {
        new MineService().getUserTypeService(MineConstants.GET_LOGIN_TYPE, listener);
    }

    /**
     * 用户反馈的接口
     * @param content： 反馈内容
     * @param contact： 联系方式
     * @param listener： callback
     */
    public void userFeedback(String content, String contact, UserFeedbackListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(MineConstants.DEVICE_INFO, LoginUtils.getDeviceInfo());
        map.put(MineConstants.APP_INFO, LoginUtils.getAppInfo());
        map.put(MineConstants.FEEDBACK_CONTENT, content);
        map.put(MineConstants.FEEDBACK_CONTACT, contact);
        map.put(MineConstants.LOGTIME, String.valueOf(System.currentTimeMillis()));
        new MineService().userFeedbackService(MineConstants.USER_FEEDBACK, map, listener);
    }

    // ===========================================================
    // 获取服务器接口成功后的回调
    // ===========================================================
    /**
     * 接收到服务器返回创建我的大咖秀表情结果
     * @param event
     */
    public void onEvent(CreateCosplayProtocal.createCosplaySuccessEvent event) {
        String response = event.getResponse();
        CreateCosplayProtocal.createCosplayResponse res = mGson.fromJson(response, CreateCosplayProtocal.createCosplayResponse.class);
        if (res != null) {
            ((CosplayListener)event.getTag()).createCosplaySucc(res.getResult(), res.getId());
        } else {
            ((CosplayListener)event.getTag()).onErr(res.getError());
        }
    }

    /**
     * 接收到服务器返回的我的大咖秀表情列表
     * @param event
     */
    public void onEvent(GetCosplayProtocal.GetCosplaySuccessEvent event) {
        String response = event.getResponse();
        GetCosplayProtocal.GetCosplayResponse res = mGson.fromJson(response, GetCosplayProtocal.GetCosplayResponse.class);
        if (res != null) {
            ((CosplayListener)event.getTag()).getCosplaySucc(res.getCosplaylist());
        } else {
            ((CosplayListener)event.getTag()).onErr(res.getError());
        }
    }

    /**
     * 接收到服务器返回的删除大咖秀表情的结果
     * @param event
     */
    public void onEvent(DelCosplayProtocal.DelCosplaySuccessEvent event) {
        String response = event.getResponse();
        DelCosplayProtocal.DelCosplayResponse res = mGson.fromJson(response, DelCosplayProtocal.DelCosplayResponse.class);
        if (res != null) {
            ((CosplayListener)event.getTag()).delCosplaySucc(res.getResult());
        } else {
            ((CosplayListener)event.getTag()).onErr(res.getError());
        }
    }

    /**
     * 接收到服务器返回的我的系统表情列表
     * @param event
     */
    public void onEvent(GetMyEmojiProtocal.GetMyEmojiSuccessEvent event) {
        String response = event.getResponse();
        GetMyEmojiProtocal.GetMyEmojiResponse res = mGson.fromJson(response, GetMyEmojiProtocal.GetMyEmojiResponse.class);
        if (res != null) {
            ((MyEmojiListener)event.getTag()).getMyEmojiSucc(res.getEmojilist());
        } else {
            ((MyEmojiListener)event.getTag()).onErr(res.getError());
        }
    }

    /**
     * 接收到服务器返回的删除系统表情的结果
     * @param event
     */
    public void onEvent(DelMyEmojiProtocal.DelMyEmojiSuccessEvent event) {
        String response = event.getResponse();
        DelMyEmojiProtocal.DelMyEmojiResponse res = mGson.fromJson(response, DelMyEmojiProtocal.DelMyEmojiResponse.class);
        if (res != null) {
            ((MyEmojiListener)event.getTag()).delEmojiSucc(res.getResult());
        } else {
            ((MyEmojiListener)event.getTag()).onErr(res.getError());
        }
    }

    /**
     * 接收到服务器返回的我收藏的IP列表
     * @param event
     */
    public void onEvent(GetFavoriateIPProtocal.GetFavoriateIPSuccessEvent event) {
        String response = event.getResponse();
        GetFavoriateIPProtocal.GetFavoriateIPResponse res = mGson.fromJson(response, GetFavoriateIPProtocal.GetFavoriateIPResponse.class);
        if (res != null) {
            ((FavoriateIPListener)event.getTag()).getFavoriateIPSucc(res.getIplist());
        } else {
            ((FavoriateIPListener)event.getTag()).onErr(res.getError());
        }
    }

    /**
     * 接收到服务器返回的删除收藏的结果
     * @param event
     */
    public void onEvent(DelFavoriateIPProtocal.DelFavoriateIPSuccessEvent event) {
        String response = event.getResponse();
        DelFavoriateIPProtocal.DelFavoriateIPResponse res = mGson.fromJson(response, DelFavoriateIPProtocal.DelFavoriateIPResponse.class);
        if (res != null) {
            ((FavoriateIPListener)event.getTag()).delFavoriateIPSucc(res.getResult());
        } else {
            ((FavoriateIPListener)event.getTag()).onErr(res.getError());
        }
    }

    /**
     * 接收到服务器返回的我发表的帖子列表
     * @param event
     */
    public void onEvent(GetMyPostProtocal.GetMyPostSuccessEvent event) {
        String response = event.getResponse();
        GetMyPostProtocal.GetMyPostResponse res = mGson.fromJson(response, GetMyPostProtocal.GetMyPostResponse.class);
        if (res != null) {
            ((MyPostListener)event.getTag()).getMyPostSucc(res.getPostlist());
        } else {
            ((MyPostListener)event.getTag()).onErr(res.getError());
        }
    }

    /**
     * 接收到服务器返回的删除我发表的帖子结果
     * @param event
     */
    public void onEvent(DelMyPostProtocal.DelMyPostSuccessEvent event) {
        String response = event.getResponse();
        DelMyPostProtocal.DelMyPostResponse res = mGson.fromJson(response, DelMyPostProtocal.DelMyPostResponse.class);
        if (res != null) {
            ((MyPostListener)event.getTag()).delMyPostSucc(res.getResult());
        } else {
            ((MyPostListener)event.getTag()).onErr(res.getError());
        }
    }

    /**
     * 接收到服务器返回的我评论的帖子列表
     * @param event
     */
    public void onEvent(GetMyReplyProtocal.GetMyReplySuccessEvent event) {
        String response = event.getResponse();
        GetMyReplyProtocal.GetMyReplyResponse res = mGson.fromJson(response, GetMyReplyProtocal.GetMyReplyResponse.class);
        if (res != null) {
            ((MyReplyListener)event.getTag()).getMyCommentSucc(res.getReplylist());
        } else {
            ((MyReplyListener)event.getTag()).onErr(res.getError());
        }
    }

    /**
     * 接收到服务器返回的删除我评论的帖子结果
     * @param event
     */
    public void onEvent(DelMyReplyProtocal.DelMyReplySuccessEvent event) {
        String response = event.getResponse();
        DelMyReplyProtocal.DelMyReplyResponse res = mGson.fromJson(response, DelMyReplyProtocal.DelMyReplyResponse.class);
        if (res != null) {
            ((MyReplyListener)event.getTag()).delMyCommentSucc(res.getResult());
        } else {
            ((MyReplyListener)event.getTag()).onErr(res.getError());
        }
    }

    /**
     * 接收到服务器返回的用户登陆类型信息
     * @param event
     */
    public void onEvent(GetUserTypeProtocal.GetUserTypeSuccessEvent event) {
        String response = event.getResponse();
        GetUserTypeProtocal.GetUserTypeResponse res = mGson.fromJson(response, GetUserTypeProtocal.GetUserTypeResponse.class);
        if (res != null) {
            ((UserTypeListener)event.getTag()).getUserTypeSucc(res.getType(), res.getNamelist());
        } else {
            ((UserTypeListener)event.getTag()).onErr(res.getError());
        }
    }

    /**
     * 接收到服务器返回的用户反馈的结果
     * @param event
     */
    public void onEvent(UserFeedbackProtocal.UserFeedbackSuccessEvent event) {
        String response = event.getResponse();
        UserFeedbackProtocal.UserFeedbackResponse res = mGson.fromJson(response, UserFeedbackProtocal.UserFeedbackResponse.class);
        if (res != null) {
            ((UserFeedbackListener)event.getTag()).userFeedbackSucc(res.getResult());
        } else {
            ((UserFeedbackListener)event.getTag()).onErr(res.getError());
        }
    }
}
