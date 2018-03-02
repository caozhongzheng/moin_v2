package com.moinapp.wuliao.commons.service;

import com.moinapp.wuliao.commons.preference.MyPreference;

public class ServicePreference extends MyPreference {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String KEY_LAST_PERIOD_CHECK = "last_period_check";
	// ===========================================================
	// Fields
	// ===========================================================
	private static ServicePreference sInstance = new ServicePreference();
	// ===========================================================
	// Constructors
	// ===========================================================
	private ServicePreference() {
	}
	
	public static ServicePreference getInstance(){
		return sInstance;
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	public long getLastPeriodCheck(){
	    return getLong(KEY_LAST_PERIOD_CHECK);
	}
	
	public void setLastPeriodCheck(long time){
        setLong(KEY_LAST_PERIOD_CHECK, time);
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
