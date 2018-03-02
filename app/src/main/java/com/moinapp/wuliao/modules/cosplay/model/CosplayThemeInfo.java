package com.moinapp.wuliao.modules.cosplay.model;

import com.moinapp.wuliao.commons.modal.BaseImage;
import com.moinapp.wuliao.commons.modal.BaseResource;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/6/29.
 * 大咖秀资源的每一个元素模型类
 */
public class CosplayThemeInfo implements Serializable {
    /**
     * 素材展示名称
     */
    private String name;

    /**
     * 素材图标
     */
    private BaseImage icon;

    /**
     * 网络上的资源文件，zip对象
     */
    private BaseResource resource;

    /**
     * 资源文件的本地下载地址
     */
    private String localPath;

    /**
     * 动画类型
     */
    private int type;

    /**
     * 帧数
     */
    private int frameCount;

    /**
     * 每帧之间的停留时间，整数，单位毫秒
     */
    private int timeSpace;

    /**
     * 客户端自己定义的子item标识，比如head0 head1 body0 body1之类的
     */
    private String subItem;

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

    public BaseResource getResource() {
        return resource;
    }

    public void setResource(BaseResource resource) {
        this.resource = resource;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    public int getTimeSpace() {
        return timeSpace;
    }

    public void setTimeSpace(int timeSpace) {
        this.timeSpace = timeSpace;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
}
