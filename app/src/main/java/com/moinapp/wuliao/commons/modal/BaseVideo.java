package com.moinapp.wuliao.commons.modal;

/**
 * Created by liujiancheng on 15/5/13.
 * 基本的视频对象，包括url 宽高等等
 */
public class BaseVideo {
    /**
     * 片花播放链接，剧照用的静态图
     * */
    private String uri;
    private int width;
    private int height;
    private String name;
    /**
     *  片花视频的第一帧的静态图
     * */
    private String imageuri;

    public void setUri(String uri) { this.uri = uri; }
    public String getUri() { return this.uri; }

    public void setWidth(int width) { this.width = width; }
    public int getWidth() { return  this.width; }

    public void setHeight(int height) { this.height = height; }
    public int getHeight() { return this.height; }

    public void setName(String name) { this.name = name; }
    public String getName() { return this.name; }

    public void setImageuri(String imageuri) { this.imageuri = imageuri; }
    public String getImageuri() { return this.imageuri; }

    @Override
    public String toString() {
        return "BaseVideo{" +
                "uri='" + uri + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", name='" + name + '\'' +
                ", imageuri='" + imageuri + '\'' +
                '}';
    }
}
