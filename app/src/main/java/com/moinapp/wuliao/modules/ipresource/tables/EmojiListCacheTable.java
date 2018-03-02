package com.moinapp.wuliao.modules.ipresource.tables;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.moinapp.wuliao.commons.db.AbsDataTable;
import com.moinapp.wuliao.commons.db.DataProvider;

/**
 * Created by liujiancheng on 15/8/3.
 */
public class EmojiListCacheTable  extends AbsDataTable {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final String TABLE_NAME = "emoji_list_cache";
    public static final String DATA_AUTHORITY = DataProvider.DATA_AUTHORITY;
    public static final String _ID = "_id";
    public static final Uri EMOJILIST_CACHE_URI = Uri.parse("content://" + DataProvider.DATA_AUTHORITY + "/" + TABLE_NAME);

    public static final String EMOJI_ID = "emoji_id";
    public static final String UID = "uid";
    public static final String EMOJI_JSON = "json";// Emoji列表中的每一个emoji基本信息的json串
    public static final String EMOJI_UPDATE_TIME = "update_time";// 每一个emoji获取的时间
//    public static final String EMOJI_DOWNLOAD_FLAG = "download_flag";// 表情专题是否已经下载的标记
//    public static final String EMOJI_LIKE_FLAG = "like_flag";// 表情专题是否被点赞的标记

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public String getName() {
        return TABLE_NAME;
    }

    @Override
    public void create(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + _ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + EMOJI_ID + " VARCHAR(20),"
                    + UID + " VARCHAR(20),"
                    + EMOJI_JSON + " VARCHAR(20),"
                    + EMOJI_UPDATE_TIME + " INTEGER default 0)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO

    }
}

