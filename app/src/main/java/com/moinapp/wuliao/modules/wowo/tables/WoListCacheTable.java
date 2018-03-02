package com.moinapp.wuliao.modules.wowo.tables;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.moinapp.wuliao.commons.db.AbsDataTable;
import com.moinapp.wuliao.commons.db.DataProvider;

/**
 * Created by liujiancheng on 15/7/30.
 */
public class WoListCacheTable extends AbsDataTable {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final String TABLE_NAME = "wo_list_cache";
    public static final String DATA_AUTHORITY = DataProvider.DATA_AUTHORITY;
    public static final String _ID = "_id";
    public static final Uri WO_LIST_CACHE_URI = Uri.parse("content://" + DataProvider.DATA_AUTHORITY + "/" + TABLE_NAME);

    public static final String WO_ID = "wo_id";
    public static final String UID = "uid";// 区分用户的uid
    public static final String GROUP = "wo_group";// 窝分组： 0是我的窝，1是推荐的窝
    public static final String WO_JSON = "json";// 窝信息的json
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
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + WO_ID + " VARCHAR(20),"
                    + UID + " VARCHAR(20),"
                    + GROUP + " INTEGER default 0,"
                    + WO_JSON + " VARCHAR(20),"
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
