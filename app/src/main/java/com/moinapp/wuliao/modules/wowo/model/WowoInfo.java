package com.moinapp.wuliao.modules.wowo.model;

import com.moinapp.wuliao.commons.modal.BaseImage;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liujiancheng on 15/6/8.
 * 窝对象的模型类
 */
public class WowoInfo implements Serializable {
    private int id;         // 窝的id
    private String name;    // 窝的名字
    private String intro;   // 窝的介绍
    private String ruleIntro;//窝规则
    private long userCount;  // 窝的用户数
    private long postCount;  // 窝的帖子数
    private List<String> tags;// 窝的tag
    private BaseImage icon; //窝的icon
    private boolean isFavorite;// 窝是否已经被关注

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getRuleIntro() {
        return ruleIntro;
    }

    public void setRuleIntro(String ruleIntro) {
        this.ruleIntro = ruleIntro;
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public long getPostCount() {
        return postCount;
    }

    public void setPostCount(long postCount) {
        this.postCount = postCount;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public BaseImage getIcon() {
        return icon;
    }

    public void setIcon(BaseImage icon) {
        this.icon = icon;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
}
