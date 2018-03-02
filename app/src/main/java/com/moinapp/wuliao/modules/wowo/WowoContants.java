package com.moinapp.wuliao.modules.wowo;

import com.moinapp.wuliao.commons.info.CommonDefine;

/**
 * Created by liujiancheng on 15/6/8.
 */
public class WowoContants {
    public static final String POST_SHARE_URL = CommonDefine.getBaseShareUrl() + "view/wowo/";

    /**
     * 每次获取到未关注的窝个数是3个
     */
    public static final int WO_LIST_PAGE_SIZE = 3;

    /**
     * 发帖时最大上传图片数
     */
    public static final int WO_POST_MAX_SIZE = 5;

    /**
     * 发帖时最大上传图片数
     */
    public static final String COMMENT_TIMESTAMP_PATTERN = "yyyy.MM.dd";

    /**
     * 窝相关接口服务器基本domain
     */
    public static final String WOWO_BASE_URL = CommonDefine.getBaseUrl() + "wowo/";

    /**
     * 获取已经关注的窝列表请求的url
     */
    public static final String GET_MY_WO_URL = WOWO_BASE_URL + "getmywo";

    /**
     * 获取未关注的窝列表请求的url
     */
    public static final String GET_SUGGEST_WO_URL = WOWO_BASE_URL + "getsuggestwo";

    /**
     * 关注一个窝的请求的url
     */
    public static final String SUBSCRIBE_WO_URL = WOWO_BASE_URL + "subscribe";

    /**
     * 获取推荐的帖子列表的请求的url
     */
    public static final String GET_SUGGEST_POST_URL = WOWO_BASE_URL + "getsuggestpost";

    /**
     * 获取窝里面帖子列表的请求的url
     */
    public static final String GET_WO_POST_URL = WOWO_BASE_URL + "getwopost";

    /**
     * 获指定IP官方窝中最热帖子的请求的url
     */
    public static final String GET_IP_POST_URL = WOWO_BASE_URL + "getippost";

    /**
     * 新发表帖子的请求的url
     */
    public static final String NEW_POST_URL = WOWO_BASE_URL + "newpost";

    /**
     *  回复帖子的请求的url
     */
    public static final String REPLY_POST_URL = WOWO_BASE_URL + "reply";

    /**
     *  获取帖子详情（回复评论）的请求的url
     */
    public static final String GET_POST_URL = WOWO_BASE_URL + "getpost";

    /**
     *  上传图片文件请求的url
     */
    public static final String UPLOAD_IMAGE_URL = WOWO_BASE_URL + "uploadpic";

    /**
     *  获取窝标签的url
     */
    public static final String GET_WO_TAG = WOWO_BASE_URL + "gettag";


    public static final String LAST_ID = "lastid";
    public static final String WO_ID = "woid";
    public static final String POST_ID = "postid";
    public static final String POST_TITLE = "title";
    public static final String POST_CONTENT = "content";
    public static final String POST_TAG = "tag";
    public static final String POST_PICS = "pics";
    public static final String POST_EMOJI = "emoji";
    public static final String POST_REFERRAL = "referral";
    public static final String POST_META = "meta";
    public static final String POST_FILTER = "filter";
    public static final String FROM = "from";
    public static final String UPLOAD_PIC = "pic";
    public static final String IPID = "ipid";
    public static final String WO_ACTION = "action";

    /**
     * 【发帖和回帖时】上传图片最大限制
     */
    public static final int WO_IMAGE_MAX_WIDTH = 1080;
    public static final int WO_IMAGE_MAX_HEIGHT = 1920;
    public static final int WO_IMAGE_MAX_MB = 5*1024*1024;
    public static final int WO_IMAGE_MAX_KB = 5*1024;

}
