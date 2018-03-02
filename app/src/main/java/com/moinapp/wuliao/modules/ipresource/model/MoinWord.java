package com.moinapp.wuliao.modules.ipresource.model;

/**
 * Created by liujiancheng on 15/5/12.
 * IP详情中的台词对象
 */
public class MoinWord {
    private String owner;
    private String content;

    public void setOwner(String owner) { this.owner = owner; }
    public String getOwner() { return owner; }

    public void setContent (String content) { this.content = content; }
    public String getContent() { return content; }

    @Override
    public String toString() {
        return "MoinWord{" +
                "owner='" + owner + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
