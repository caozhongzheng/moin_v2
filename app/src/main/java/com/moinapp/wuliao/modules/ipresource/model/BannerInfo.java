package com.moinapp.wuliao.modules.ipresource.model;

import com.moinapp.wuliao.commons.modal.BaseImage;
import com.moinapp.wuliao.commons.modal.PostId;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/8/11.
 */
public class BannerInfo implements Serializable {
    public static final int TYPE_UNKNOWN = -1;
    /**IP 1*/
    public static final int TYPE_IP = 1;
    /**2 表情*/
    public static final int TYPE_EMOJI = TYPE_IP + 1;
    /**3 大咖秀*/
    public static final int TYPE_COSPLAY = TYPE_EMOJI + 1;
    /**4 帖子*/
    public static final int TYPE_POST = TYPE_COSPLAY + 1;
    /**5 窝窝*/
    public static final int TYPE_WOWO = TYPE_POST + 1;
    /**6 外链*/
    public static final int TYPE_EXTERANL_LINK = TYPE_WOWO + 1;

    /**
     * banner的跳转类型
     * 1 IP资源 2 表情专辑 3 大咖秀 4 帖子 5 窝窝 6 外部链接
     */
    private int type;

    /**
     * banner图片
     */
    private BaseImage image;

    private String title;

    private String desc;

    /**
     * 资源的ID信息
     */
    private ResourceInfo resource;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ResourceInfo getResource() {
        return resource;
    }

    public void setResource(ResourceInfo resource) {
        this.resource = resource;
    }
}
