package com.moinapp.wuliao.modules.wowo.model;

import com.moinapp.wuliao.commons.modal.BaseImage;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/6/8.
 * 用户在窝窝中浏览，回复，发帖时用到的表情对象
 * 有两种： 一是系统表情 二是用户自定义表情
 */
public class Emoji implements Serializable {
    /**
     * 表情专辑ID
     */
    private String parentid;
    /**
     * 单个表情id， 或大咖秀表情id
     */
    private int id;
    /**
     * 0 系统表情 1 用户自定义表情(大咖秀)*/
    private int type;

    /**
     * 表情图片url
     * @return
     */
    // TODO
    private BaseImage image;

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BaseImage getImage() {
        return image;
    }

    public void setImage(BaseImage image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Emoji{" +
                "parentid='" + parentid + '\'' +
                ", id=" + id +
                ", type=" + type +
                ", image=" + image +
                '}';
    }
}
