package com.moinapp.wuliao.modules.mine.model;

import com.moinapp.wuliao.commons.modal.BaseImage;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/6/24.
 * 大咖秀表情
 */
public class EmojiCosPlay implements Serializable {
    /**
     * 大咖秀的id
     */
    private String id;

    /**
     * 大咖秀作者的uid
     */
    private String uid;

    /**
     * 大咖秀表情的缩略图
     */
    private BaseImage icon;

    /**
     * 大咖秀表情的大图
     */
    private BaseImage picture;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public BaseImage getIcon() {
        return icon;
    }

    public void setIcon(BaseImage icon) {
        this.icon = icon;
    }

    public BaseImage getPicture() {
        return picture;
    }

    public void setPicture(BaseImage picture) {
        this.picture = picture;
    }
}
