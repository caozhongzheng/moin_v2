package com.moinapp.wuliao.commons.modal;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/8/11.
 */
public class PostId implements Serializable {
    private int woid;
    private String postid;

    public int getWoid() {
        return woid;
    }

    public void setWoid(int woid) {
        this.woid = woid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }
}
