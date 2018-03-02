package com.moinapp.wuliao.modules.ipresource;

import com.moinapp.wuliao.commons.info.CommonDefine;

/**
 * Created by liujiancheng on 15/5/12.
 */
public class IPResourceConstants {
    public static final String IP_SHARE_URL = CommonDefine.getBaseShareUrl() + "view/ipresource/";
    public static final String EMOJI_SHARE_URL = CommonDefine.getBaseShareUrl() + "view/emoji/";
    /**
     * 登录服务器基本domain
     */
    public static final String IP_BASE_URL = CommonDefine.getBaseUrl() + "ipresource/";

    /**
     * 获取banner请求的url
     */
    public static final String GET_BANNER_URL = IP_BASE_URL + "getbanner";

    /**
     *  获取热门标签请求的url
     */
    public static final String GET_HOT_TAG_URL = CommonDefine.getBaseUrl() + "user/gethottag";

    /**
     *  标签搜索请求的url
     */
    public static final String SEARCH_TAG_URL = CommonDefine.getBaseUrl() + "user/searchtag";

    /**
     * 获取ip列表请求的url
     */
    public static final String GET_IPLIST_URL = IP_BASE_URL + "getlist";

    /**
     * 获取ip详情请求的url
     */
    public static final String GET_IP_DETAIL = IP_BASE_URL + "getip";

    /**
     * 获取热门ip请求的url
     */
    public static final String GET_HOT_IP = IP_BASE_URL + "gethotip";

    /**
     * 获取ip的表情专题请求的url
     */
    public static final String GET_EMOJI = IP_BASE_URL + "getemoji";

    /**
     * 下载表情专题请求的url
     */
    public static final String DOWNLOAD_IP_EMOJI = CommonDefine.getBaseUrl() + "user/downloademoji";

    /**
     * 收藏ip请求的url
     */
    public static final String FAVORIATE_IP = CommonDefine.getBaseUrl() + "user/addfavoriteip";

    /**
     * 给资源点赞请求的url
     */
    public static final String LIKE_RESOURCE = CommonDefine.getBaseUrl() + "user/like";

    /**
     * 处理获取ip资源时失败的定义
     */
    public static final int NETWORK_ERROR = -99;

    /**
     * 请求IP资源的参数key
     */

//    电影、电视、综艺、其他
    public static final int TYPE_NONE = 0;
    public static final int TYPE_MV = TYPE_NONE + 1;
    public static final int TYPE_TV = TYPE_MV + 1;
    public static final int TYPE_ZY = TYPE_TV + 1;

    public static final String IP_RELEASE_DATE_FORMAT = "yyyy.MM.dd";
    public static final String IP_ID = "ipid";
    public static final String SINGLE_EMOJI_ID = "emojiid";
    public static final String LAST_ID = "lastid";
    public static final String SORT_BY = "sortby";
    public static final String EMOJI_ID = "id";
    public static final String FAVORIATE_IP_ID = "id";
    public static final String LIKE_ID = "id";
    public static final String LIKE_TYPE ="type";
    public static final String ACTION = "action";
    public static final String TAG_TYPE ="type";
    public static final String SEARCH_CONTENT ="tags";
    /**
     * 分享到的平台
     */
    public static final int PLATFORM_WEIXIN = 1;
    public static final int PLATFORM_WEIXIN_FRIENDS = 2;
    public static final int PLATFORM_QQ = 3;
    public static final int PLATFORM_QZONE = 4;
    public static final int PLATFORM_WOSAI = 5;

    public static final String JPG_EXTENSION = ".j";
    public static final String GIF_EXTENSION = ".g";
    public static final String WEBP_EXTENSION = ".webp";
    public static final String COMPRESS_EXTENSION = ".jpg";
}
