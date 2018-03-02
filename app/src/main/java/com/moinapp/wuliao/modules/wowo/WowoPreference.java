package com.moinapp.wuliao.modules.wowo;

import com.moinapp.wuliao.commons.preference.MyPreference;

/**
 * Created by liujiancheng on 15/5/14.
 */
public class WowoPreference extends MyPreference {

    // ===========================================================
    // Constants
    // ===========================================================
    /**是否进入过表情下载列表*/
    public static final String KEY_VISIT_EMOJILIST = "visit_emojilist";
    public static final String KEY_LAST_LOGIN_EVENT_TIME = "last_login_event_time";
    public static final String KEY_FIRST_POST = "first_post";//第一次发帖
    public static final String KEY_FIRST_POST_DETAIL = "first_post_detail";//第一次帖子详情
    public static final String KEY_DEFAULT_EMOJISET_ID = "default_emojiset_id";//预制表情专辑ID
    // ===========================================================
    // Fields
    // ===========================================================
    private static WowoPreference sInstance;
    // ===========================================================
    // Constructors
    // ===========================================================
    private WowoPreference() {
        super(WowoModule.MODULE_NAME);
    }

    public static WowoPreference getInstance() {
        if (sInstance == null) {
            sInstance = new WowoPreference();
        }
        return sInstance;
    }

    // ===========================================================
    // Getter & Setter
    public boolean isVisitEmojiList() {
        return getBoolean(KEY_VISIT_EMOJILIST, false);
    }

    public void setVisitEmojiList(boolean visit) {
        setBoolean(KEY_VISIT_EMOJILIST, visit);
    }

    public long getLastLoginEventTime() {
        return getLong(KEY_LAST_LOGIN_EVENT_TIME, 0);
    }

    public void setLastLoginEventTime(long time) {
        setLong(KEY_LAST_LOGIN_EVENT_TIME, time);
    }

    public boolean isFirstPost() {
        return getBoolean(KEY_FIRST_POST, true);
    }

    public void setFirstPost(boolean firstPost) {
        setBoolean(KEY_FIRST_POST, firstPost);
    }

    public boolean isFirstPostDetail() {
        return getBoolean(KEY_FIRST_POST_DETAIL, true);
    }

    public void setFirstPostDetail(boolean firstPostDetail) {
        setBoolean(KEY_FIRST_POST_DETAIL, firstPostDetail);
    }
    public String getDefaultEmojisetId() {
        return getString(KEY_DEFAULT_EMOJISET_ID);
    }

    public void setDefaultEmojisetId(String emojisetId) {
        setString(KEY_DEFAULT_EMOJISET_ID, emojisetId);
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