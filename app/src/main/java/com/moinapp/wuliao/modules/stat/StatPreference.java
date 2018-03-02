package com.moinapp.wuliao.modules.stat;

import com.moinapp.wuliao.commons.preference.MyPreference;

public class StatPreference extends MyPreference {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String KEY_UPLOAD_PACKAGE_FINISH = "upload_package_finish";
    private static final String KEY_LAST_UPLOAD_LOG = "last_upload_log";

    private static final String KEY_ACTION_LOG_ENABLE = "action_log_enable";//行为日志开关
    private static final String KEY_PACKAGE_LOG_ENABLE = "package_log_enable";//安装包信息上传开关
    // ===========================================================
    // Fields
    // ===========================================================

    private static StatPreference sInstance;

    // ===========================================================
    // Constructors
    // ===========================================================

    private StatPreference() {
    }

    public static StatPreference getInstance(){
        if(sInstance == null){
            sInstance = new StatPreference();
        }

        return sInstance;
    }


    // ===========================================================
    // Getter & Setter
    // ===========================================================
	public boolean getUploadPackageFinish(){
	    return getBoolean(KEY_UPLOAD_PACKAGE_FINISH);
	}
	public void setUploadPackageFinish(boolean finish){
        setBoolean(KEY_UPLOAD_PACKAGE_FINISH, finish);
    }
	
	public long getLastUploadLog(){
	    return getLong(KEY_LAST_UPLOAD_LOG);
	}
	public void setLastUploadLog(long time){
        setLong(KEY_LAST_UPLOAD_LOG, time);
    }
	
	public boolean isUploadLogEnabled(){
	    return getBoolean(KEY_ACTION_LOG_ENABLE);
	}
	public void setUploadLogEnabled(boolean value){
        setBoolean(KEY_ACTION_LOG_ENABLE, value);
    }
	
	public boolean isUploadPackageEnabled(){
	    return getBoolean(KEY_PACKAGE_LOG_ENABLE);
	}
	public void setUploadPackageEnabled(boolean value){
        setBoolean(KEY_PACKAGE_LOG_ENABLE, value);
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
