package com.moinapp.wuliao.modules.push;

import com.moinapp.wuliao.commons.preference.MyPreference;

public class PushPreference extends MyPreference {

	// ===========================================================
	// Constants
	// ===========================================================
	public static final String KEY_LAST_GET_PUSH_TIME = "last_get_push_time";
	// 服务器下发的是否允许推送开关
	public static final String KEY_PUSH_ENABLE = "push_enable";
	// Push Notification wifi下联网频率，单位分钟
	public static final String KEY_PUSH_FREQ_WIFI = "push_freq_wifi";
	// Push Notification 非wifi下联网频率，单位分钟
	public static final String KEY_PUSH_FREQ_3G = "push_freq_3g";
	// ===========================================================
	// Fields
	// ===========================================================
	private static PushPreference sInstance;
	// ===========================================================
	// Constructors
	// ===========================================================
	private PushPreference() {
	}

	public static PushPreference getInstance() {
		if (sInstance == null) {
			sInstance = new PushPreference();
		}
		return sInstance;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================
    public boolean isPushEnable(){
        return getBoolean(KEY_PUSH_ENABLE);
    }
    public void setPushEnable(boolean enable){
        setBoolean(KEY_PUSH_ENABLE, enable);
    }

	public long getLastGetPushTime() {
		return getLong(PushPreference.KEY_LAST_GET_PUSH_TIME);
	}

	public void setLastGetPushTime(long lastGetPushTime) {
		setLong(PushPreference.KEY_LAST_GET_PUSH_TIME, lastGetPushTime);
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
