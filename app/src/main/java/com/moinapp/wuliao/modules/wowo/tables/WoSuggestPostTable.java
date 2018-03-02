package com.moinapp.wuliao.modules.wowo.tables;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.moinapp.wuliao.commons.db.AbsDataTable;
import com.moinapp.wuliao.commons.db.DataProvider;

/**
 * Created by liujiancheng on 15/7/30.
 */
public class WoSuggestPostTable extends AbsDataTable {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final String TABLE_NAME = "wo_suggest_post_cache";
    public static final String DATA_AUTHORITY = DataProvider.DATA_AUTHORITY;
    public static final String _ID = "_id";
    public static final Uri WOSUGGEST_POST_CACHE_URI = Uri.parse("content://" + DataProvider.DATA_AUTHORITY + "/" + TABLE_NAME);

    public static final String POST_ID = "post_id";
    public static final String WO_ID = "wo_id";
    public static final String POST_JSON = "json";// 帖子的json
    public static final String FROM = "post_from";// 推荐的来源： 0，ip首页的推荐帖子 1:窝贴列表的推荐帖子
    public static final String UPDATE_TIME = "upadte_time";// 更新的时间

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
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + POST_ID + " VARCHAR(20),"
                    + WO_ID + " INTEGER default 0,"
                    + POST_JSON + " VARCHAR(20),"
                    + FROM + " INTEGER default 0,"
                    + UPDATE_TIME + " INTEGER default 0)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO

    }
}
