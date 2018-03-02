package com.moinapp.wuliao.commons.modal;

import java.io.Serializable;

/**
 * Created by liujiancheng on 15/8/11.
 */
public class TagInfo implements Serializable {
    private String type;

    private String name;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
