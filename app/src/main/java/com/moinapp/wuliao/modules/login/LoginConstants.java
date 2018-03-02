package com.moinapp.wuliao.modules.login;

import com.moinapp.wuliao.commons.info.CommonDefine;

/**
 * Created by liujiancheng on 15/5/7.
 */
public class LoginConstants {
    /**
     * 登录服务器基本domain
     */
    public static final String LOGIN_BASE_URL = CommonDefine.getBaseUrl() + "user/";

    /**
     * 用户登录请求的url
     */
    public static final String LOGIN_URL = LOGIN_BASE_URL + "login";

    /**
     * 用户退出登录请求的url
     */
    public static final String LOGOUT_URL = LOGIN_BASE_URL + "logout";

    /**
     * 用户更新数据的请求url
     */
    public static final String LOGIN_UPDATE_URL = LOGIN_BASE_URL + "updatebasic/";

    /**
     * 用户注册的请求url
     */
    public static final String LOGIN_REGISTER_URL = LOGIN_BASE_URL + "register";

    /**
     * 检查电话号码唯一性的请求url
     */
    public static final String LOGIN_CHECK_PHONE_URL = LOGIN_BASE_URL + "checkphone";

    /**
     * 检查邮箱唯一性的请求url
     */
    public static final String LOGIN_CHECK_EMAIL_URL = LOGIN_BASE_URL + "checkemail";

    /**
     * 获取短信验证码的请求url
     */
    public static final String LOGIN_GET_SMSCODE_URL = LOGIN_BASE_URL + "sentsmscode";

    /**
     * 获取邮箱验证码的请求url
     */
    public static final String LOGIN_GET_EMAILCODE_URL = LOGIN_BASE_URL + "sentemailcode";

    /**
     * 找回密码的请求url
     */
    public static final String LOGIN_RETRIEVE_PASSWORD_URL = LOGIN_BASE_URL + "findpassword";

    /**
     * 修改密码的请求url
     */
    public static final String LOGIN_UPDATE_PASSWORD_URL = LOGIN_BASE_URL + "updatepassword";

    /**
     * 上传图像的请求url
     */
    public static final String LOGIN_UPLOAD_URL = LOGIN_BASE_URL + "uploadavatar";

    /**
     * 获取用户信息的请求url
     */
    public static final String LOGIN_GET_INFO_URL = LOGIN_BASE_URL + "getinfo";


    /**
     * 第三方登录从ui上传来的bundle key
     */
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String OPEN_ID = "openid";
    public static final String EXPIRE_IN = "expires_in";
    public static final String REFRESH_TOKEN_EXPIRES = "refresh_token_expires";
    public static final String UNION_ID = "unionid";

    /**
     * 同服务器交互时的参数key
     */
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String NAME = "name";
    public static final String PASSWORD = "password";
    public static final String TOKEN = "token";
    public static final String OPENID = "openid";
    public static final String UID = "uid";
    public static final String MOIN_PASSPORT = "passport";
    public static final String IMEI = "imei";
    public static final String ERROR = "error";
    public static final String MD5 = "md5";
    public static final String SMSCODE = "smscode";
    public static final String EMAILCODE = "emailcode";
    public static final String DEVICEINFO = "device";
    public static final String APPINFO = "app";
    public static final String CODE = "code";
    public static final String NICKNAME= "nickname";
    public static final String SEX = "sex";
    public static final String AGES = "ages";
    public static final String STARS = "stars";
    public static final String AVATAR = "avatar";
    public static final String LOCATION = "location";
    public static final String SIGNATURE = "signature";
    /**
     * DeviceInfo的key
     */
    public static final String OS_NAME = "osName";
    public static final String OS_VERSION = "osVersion";
    public static final String SCREEN_WIDTH = "width";
    public static final String SCREEN_HEIGHT = "height";
    public static final String NETWORK = "net";
    public static final String LANGUAGE = "language";
    public static final String COUNTRY = "country";
    public static final String IMSI = "imsi";
    public static final String MAC = "mac";

    /**
     * AppInfo的key
     */
    public static final String EDITION = "edition";
    public static final String CHANNEL_ID = "channel";

    /**
     * 跳转到登陆界面的来源
     */
    public static final int IP_DETAIL = 0; //IP详情页
    public static final int EMOJI_RESOURCE = 1; //表情专题页
    public static final int FRAGMENT_WOWO = 2; //窝列表
    public static final int WOWO_RULE = 3; //窝规则
    public static final int WO_POST_LIST_ATTENTION = 4;//窝贴列表页关注窝
    public static final int WO_POST_LIST_NEWPOST= 5;//窝贴列表页发帖
    public static final int POST_DETAIL_COMMENT = 6;//帖子详情页评论
    public static final int POST_DETAIL_REPLY = 7;//帖子详情页回复
    public static final int FRAGMENT_MINE = 8;//我的
    public static final int MINE_EMOJI = 9;//我的表情
    public static final int MINE_IP = 10;//我的IP
    public static final int MINE_WOWO = 11;//我的WOWO
    public static final int MINE_SETTINGS = 12;//我的设置
    public static final int MINE_AVATAR = 13;//我的头像
}
