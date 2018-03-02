package com.moinapp.wuliao.modules.regularupdate;

import com.moinapp.wuliao.commons.preference.MyPreference;

public class RegularUpatePreference extends MyPreference {
	// ===========================================================
	// Constants
	// ===========================================================
	/** 定期联网频率配置文件版本 */
    private static final String KEY_LAST_REGULAR_UPDATE_TIME = "last_regular_update_time";
	private static final String KEY_REGULARUPD_VERSION = "regular_update_ver";// 定期联网频率配置文件版本
	private static final String KEY_REGULAR_UPDATE_FREQ_WIFI = "regular_update_freq_wifi";// 定期联网接口，wifi下联网频率，单位分钟
	private static final String KEY_REGULAR_UPDATE_FREQ_3G = "regular_update_freq_3g";// 定期联网，3g下联网频率，单位分钟
	// ===========================================================
	// Fields
	// ===========================================================
	private static RegularUpatePreference sInstance = new RegularUpatePreference();

	// ===========================================================
	// Constructors
	// ===========================================================
	private RegularUpatePreference() {
	}

	public static RegularUpatePreference getInstance() {
		return sInstance;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public long getLastRegularUpdateTime(){
	    return getLong(KEY_LAST_REGULAR_UPDATE_TIME);
	}
	public void setLastRegularUpdateTime(long time){
        setLong(KEY_LAST_REGULAR_UPDATE_TIME, time);
    }
	
	public String getRegularUpdateVersion(){
	    return getString(KEY_REGULARUPD_VERSION);
	}
	public void setRegularUpdateVersion(String version){
        setString(KEY_REGULARUPD_VERSION, version);
    }
	
	public long getRegularUpdateFreqWifiInMinutes(){
	    return getLong(KEY_REGULAR_UPDATE_FREQ_WIFI);
	}
	public void setRegularUpdateFreqWifiInMinutes(long time){
        setLong(KEY_REGULAR_UPDATE_FREQ_WIFI, time);
    }
	
	public long getRegularUpdateFreq3gInMinutes(){
	    return getLong(KEY_REGULAR_UPDATE_FREQ_3G);
	}
	public void setRegularUpdateFreq3gInMinutes(long time){
        setLong(KEY_REGULAR_UPDATE_FREQ_3G, time);
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
