package com.moinapp.wuliao.modules.wowo.model;

/**
 * Created by moying on 15/6/8.
 * 图片，拍照 item
 */
public class AppBean {

    private int id;
    private String icon;
    private String funcName;

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getIcon() { return icon; }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "AppBean{" +
                "id=" + id +
                ", icon='" + icon + '\'' +
                ", funcName='" + funcName + '\'' +
                '}';
    }
}
