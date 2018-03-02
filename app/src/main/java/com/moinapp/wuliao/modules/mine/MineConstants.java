package com.moinapp.wuliao.modules.mine;

import com.moinapp.wuliao.commons.info.CommonDefine;

/**
 * Created by liujiancheng on 15/6/23.
 * 我的相关常量定义
 */
public class MineConstants {
    /**
     * 我的模块：服务器基本domain
     */
    public static final String MINE_BASE_URL = CommonDefine.getBaseUrl() + "user/";

    /**
     * 创建我的大咖秀表情接口的url
     */
    public static final String CREATE_COSPLAY_URL = MINE_BASE_URL + "createcosplay";

    /**
     * 获取我的大咖秀表情接口的url
     */
    public static final String GET_COSPLAY_URL = MINE_BASE_URL + "getcosplay";

    /**
     * 删除我的大咖秀表情接口的url
     */
    public static final String DEL_COSPLAY_URL = MINE_BASE_URL + "deletecosplay";

    /**
     * 获取我的系统表情接口的url
     */
    public static final String GET_EMOJI_URL = MINE_BASE_URL + "getdownloademoji";

    /**
     * 删除我的系统表情接口的url
     */
    public static final String DEL_EMOJI_URL = MINE_BASE_URL + "deleteEmoji";

    /**
     * 获取我关注的ip接口的url
     */
    public static final String GET_FAVORIATE_IP = MINE_BASE_URL + "getfavoriteip";

    /**
     * 删除我关注的ip接口的url
     */
    public static final String DEL_FAVORIATE_IP = MINE_BASE_URL + "deletefavoriteip";

    /**
     * 获取我发的帖子接口的url
     */
    public static final String GET_MY_POST = MINE_BASE_URL + "getposts";

    /**
     * 删除我发的帖子接口的url
     */
    public static final String DEL_MY_POST = MINE_BASE_URL + "deleteposts";

    /**
     * 获取我参与评论帖子接口的url
     */
    public static final String GET_MY_REPLY = MINE_BASE_URL + "getreply";

    /**
     * 删除我参与的帖子接口的url
     */
    public static final String DEL_MY_REPLY = MINE_BASE_URL + "deletereply";

    /**
     * 得到用户登陆的类型接口的url
     */
    public static final String GET_LOGIN_TYPE = MINE_BASE_URL + "gettype";

    /**
     * 用户意见反馈接口的url
     */
    public static final String USER_FEEDBACK = MINE_BASE_URL + "feedback";

    public static final String COSPLAY_ICON = "icon";
    public static final String COSPLAY_PICTURE = "picture";
    public static final String COSPLAY_IPID = "ipid";
    public static final String COSPLAY_ID = "ids";
    public static final String EMOJI_ID = "ids";
    public static final String IP_ID = "ids";
    public static final String POST_ID = "ids";
    public static final String FEEDBACK_CONTENT = "content";
    public static final String FEEDBACK_CONTACT = "contact";
    public static final String DEVICE_INFO = "device";
    public static final String APP_INFO = "app";
    public static final String LOGTIME = "logtime";

    public static final int MODE_NONE = 0;
    public static final int MODE_EDIT = 1;
}
