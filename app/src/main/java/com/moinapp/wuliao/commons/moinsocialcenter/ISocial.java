package com.moinapp.wuliao.commons.moinsocialcenter;

import android.app.Activity;

/**
 * Created by liujiancheng on 15/6/2.
 */
public interface ISocial {
    public void doAuthorize(int platform, DoAuthorizeListener listner);
    public void getUserInfo(int platform, GetThirdUserInfoListener listener);
    public void openShare(Activity activity);//默认分享面板
    public void postShare();//自定义分享面板
    public void postShare(int platform);//传入要分享的平台id
    public void setShareContent(String title, String content, String targetUrl, String imageUrl);
}
