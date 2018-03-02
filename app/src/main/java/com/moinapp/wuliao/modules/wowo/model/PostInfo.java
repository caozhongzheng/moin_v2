package com.moinapp.wuliao.modules.wowo.model;

import com.moinapp.wuliao.commons.modal.BaseImage;
import com.moinapp.wuliao.modules.ipresource.model.EmojiInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liujiancheng on 15/6/8.
 * 帖子对象的模型类
 */
public class PostInfo implements Serializable {
    /**
     * 帖子所属窝的id
     */
    private int woid;
    /**
     * 帖子id
     */
    private String postid;
    /**
     * 帖子所在窝的icon
     */
    private BaseImage woicon;
    /**
     * 帖子所在窝的名字
     */
    private String woname;
    /**
     * 帖子的封面
     */
    private BaseImage cover;
    /**
     * 帖子的作者
     */
    private PostAuthor author;
    /**
     * 帖子创建的时间
     */
    private long createdAt;
    /**
     * 帖子更新的时间
     */
    private long updatedAt;
    /**
     * 帖子的状态， 包括1 正常 2 置顶 3 精华
     */
    private int status;
    /**
     * 帖子的标题
     */
    private String title;
    /**
     * 帖子的内容 （注意在获取帖子列表页面中只取帖子文本内容的前100个字符）
     */
    private String content;
    /**
     * 帖子的评论数量
     */
    private long commentCount;
    /**
     * 帖子的用户数量
     */
    private long userCount;
    /**
     * 帖子包含图片数组
     */
    private List<BaseImage> images;
    /**
     * 帖子包含的表情
     */
    private EmojiInfo emoji;
    /**
     * 帖子是否包含图片
     */
    private boolean hasImage;
    /**
     * 帖子是否包含表情
     */
    private boolean hasEmoji;
    /**
     * 分享链接
     */
    private String shareUrl;
    /**
     * //当从Ip首页进入窝窝热帖时，推荐的ip
     */
    private SuggestIP suggestip;
    /**
     * //当从Ip首页进入窝窝热帖时，推荐的表情
     */
    private SuggestEmoji suggestemoji;
    /**
     * //帖子标签，只有一个
     */
    private String tag;

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

    public BaseImage getWoicon() {
        return woicon;
    }

    public void setWoicon(BaseImage woicon) {
        this.woicon = woicon;
    }

    public String getWoname() {
        return woname;
    }

    public void setWoname(String woname) {
        this.woname = woname;
    }

    public BaseImage getCover() {
        return cover;
    }

    public void setCover(BaseImage cover) {
        this.cover = cover;
    }

    public PostAuthor getAuthor() {
        return author;
    }

    public void setAuthor(PostAuthor author) {
        this.author = author;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updateAt) {
        this.updatedAt = updateAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
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

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public boolean isHasEmoji() {
        return hasEmoji;
    }

    public void setHasEmoji(boolean hasEmoji) {
        this.hasEmoji = hasEmoji;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public SuggestIP getSuggestip() {
        return suggestip;
    }

    public void setSuggestip(SuggestIP suggestip) {
        this.suggestip = suggestip;
    }

    public SuggestEmoji getSuggestemoji() {
        return suggestemoji;
    }

    public void setSuggestemoji(SuggestEmoji suggestemoji) {
        this.suggestemoji = suggestemoji;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "PostInfo{" +
                "woid=" + woid +
                ", postid='" + postid + '\'' +
                ", woicon=" + woicon +
                ", woname='" + woname + '\'' +
                ", cover=" + cover +
                ", author=" + author.toString() +
                ", updatedAt=" + updatedAt +
                ", status=" + status +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", commentCount=" + commentCount +
                ", userCount=" + userCount +
                ", images=" + images +
                ", emoji=" + emoji +
                ", shareUrl='" + shareUrl + '\'' +
                ", suggestip=" + suggestip +
                ", suggestemoji=" + suggestemoji +
                ", tag='" + tag + '\'' +
                '}';
    }
}
