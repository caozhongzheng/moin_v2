package com.moinapp.wuliao.modules.cosplay.model;

import com.moinapp.wuliao.commons.modal.BaseImage;
import com.moinapp.wuliao.modules.ipresource.model.MoinPictures;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/6/29.
 * 大咖秀的资源类，需要从服务器获取
 */
public class CosplayResource implements Serializable {
    /**
     * 大咖秀资源的id
     */
    private String id;

    /**
     * 大咖秀资源相关的ipid
     */
    private String ipid;

    /**
     * 大咖秀资源的名称
     */
    private String name;

    /**
     * 大咖秀资源ip相关的缩略图
     */
    private BaseImage icon;

    /**
     * 大咖秀资源ip相关的缩略图的本地下载地址，大咖秀需要用到
     */
    private String iconLocalPath;

    /**
     * 大咖秀资源的图片，下载列表中每一项用到
     */
    private MoinPictures pics;

    /**
     * 大咖秀资源的描述信息
     */
    private String desc;

    /**
     * 大咖秀资源的版本
     */
    private int version;

    /**
     * 大咖秀资源的容量
     */
    private long size;

    /**
     * 大咖秀元素的个数
     */
    private int themeNum;

    /**
     * 大咖秀资源的创建时间
     */
    private int createdAt;

    /**
     * 大咖秀资源的更新时间
     */
    private int updatedAt;

    /**
     * 大咖秀头 身等类型内容对象
     */
    private CosplayThemes themes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIpid() {
        return ipid;
    }

    public void setIpid(String ipid) {
        this.ipid = ipid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BaseImage getIcon() {
        return icon;
    }

    public void setIcon(BaseImage icon) {
        this.icon = icon;
    }

    public MoinPictures getPics() {
        return pics;
    }

    public void setPics(MoinPictures pics) {
        this.pics = pics;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getThemeNum() {
        return themeNum;
    }

    public void setThemeNum(int themeNum) {
        this.themeNum = themeNum;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CosplayThemes getThemes() {
        return themes;
    }

    public void setThemes(CosplayThemes themes) {
        this.themes = themes;
    }

    public String getIconLocalPath() {
        return iconLocalPath;
    }

    public void setIconLocalPath(String iconLocalPath) {
        this.iconLocalPath = iconLocalPath;
    }
}
