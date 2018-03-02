package com.moinapp.wuliao.modules.ipresource.model;

import com.moinapp.wuliao.commons.modal.BaseVideo;

/**
 * Created by liujiancheng on 15/5/12.
 * ip详情用到的片花对象: 片花和剧照的混排，用属性media区分：1代表图像，2代表视频
 */
public class MoinClip extends BaseVideo {

    private int media;

    public MoinClip(String uri, String imgUri) {
        super.setUri(uri);
        super.setImageuri(imgUri);
    }

    public int getMedia() {
        return media;
    }

    public void setMedia(int media) {
        this.media = media;
    }

    @Override
    public String toString() {
        return "MoinClip{" +
                "media=" + media +
                ", video=" + super.toString() +
                '}';
    }
}
