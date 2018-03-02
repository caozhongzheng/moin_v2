package com.moinapp.wuliao.modules.ipresource.model;

import com.moinapp.wuliao.commons.modal.PostId;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/8/11.
 */
public class ResourceInfo implements Serializable {
    /**
     * 如果是ip emoji cosplay 外链url都是用id这个字段
     */
    private String postid;
    private int woid;
    private String ipid;
    private String emojiid;
    private String netid;

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public int getWoid() {
        return woid;
    }

    public void setWoid(int woid) {
        this.woid = woid;
    }

    public String getIpid() {
        return ipid;
    }

    public void setIpid(String ipid) {
        this.ipid = ipid;
    }

    public String getEmojiid() {
        return emojiid;
    }

    public void setEmojiid(String emojiid) {
        this.emojiid = emojiid;
    }

    public String getNetid() {
        return netid;
    }

    public void setNetid(String netid) {
        this.netid = netid;
    }
}
