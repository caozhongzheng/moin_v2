package com.moinapp.wuliao.modules.ipresource.tables;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.moinapp.wuliao.commons.db.AbsDataTable;
import com.moinapp.wuliao.commons.db.DataProvider;

/**
 * Created by liujiancheng on 15/8/12.
 */
public class BannerListCache extends AbsDataTable {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final String TABLE_NAME = "banner_list_cache";
    public static final String DATA_AUTHORITY = DataProvider.DATA_AUTHORITY;
    public static final String _ID = "_id";
    public static final Uri BANNER_CACHE_URI = Uri.parse("content://" + DataProvider.DATA_AUTHORITY + "/" + TABLE_NAME);

    public static final String BANNER_TYPE = "banner_type";
    public static final String BANNER_TITLE = "banner_title";
    public static final String BANNER_JSON = "json";// banner的json串
    public static final String BANNER_UPDATE_TIME = "update_time";// 每一个BANNER获取的时间

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
        createBannerTable(db);
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        createBannerTable(db);
    }

    private void createBannerTable(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + _ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + BANNER_TYPE + " INTEGER default 0,"
                    + BANNER_TITLE + " VARCHAR(20),"
                    + BANNER_JSON + " VARCHAR(20),"
                    + BANNER_UPDATE_TIME + " INTEGER default 0)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

