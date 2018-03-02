package com.moinapp.wuliao.modules.domainupdate.table;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.moinapp.wuliao.commons.db.AbsDataTable;
import com.moinapp.wuliao.commons.db.DataProvider;

public class DomainTable extends AbsDataTable{

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    public static final String TABLE_NAME = "domain_table";
    public static final int TABLE_VERSION = 2;
    public static final String DATA_AUTHORITY = DataProvider.DATA_AUTHORITY;
    public static final Uri TABLE_URI = Uri.parse("content://"+DATA_AUTHORITY+"/" + TABLE_NAME);

    public static final String DOMAIN_TYPE = "type"; //0 Launcher 1 Weather
    public static final String DOMAIN_REGION = "region"; //0 cn 1 en
    public static final String DOMAIN_VER = "version";
    public static final String DOMAIN_SEQ = "seq";
    public static final String DOMAIN_DOMAINS = "domains"; 
    

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
                            _ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                            DOMAIN_TYPE + " INTEGER," +
                            DOMAIN_REGION + " INTEGER default 0," +
                            DOMAIN_VER + " VARCHAR(20)," +
                            DOMAIN_SEQ + " INTEGER default 0," +
                            DOMAIN_DOMAINS + " VARCHAR(20)" +                            
                            ")"
            );
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.upgrade(db, oldVersion, newVersion);
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
