package com.moinapp.wuliao.modules.ipresource.model;

/**
 * Created by liujiancheng on 15/5/22.
 */
public class MoinShareUrl {
    private String key;//分享链接地址，key 是平台名字，android, ios,wp,firefox等
    private String value;//对应的地址

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
