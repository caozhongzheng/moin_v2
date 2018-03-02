package com.moinapp.wuliao.commons.banner;

import java.io.Serializable;

/**
 * IP,
 */
public class Banner implements Serializable {
    private static final long serialVersionUID = -6988911941842961189L;

    public static final int TYPE_UNKNOWN = -1;
    /**热门推荐封面0*/
    public static final int TYPE_COVER = TYPE_UNKNOWN + 1;
    /**热门推荐封面1 表情*/
    public static final int TYPE_EMOJI = TYPE_COVER + 1;
    /**热门推荐封面2 片花*/
    public static final int TYPE_CLIP = TYPE_EMOJI + 1;
    /**热门推荐封面3 剧照*/
    public static final int TYPE_SCREEN = TYPE_CLIP + 1;
    /**热门推荐封面4 海报*/
    public static final int TYPE_POSTER = TYPE_SCREEN + 1;


    /**
     * strId
     */
    private String strId;

    private String strName;
   /**
     * banner和pop的广告图片
     */
    private String imageUrl;

    private int type;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStrId() {
        return strId;
    }

    public void setStrId(String strId) {
        this.strId = strId;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Banner{" +
                "strId='" + strId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", type=" + type +
                '}';
    }
}
