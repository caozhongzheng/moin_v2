package com.moinapp.wuliao.modules.wowo.model;

import com.moinapp.wuliao.commons.modal.BaseImage;
import com.moinapp.wuliao.modules.ipresource.model.EmojiInfo;

import java.util.List;

/**
 * Created by liujiancheng on 15/6/9.
 * 帖子的评论（回复）模型类
 */
public class CommentInfo {
    /**
     * 回复内容
     */
    private String content;
    /**
     * 楼号
     */
    private int cid;
    /**
     * 引用的楼号
     */
    private int referral;
    /**
     * 引用的摘要
     */
    private String meta;
    /**
     * 帖子id
     */
    private String postid;
    /**
     * 图片信息
     */
    private List<BaseImage> images;
    /**
     * 表情信息
     */
    private EmojiInfo emoji;
    /**
     * 回复时间
     */
    private long updatedAt;
    /**
     * 回复作者信息
     */
    private PostAuthor author;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int commentid) {
        this.cid = commentid;
    }

    public int getReferral() {
        return referral;
    }

    public void setReferral(int referral) {
        this.referral = referral;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public List<BaseImage> getImages() {
        return images;
    }

    public void setImages(List<BaseImage> images) {
        this.images = images;
    }

    public EmojiInfo getEmoji() {
        return emoji;
    }

    public void setEmoji(EmojiInfo emoji) {
        this.emoji = emoji;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long replyTimestamp) {
        this.updatedAt = replyTimestamp;
    }

    public PostAuthor getAuthor() {
        return author;
    }

    public void setAuthor(PostAuthor replyAuthor) {
        this.author = replyAuthor;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    @Override
    public String toString() {
        return "CommentInfo{" +
                "content='" + content + '\'' +
                ", cid=" + cid +
                ", postid='" + postid + '\'' +
//                ", images=" + images.toString() +
//                ", emoji=" + emoji.toString() +
                ", replyTimestamp=" + updatedAt +
                ", replyAuthor=" + author.toString() +
                '}';
    }
}
