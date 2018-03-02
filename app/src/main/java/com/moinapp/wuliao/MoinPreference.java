package com.moinapp.wuliao;

import com.moinapp.wuliao.commons.preference.MyPreference;

/**
 * Created by moying on 15/8/3.
 */
public class MoinPreference extends MyPreference {

    // ===========================================================
    // Constants
    // ===========================================================
    /**是否第一次打开应用*/
    public static final String KEY_FIRST_ENTER = "first_enter";
    // ===========================================================
    // Fields
    // ===========================================================
    private static MoinPreference sInstance;
    // ===========================================================
    // Constructors
    // ===========================================================
    private MoinPreference() {
        super();
    }

    public static MoinPreference getInstance() {
        if (sInstance == null) {
            sInstance = new MoinPreference();
        }
        return sInstance;
    }

    // ===========================================================
    // Getter & Setter
    public boolean isFirstEnter() {
        return getBoolean(KEY_FIRST_ENTER, true);
    }

    public void setFirstEnter(boolean firstEnter) {
        setBoolean(KEY_FIRST_ENTER, firstEnter);
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
