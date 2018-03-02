package com.moinapp.wuliao.modules.wowo.model;

import com.moinapp.wuliao.commons.modal.BaseImage;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/6/8.
 * 帖子的作者信息
 */
public class PostAuthor implements Serializable {
    private String id; // 作者的id
    private BaseImage avatar; // 作者的头像信息
    private int level; // 作者的等级
    private String nickname; //作者名字

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BaseImage getAvatar() {
        return avatar;
    }

    public void setAvatar(BaseImage avatar) {
        this.avatar = avatar;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
