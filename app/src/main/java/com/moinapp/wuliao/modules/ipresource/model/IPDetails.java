package com.moinapp.wuliao.modules.ipresource.model;

import com.moinapp.wuliao.commons.modal.BaseImage;
import com.moinapp.wuliao.commons.modal.BaseVideo;

import java.util.List;

/**
 * Created by liujiancheng on 15/5/12.
 * ip详情的模型类，业务层用到
 */
public class IPDetails {
    private String id;//IP资源ID
    private int status;
    private int type;
    private MoinPictures pics;//详情需要的图像资源数组集合
    private BaseVideo clip;//详情中第一张大的片花图片
    private List<List<MoinClip>> cliplist;//片花剧照的图片视频资源集合
    private List<String> tags;//标签数组集合，对应tags Collection中的记录
    private IPTypeInfo typeInfo;//类型子对象，可以是 movieInfo, tvInfo, showInfo中的一个
    private String location;//地区
    private String language;//语言
    private String owner;//出品方
    private long releaseDate;//上映时间 注意这是一个整数，需要转成date
    private List<BaseImage> posters;//海报图集
//    private List<BaseImage> screens;//剧照图集
    private String director;//导演
    private String actor;//演员
    private List<MoinWord> words;//台词数据组集合
    private String shareUrl;//分享链接地址，服务器根据客户端类型自动生成对应内容。
    private boolean isFavorite;//是否被收藏
    private int favoriteStat;//被收藏的次数

    private List<EmojiInfo> emojis;//次ip相关的表情集合，一般是6张
    private int size;//大小
    private String name;//名称
    private String intro;//介绍
    private float statSort;//分享的次数加上收藏的次数作为整数部分，取编辑时的时间毫秒数作为小数部分，供分页排序使用。

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public MoinPictures getPics() {
        return pics;
    }

    public void setPics(MoinPictures pics) {
        this.pics = pics;
    }

    public BaseVideo getClip() {
        return clip;
    }

    public void setClip(BaseVideo clip) {
        this.clip = clip;
    }

    public List<List<MoinClip>> getCliplist() {
        return cliplist;
    }

    public void setCliplist(List<List<MoinClip>> cliplist) {
        this.cliplist = cliplist;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public IPTypeInfo getTypeInfo() {
        return typeInfo;
    }

    public void setTypeInfo(IPTypeInfo typeInfo) {
        this.typeInfo = typeInfo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public long getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(long releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<BaseImage> getPosters() {
        return posters;
    }

    public void setPosters(List<BaseImage> posters) {
        this.posters = posters;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public List<MoinWord> getWords() {
        return words;
    }

    public void setWords(List<MoinWord> words) {
        this.words = words;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }


    public List<EmojiInfo> getEmojis() {
        return emojis;
    }

    public void setEmojis(List<EmojiInfo> emojis) {
        this.emojis = emojis;
    }
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    public float getStatSort() {
        return statSort;
    }

    public void setStatSort(float statSort) {
        this.statSort = statSort;
    }

    public int getFavoriteStat() {
        return favoriteStat;
    }

    public void setFavoriteStat(int favoriteStat) {
        this.favoriteStat = favoriteStat;
    }
}
