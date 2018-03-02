package com.moinapp.wuliao.modules.ipresource.model;

import com.moinapp.wuliao.commons.modal.BaseImage;

/**
 * Created by liujiancheng on 15/5/12.
 * ip首页用到的ip模型类
 */
public class IPResource {
    private String id;
    private int type;
    private String name;
    private String shortIntro;
    private BaseImage icon;
    private MoinPictures pics;
//    private List<BaseImage> pics;
    private int isHot;
    private long releaseDate;//上映时间 注意这是一个整数需要转成date

    public String get_id() {
        return id;
    }

    public void set_id(String _id) {
        this.id = _id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public BaseImage getIcon() {
        return icon;
    }

    public void setIcon(BaseImage icon) {
        this.icon = icon;
    }

    public String getShortIntro() {
        return shortIntro;
    }

    public void setShortIntro(String shortIntro) {
        this.shortIntro = shortIntro;
    }

    public MoinPictures getPics() {
        return pics;
    }

    public void setPics(MoinPictures pics) {
        this.pics = pics;
    }

    public int getIsHot() {
        return isHot;
    }

    public void setIsHot(int isHot) {
        this.isHot = isHot;
    }

    public long getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(long releaseDate) {
        this.releaseDate = releaseDate;
    }
//    @Override
//    public String toString() {
//        return "IP Resource: id="+_id + ", name="+ name + ", shortIntro=" + shortIntro
//                + ", pics:posters=" + pics.getPoster() + ", clip="+ pics.getClip()
//                + ", emojis="+ pics.getEmoji() + ", cover=" + pics.getCover() + ",screen="
//                + pics.getScreen() + ", icon="+icon.getUri() +", isHot=" + isHot;
//    }
}
