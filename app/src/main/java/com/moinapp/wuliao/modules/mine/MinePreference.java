package com.moinapp.wuliao.modules.mine;

import android.text.TextUtils;

import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.preference.MyPreference;

/**
 * Created by moin on 15/7/2.
 */
public class MinePreference extends MyPreference {

    // ===========================================================
    // Constants
    // ===========================================================
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_NEED_FETCH_NICKNAME = "need_fetch_nickname";
    public static final String KEY_EMJ_DOWNLOAD_COUNT = "emj_download_count";
    public static final String KEY_EMJ_DOWNLOAD_ID = "emj_download_id";
    // ===========================================================
    // Fields
    // ===========================================================
    private static MinePreference sInstance;
    // ===========================================================
    // Constructors
    // ===========================================================
    private MinePreference() {
        super(MineModule.MODULE_NAME);
    }

    public static MinePreference getInstance() {
        if (sInstance == null) {
            sInstance = new MinePreference();
        }
        return sInstance;
    }

    // ===========================================================
    // Getter & Setter
    /**之所以加上UID前缀是为了防止别的用户登录后用的也是这个
     * ClientInfo.getUID()+"_" + KEY*/
    public String getNickname() {
        return getString(ClientInfo.getUID().hashCode()+"_" + KEY_NICKNAME);
    }

    public void setNickname(String nickname) {
        setString(ClientInfo.getUID().hashCode()+"_" + KEY_NICKNAME, nickname);
    }

    public boolean isNeedFetchNickname() {
        return getBoolean(KEY_NEED_FETCH_NICKNAME, true);
    }

    public void setNeedFetchNickname(boolean needFetchNickname) {
        setBoolean(KEY_NEED_FETCH_NICKNAME, needFetchNickname);
    }

    public int getEmjDownloadCount() {
        return getInt(ClientInfo.getUID().hashCode()+"_" + KEY_EMJ_DOWNLOAD_COUNT);
    }

    public void setEmjDownloadCount(int count) {
        setInt(ClientInfo.getUID().hashCode()+"_" + KEY_EMJ_DOWNLOAD_COUNT, count);
    }

    public String getEmjDownloadID() {
        return getString(ClientInfo.getUID().hashCode()+"_" + KEY_EMJ_DOWNLOAD_ID);
    }

    public void setEmjDownloadID(String id) {
        setString(ClientInfo.getUID().hashCode()+"_" + KEY_EMJ_DOWNLOAD_ID, id);
    }
    public void addEmjDownloadID(String id) {
        String oldID = getEmjDownloadID();
        if(TextUtils.isEmpty(oldID)) {
            setString(ClientInfo.getUID().hashCode()+"_" + KEY_EMJ_DOWNLOAD_ID, id);
        } else {
            setString(ClientInfo.getUID().hashCode()+"_" + KEY_EMJ_DOWNLOAD_ID, oldID + ";" + id);
        }
    }

    public boolean isEmjDownloaded(String id) {
        if (id == null) {
            return true;
        }
        String oldID = getEmjDownloadID();
        if(TextUtils.isEmpty(oldID))
            return false;
        return oldID.contains(id);
        // 更保险
//        String[] arr = oldID.split(";");
//        for (int i = 0; i < arr.length; i++) {
//            String s = arr[i];
//            if(s.equals(id))
//                return true;
//        }
//        return false;
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