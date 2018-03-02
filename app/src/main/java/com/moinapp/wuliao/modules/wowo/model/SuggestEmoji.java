package com.moinapp.wuliao.modules.wowo.model;

import com.moinapp.wuliao.commons.modal.BaseImage;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/6/10.
 * IP首页中的窝窝TOP帖子列表中需要的推荐表情对象
 */
public class SuggestEmoji implements Serializable {
    private String id; //emoji专题的id
    private String ipid;//ipid
    private BaseImage icon; //ip的缩略图
    private String name; //ip名字
    private long emojiNum; // 表情个数

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

    public long getEmojiNum() {
        return emojiNum;
    }

    public void setEmojiNum(long emojiNum) {
        this.emojiNum = emojiNum;
    }
}
