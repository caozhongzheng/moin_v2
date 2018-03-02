package com.moinapp.wuliao.commons.moinsocialcenter;

import android.app.Activity;

/**
 * Created by liujiancheng on 15/6/2.
 */
public class MoinSocialFactory {
    private static ISocial sMock;

    public static void setMock(ISocial mock) {
        sMock = mock;
    }

    public static  ISocial getService(Activity activity) {
        if (sMock != null) {
            return sMock;
        }
        return UmengSocialCenter.getInstance(activity);
    }
}
