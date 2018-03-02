package com.moinapp.wuliao.modules.login;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.info.MobileInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.login.model.UserInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liujiancheng on 15/5/7.
 */
public class LoginUtils {

    public static final ILogger MyLog = LoggerFactory.getLogger("ll");

    public static Map registerInfo2Map(int type, String name, String md5, String smscode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(LoginConstants.USERNAME, name);
        map.put(LoginConstants.PASSWORD, md5);
        map.put(LoginConstants.CODE, smscode);
        map.put(LoginConstants.DEVICEINFO, getDeviceInfo());
        map.put(LoginConstants.APPINFO, getAppInfo());
        return map;
    }

    public static Map ourLogin2Map(String name, String md5) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(LoginConstants.USERNAME, name);
        map.put(LoginConstants.PASSWORD, md5);

        return map;
    }

    public static Map thirdLogin2Map(String platform, Object token) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(LoginConstants.TOKEN, getThirdLoginJson(platform, (Bundle) token));
        return map;
    }

    public static Map userInfo2Map(UserInfo info) {
        Map<String, String> map = new HashMap<String, String>();
        if (!TextUtils.isEmpty(info.getName())) {
            map.put(LoginConstants.NAME, info.getName());
        }
        if (!TextUtils.isEmpty(info.getPhone())) {
            map.put(LoginConstants.PHONE, info.getPhone());
        }

        if (!TextUtils.isEmpty(info.getEmail())) {
            map.put(LoginConstants.EMAIL, info.getEmail());
        }

        if (!TextUtils.isEmpty(info.getNickname())) {
            map.put(LoginConstants.NICKNAME, info.getNickname());
        }

        if (!TextUtils.isEmpty(info.getAges())) {
            map.put(LoginConstants.AGES, info.getAges());
        }

        if (!TextUtils.isEmpty(info.getSex())) {
            map.put(LoginConstants.SEX, info.getSex());
        }

        if (info.getLocation() != null) {
            map.put(LoginConstants.LOCATION, (new Gson()).toJson(info.getLocation()));
        }

        if (info.getSignature() != null) {
            map.put(LoginConstants.SIGNATURE, info.getSignature());
        }

        if (info.getStars() != -1) {
            map.put(LoginConstants.STARS, String.valueOf(info.getStars()));
        }
        return map;
    }

    public static Map phoneRetievePassword2Map(String phone, String smscode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(LoginConstants.PHONE, phone);
        map.put(LoginConstants.SMSCODE, smscode);

        return map;
    }

    public static Map emailRetievePassword2Map(String email, String emailcode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(LoginConstants.EMAIL, email);
        map.put(LoginConstants.EMAILCODE, emailcode);

        return map;
    }

    private static String getThirdLoginJson(String platform, Bundle b) {
        ThridToken token = new ThridToken();
        token.setName(platform);

        ThirdInfo info = new ThirdInfo();
        info.access_token = b.getString(LoginConstants.ACCESS_TOKEN);
        info.refresh_token = b.getString(LoginConstants.REFRESH_TOKEN);
        info.openid = b.getString(LoginConstants.OPEN_ID);
        MyLog.i(LoginConstants.EXPIRE_IN + " " + b.getString(LoginConstants.EXPIRE_IN));
//        MyLog.i(LoginConstants.REFRESH_TOKEN_EXPIRES + " " + b.getString(LoginConstants.REFRESH_TOKEN_EXPIRES));
//        MyLog.i(LoginConstants.EXPIRE_IN + " long, " + b.getLong(LoginConstants.EXPIRE_IN));
        MyLog.i(LoginConstants.REFRESH_TOKEN_EXPIRES + " long, " + b.getLong(LoginConstants.REFRESH_TOKEN_EXPIRES));
        info.expires_in = Long.parseLong(b.getString(LoginConstants.EXPIRE_IN));
        info.refresh_token_expires = b.getLong(LoginConstants.REFRESH_TOKEN_EXPIRES);
        info.unionid = b.getString(LoginConstants.UNION_ID);
        info.uid = b.getString(LoginConstants.UID);

        token.setInfo(info);
        Gson gson = new Gson();
        return gson.toJson(token);
    }

    public static String getDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.osVersion = Build.VERSION.RELEASE;
        deviceInfo.width = MobileInfo.getDisplayWidth(ApplicationContext.getContext());
        deviceInfo.height = MobileInfo.getDisplayHeight(ApplicationContext.getContext());
        deviceInfo.net = MobileInfo.getNetworkType(ApplicationContext.getContext());
        deviceInfo.language = ClientInfo.getClientLanguage(ApplicationContext.getContext());
        deviceInfo.country = MobileInfo.getCountry(ApplicationContext.getContext());
        deviceInfo.imei = MobileInfo.getImei(ApplicationContext.getContext());
        deviceInfo.imsi = MobileInfo.getImsi(ApplicationContext.getContext());
        deviceInfo.mac = MobileInfo.getLocalMacAddress(ApplicationContext.getContext());
        deviceInfo.manufacturer = Build.MANUFACTURER;
        deviceInfo.model = Build.MODEL;

        Gson gson = new Gson();
        return gson.toJson(deviceInfo);
    }

    public static String getAppInfo() {
        AppInfo appInfo = new AppInfo();
        appInfo.channel = ClientInfo.getChannelId();
        appInfo.edition = ClientInfo.getEditionId();

        Gson gson = new Gson();
        return gson.toJson(appInfo);
    }

    public static class ThridToken {
        private String name;
        private ThirdInfo info;

        public void setName(String name) { this.name = name; }
        public String getName() { return  this.name; }
        public void setInfo(ThirdInfo info) { this.info =  info; }
        public ThirdInfo getInfo() { return this.info; }
    }

    public static class ThirdInfo {
        public String access_token;
        public String refresh_token;
        public String openid;
        public long expires_in;
        public long refresh_token_expires;
        public String unionid;
        public String uid;
    }

    public static class DeviceInfo {
        public String osName;
        public String osVersion;
        public int width;
        public int height;
        public String net;
        public String language;
        public String country;
        public String imsi;
        public String imei;
        public String mac;
        public String manufacturer;
        public String model;
    }

    public static class AppInfo {
        public int edition;
        public String channel;
    }
}
