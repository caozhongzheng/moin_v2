package com.moinapp.wuliao.modules.login.model;

import com.moinapp.wuliao.commons.modal.BaseImage;

/**
 * Created by liujiancheng on 15/5/5.
 * 用户信息模型类，用于和服务器通讯接口的json交互
 */
public class UserInfo {
    private String name;
    private String phone;
    private String id;
    private String password;
    private String email;
    private String sex;//注意这是字符串，ui要控制，比如“male/femail/unknown” 或者“男／女／未知”
    private String nickname;
    private String ages;//注意也是字符串 00后就是00 75后就是75
    private int stars = -1;//12个星座，存成1-12的整数
    private Location location;
    private String signature;
    private BaseImage avatar;

    public void setName(String name) { this.name = name;  }

    public String getName() { return this.name; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getPhone() { return this.phone; }

    public String getId() {return this.id; }

    public void setPassword(String password) { this.password = password; }

    public String getPassword() { return this.password; }

    public void setEmail(String email) { this.email = email; }

    public String getEmail() { return this.email; }

    public void setSex(String sex) { this.sex = sex; }

    public String getSex() { return this.sex; }

    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getNickname() { return this.nickname; }

    public void setAges(String ages) { this.ages = ages; }

    public String getAges() { return this.ages; }

    public void setStars(int stars) { this.stars = stars; }

    public int getStars() { return this.stars; }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public BaseImage getAvatar() {
        return avatar;
    }

    public void setAvatar(BaseImage avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "signature='" + signature + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", sex='" + sex + '\'' +
                ", nickName='" + nickname + '\'' +
                ", ages='" + ages + '\'' +
                ", stars=" + stars +
                ", location=" + location +
                '}';
    }
}
