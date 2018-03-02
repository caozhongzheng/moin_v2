package com.moinapp.wuliao.modules.stat.table;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.moinapp.wuliao.commons.db.AbsDataTable;
import com.moinapp.wuliao.commons.db.DataProvider;

public class StoreStatTable extends AbsDataTable{

    // ===========================================================
    // Constants
    // ===========================================================

    public static final String TABLE_NAME = "store_stat";
    public static final int TABLE_VERSION = 2;
    public static final String DATA_AUTHORITY = DataProvider.DATA_AUTHORITY;
    public static final Uri TABLE_URI = Uri.parse("content://"+DATA_AUTHORITY+"/" + TABLE_NAME);

    public static final String STORE_STAT_ID = "_id";
    public static final String STORE_STAT_DESC = "desc";
    public static final String STORE_STAT_RESOURCEID = "resourceId";
    public static final String STORE_STAT_SCNE = "scene";
    public static final String STORE_STAT_TIME = "time";
    public static final String STORE_STAT_ISUPLOAD = "isUpload";

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
    public int getVersion() {
        return TABLE_VERSION;
    }

    @Override
    public void create(SQLiteDatabase db) {
        try{
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                            STORE_STAT_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                            STORE_STAT_DESC + " VARCHAR(20)," +
                            STORE_STAT_RESOURCEID + " VARCHAR(20)," +
                            STORE_STAT_SCNE + " VARCHAR(20)," +
                            STORE_STAT_TIME + " INTEGER," +
                            STORE_STAT_ISUPLOAD + " INTEGER default 0" +
                            ")"
            );
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
