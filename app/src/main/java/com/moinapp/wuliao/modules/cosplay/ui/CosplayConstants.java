package com.moinapp.wuliao.modules.cosplay.ui;

import android.os.Environment;

/**
 * Created by liujiancheng on 15/6/30.
 */
public class CosplayConstants {
    /**
     * 　获取大咖秀资源的服务器基本domain
     */
    public static final String GET_COSPLAY_RES_URL = "https://dev.mo-image.com/ipresource/getcosplay";

    /**
     * ip的id
     */
    public static final String IP_ID = "ipid";

    /**
     * 上次获取到的最后一个大咖秀资源的id
     */
    public static final String LAST_ID = "lastid";

    /**
     * 大咖秀资源的本地地址
     */
    public static String COSPLAY_RES_FOLDER = "Moin/CosplayRes/";
    public static String COSPLAY_DOWNLOAD_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + COSPLAY_RES_FOLDER;

    /**
     * 用户创建的大咖秀表情地址
     */
    public static String COSPLAY_FOLDER = "Moin/Cosplay/";
    public static String COSPLAY_EMOJI_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + COSPLAY_FOLDER;
}
