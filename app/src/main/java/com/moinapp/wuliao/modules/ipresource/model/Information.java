package com.moinapp.wuliao.modules.ipresource.model;

/**
 * Created by moying on 15/5/26.
 */
public class Information {
    private String title;
    private String desc;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Information [title=" + title + ", desc=" + desc + "]";
    }
}
