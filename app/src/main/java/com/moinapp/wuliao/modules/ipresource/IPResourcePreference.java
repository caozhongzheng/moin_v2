package com.moinapp.wuliao.modules.ipresource;

import com.moinapp.wuliao.commons.preference.MyPreference;

/**
 * Created by liujiancheng on 15/5/14.
 */
public class IPResourcePreference extends MyPreference {

    // ===========================================================
    // Constants
    // ===========================================================
    public static final String KEY_LAST_GET_MOIN_IP_TIME = "last_get_moin_ip_time";//上次获取最moin ip列表时间
    public static final String KEY_LAST_GET_HOT_IP_TIME = "last_get_hot_ip_time";//上次获取最热ip列表时间
    public static final String KEY_LAST_GET_PRIMARY_HOT_IP_TIME = "last_get_primary_hot_ip_time";//上次获取首页的热门ip时间
    public static final String KEY_FIRST_IP_DETAIL = "first_enter_ipdetail";//是否首次进入IPMoin首页
    public static final String KEY_FIRST_BANNER = "first_banner";//是否首次看到Banner详情

    public static final String KEY_LAST_GET_EMOJI_TIME = "last_get_emoji_time";//上次获取最emoji列表时间
    public static final String KEY_SEARCH_EMJ_HISTORY = "search_emoji_history";//表情tab搜索历史
    // ===========================================================
    // Fields
    // ===========================================================
    private static IPResourcePreference sInstance;
    // ===========================================================
    // Constructors
    // ===========================================================
    private IPResourcePreference() {
        super(IPResourceModule.MODULE_NAME);
    }

    public static IPResourcePreference getInstance() {
        if (sInstance == null) {
            sInstance = new IPResourcePreference();
        }
        return sInstance;
    }

    // ===========================================================
    // Getter & Setter
    public long getLastGetMoinIPTime() {
        return getLong(KEY_LAST_GET_MOIN_IP_TIME);
    }

    public void setKeyLastGetMoinIpTime(long lastTime) {
        setLong(KEY_LAST_GET_MOIN_IP_TIME, lastTime);
    }

    public long getLastGetHotIPTime() {
        return getLong(KEY_LAST_GET_HOT_IP_TIME);
    }

    public void setLastGetHotIPTime(long lastTime) {
        setLong(KEY_LAST_GET_HOT_IP_TIME, lastTime);
    }

    public long getLastGetPrimaryHotIPtIME() {
        return getLong(KEY_LAST_GET_PRIMARY_HOT_IP_TIME);
    }

    public void setKeyLastGetPrimaryHotIpTime(long lastTime) {
        setLong(KEY_LAST_GET_PRIMARY_HOT_IP_TIME, lastTime);
    }

    public boolean isFirstEnterIPMoin() {
        return getBoolean(KEY_FIRST_IP_DETAIL, true);
    }

    public void setFirstEnterIPMoin(boolean firstEnterIPMoin) {
        setBoolean(KEY_FIRST_IP_DETAIL, firstEnterIPMoin);
    }

    public boolean isFirstBanner() {
        return getBoolean(KEY_FIRST_BANNER, true);
    }

    public void setFirstEnterBanner(boolean firstEnterBanner) {
        setBoolean(KEY_FIRST_BANNER, firstEnterBanner);
    }

    public long getLastGetEmojiTime() {
        return getLong(KEY_LAST_GET_EMOJI_TIME);
    }

    public void setLastGetEmojiTime(long lastTime) {
        setLong(KEY_LAST_GET_EMOJI_TIME, lastTime);
    }

    public String getKeySearchEmjHistory() {
        return getString(KEY_SEARCH_EMJ_HISTORY);
    }

    public void setKeySearchEmjHistory(String history) {
        setString(KEY_SEARCH_EMJ_HISTORY, history);
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