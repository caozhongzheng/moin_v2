package com.moinapp.wuliao.commons.preference;

public class CommonsPreference extends MyPreference {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final String KEY_CHANEL_ID = "channel_id";
    private static final String KEY_NEED_FORE_ACTIVE = "need_fore_active";
    private static final String KEY_FORE_ACTIVE_SUCC = "fore_active_succ";
	private static final String KEY_UID = "moin_uid";
	private static final String KEY_PASSPORT = "moin_passport";
	private static final String KEY_VIRTUAL_KEYBOARD_HEIGHT = "virtual_keyboard_height";
	// ===========================================================
	// Fields
	// ===========================================================
    private static CommonsPreference sInstance = new CommonsPreference();

	// ===========================================================
	// Constructors
	// ===========================================================
	private CommonsPreference() {
	}
	
	public static CommonsPreference getInstance(){
		return sInstance;
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================
    public String getChannelId() {
        return getString(KEY_CHANEL_ID);
    }

    public void setChannelId(String channelId) {
        setString(KEY_CHANEL_ID, channelId);
    }
    
	public boolean isNeedForegroundActive(){
	    return getBoolean(KEY_NEED_FORE_ACTIVE);
	}	
	public void setNeedForegroundActive(boolean need){
        setBoolean(KEY_NEED_FORE_ACTIVE, need);
    }
	
	public boolean isForegroundActiveSuccess(){
	    return getBoolean(KEY_FORE_ACTIVE_SUCC);
	}	
	public void setForegroundActiveSuccess(boolean success){
        setBoolean(KEY_FORE_ACTIVE_SUCC, success);
    }

	public void setUID(String uid) {
		setString(KEY_UID, uid);
	}

	public String getUID() {
		return getString(KEY_UID);
	}

	public void setPassport(String passport) {
		setString(KEY_PASSPORT, passport);
	}

	public String getPassport() {
		return getString(KEY_PASSPORT);
	}

	public void setVirtualKeyboardHeight(int height) {
		setInt(KEY_VIRTUAL_KEYBOARD_HEIGHT, height);
	}

	public int getVirtualKeyboardHeight() {
		return getInt(KEY_VIRTUAL_KEYBOARD_HEIGHT);
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
