package com.moinapp.wuliao.modules.ipresource;

import android.text.TextUtils;

import com.moinapp.wuliao.modules.cosplay.ui.CosplayConstants;
import com.moinapp.wuliao.modules.ipresource.model.EmojiInfo;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.StringUtil;

/**
 * Created by moying on 15/7/13.
 */
public class EmojiUtils {
//    public static String getEmojiPath(String emojiResourceId) {
//        if(AppTools.existsSDCARD()) {
//            FileUtil.createFolder(BitmapUtil.BITMAP_EMOJI + emojiResourceId + File.separator);
//        }
//        return BitmapUtil.BITMAP_EMOJI + emojiResourceId + File.separator;
//    }


    /**
     * 表情专辑的本地文件夹地址
     */
    public static String getEmjSetFolder(String emojiResourceId) {
        if (StringUtil.isNullOrEmpty(emojiResourceId)) {
            return "";
        }
        return BitmapUtil.BITMAP_EMOJI + emojiResourceId + "/";
    }

    /**
     * 表情专辑ICON的本地地址
     */
    public static String getEmjSetPath(String emojiResourceId) {
        if (StringUtil.isNullOrEmpty(emojiResourceId)) {
            return "";
        }
        return BitmapUtil.BITMAP_EMOJI + emojiResourceId + "/" + emojiResourceId + IPResourceConstants.JPG_EXTENSION;
    }


    /**
     * 表情专辑ICON的本地地址
     */
    public static String getEmjSetPath(EmojiInfo emoji) {
        if (emoji == null) {
            return null;
        }
        return BitmapUtil.BITMAP_EMOJI + emoji.getParentid() + "/" + emoji.getParentid() + IPResourceConstants.JPG_EXTENSION;
    }


    /**
     * 表情ICON的本地地址
     */
    public static String getEmjPath(EmojiInfo emoji) {
        if (emoji == null) {
            return null;
        }
        return BitmapUtil.BITMAP_EMOJI + emoji.getParentid() + "/" + emoji.getParentid() + "_" + emoji.getId() + IPResourceConstants.GIF_EXTENSION;
    }


    /**
     * 表情缩略图的本地地址
     */
    public static String getThumbPath(EmojiInfo emoji) {
        if (emoji == null) {
            return null;
        }
        return BitmapUtil.BITMAP_EMOJI + emoji.getParentid() + "/" + emoji.getParentid() + "_" + emoji.getId() + IPResourceConstants.JPG_EXTENSION;
    }

    /**
     * 大咖秀gif的本地地址
     */
    public static String getCosplayPath(String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        return CosplayConstants.COSPLAY_EMOJI_FOLDER + id +  IPResourceConstants.GIF_EXTENSION;
    }


    /**
     * 表情缩略图的本地地址
     */
    public static String getCosplayThumbPath(String id) {
        if (TextUtils.isEmpty(id)) {
            return null;
        }
        return CosplayConstants.COSPLAY_EMOJI_FOLDER + id +  IPResourceConstants.JPG_EXTENSION;
    }

    /**
     * 表情ICON的缩放大小
     *
     * @param width 表情ICON的宽度
     * @param height 表情ICON的高度
     * @param minWidth 表情ICON的显示最小宽度
     */
    public static int[] getScaleEmjHeight(int width, int height, int minWidth) {
        int[] params = new int[2];
        if(width >= minWidth) {
            params[0] = width;
            params[1] = height;
            return params;
        }
        float newheight = ((float) height * minWidth / width);
        params[0] = minWidth;
        params[1] = (int) newheight;
        return params;
    }



    /**
     * 图片的缩放大小
     *
     * @param width 图片的宽度
     * @param height 图片的高度
     * @param mPicMaxWidth 图片的显示最大宽度
     */
    public static int[] getScaleHeight(int width, int height, int mPicMaxWidth) {
        int[] params = new int[2];
        if(width <= mPicMaxWidth) {
            float scale = (float) mPicMaxWidth / (float) width;
            scale = scale > 2 ? 2 : scale;
            params[0] = (int) (width * scale);
            params[1] = (int) (height * scale);
            return params;
        }
        float newheight = ((float) height * mPicMaxWidth / width);
        params[0] = mPicMaxWidth;
        params[1] = (int) newheight;
        return params;
    }

}
