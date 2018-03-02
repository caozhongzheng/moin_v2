package com.moinapp.wuliao.modules.ipresource.tables;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.moinapp.wuliao.commons.db.AbsDataTable;
import com.moinapp.wuliao.commons.db.DataProvider;

/**
 * Created by liujiancheng on 15/5/14.
 * ip列表的缓存表
 */
public class IPListCacheTable extends AbsDataTable {
    // ===========================================================
    // Constants
    // ===========================================================
    public static final String TABLE_NAME = "iplist_cache";
    public static final String DATA_AUTHORITY = DataProvider.DATA_AUTHORITY;
    public static final String _ID = "_id";
    public static final Uri IPLIST_CACHE_URI = Uri.parse("content://" + DataProvider.DATA_AUTHORITY + "/" + TABLE_NAME);

    public static final String IP_ID = "ip_id";
    public static final String IP_EXPIRTIME = "expire";
    public static final String IP_JSON = "json";// ip列表中的每一个ip基本信息的json串，主要是首页用
    public static final String IP_DETAIL_JSON = "detail_json";// 每一个ip详细信息的json串
    public static final String IP_UPDATE_TIME = "update_time";// 每一个ip获取的时间
    public static final String IP_FLAG = "flag";// 1:最moin ip的标记; 2:最热门ip的标记; 4:首页上的热门ip标记; 注意同一个IP可以同时是热门moin和最热

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
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + IP_ID + " VARCHAR(20),"
                    + IP_EXPIRTIME + " INTEGER default 0,"
                    + IP_JSON + " VARCHAR(20),"
                    + IP_DETAIL_JSON + " VARCHAR(20),"
                    + IP_UPDATE_TIME + " INTEGER default 0,"
                    + IP_FLAG + " INTEGER default 0)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO

    }
}
