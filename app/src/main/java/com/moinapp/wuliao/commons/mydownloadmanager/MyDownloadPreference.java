package com.moinapp.wuliao.commons.mydownloadmanager;

import com.moinapp.wuliao.commons.preference.MyPreference;

public class MyDownloadPreference extends MyPreference {

    // ===========================================================
    // Constants
    // ===========================================================

    public static final String KEY_LAST_CHECK_DOWNLOAD ="last_check_download";

    // ===========================================================
    // Fields
    // ===========================================================

    private static MyDownloadPreference sInstance;

    // ===========================================================
    // Constructors
    // ===========================================================

    private MyDownloadPreference() {
    }

    public static MyDownloadPreference getInstance(){
        if(sInstance == null){
            sInstance = new MyDownloadPreference();
        }

        return sInstance;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================
	public long getLastCheckDownload(){
	    return getLong(KEY_LAST_CHECK_DOWNLOAD);
	}
	
	public void setLastCheckDownload(long time){
        setLong(KEY_LAST_CHECK_DOWNLOAD, time);
    }
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
