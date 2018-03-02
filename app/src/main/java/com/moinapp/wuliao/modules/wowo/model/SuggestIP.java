package com.moinapp.wuliao.modules.wowo.model;

import com.moinapp.wuliao.commons.modal.BaseImage;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/6/10.
 * IP首页中的窝窝TOP帖子列表中需要的推荐IP对象
 */
public class SuggestIP implements Serializable {
    private String id; //ip id
    private BaseImage icon; //ip的缩略图
    private String name; //ip名字
    private int type; //ip类型
    private long releaseDate; //ip的发布日期

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BaseImage getIcon() {
        return icon;
    }

    public void setIcon(BaseImage icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(long releaseDate) {
        this.releaseDate = releaseDate;
    }
}
