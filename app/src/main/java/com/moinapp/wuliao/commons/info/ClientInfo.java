package com.moinapp.wuliao.commons.info;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.preference.CommonsPreference;
import com.moinapp.wuliao.utils.PackageUtils;

import java.util.Map;

public class ClientInfo {
	private static ILogger MyLog = LoggerFactory.getLogger("ImageResizer");
	private static IClientInfo sClientInfo;
	private static final int EDITION = 20;
	/**
	 * Baidu; 91; AnZhuo; MyApp; XiaoMi; WanDouJia;360;
	 * */
	private static final String CHANNEL_MOIN = "Moin";
	private static final String CHANNEL_BAIDU = "Baidu";
	private static final String CHANNEL_91 = "91";
	private static final String CHANNEL_ANZHUO = "AnZhuo";
	private static final String CHANNEL_MYAPP = "MyApp";
	private static final String CHANNEL_XIAOMI = "XiaoMi";
	private static final String CHANNEL_WANDOUJIA = "WanDouJia";
	private static final String CHANNEL_360 = "360";
	private static final String CHANNEL = CHANNEL_XIAOMI;
	static {
//		NqTest.init();
		sClientInfo = ClientInfoFactory.getInstance();
	}

	public static int getBusinessId(){
		return sClientInfo.getBusinessId();
	}
	
	public static int getEditionId(){
		return PackageUtils.getCurrentVersion(ApplicationContext.getContext());
	}
	
	public static String getPackageName(){
		return sClientInfo.getPackageName();
	}
	
	public static boolean isGP() {
		return sClientInfo.isGP();
	}

	public static boolean hasLocalTheme(){
		return sClientInfo.hasLocalTheme();
	}
	
	public static boolean isUseBingSearchUrl(){
		return sClientInfo.isUseBingSearchUrl();
	}

	public static void onUpgrade(int lastVer){
		sClientInfo.onUpgrade(lastVer);
	}
	
	public static String getClientLanguage(Context context) {
		Resources resources = context.getResources();
		String lang = null;
		

		int resource_id = resources.getIdentifier("lang", "string",
				context.getPackageName());
		if (resource_id != 0) {
			try {
				lang = resources.getString(resource_id);
				MyLog.d("lang in resource file: " + lang);
			} catch (Exception e) {
				MyLog.e(e);
			}
		}
		if (!TextUtils.isEmpty(lang)){
			lang = lang.replace('_', '-');//防御性
			int n = lang.indexOf("-");
			if (n < 2){//语言代码至少2个字符
				lang = null;
			}
				
		}
		
		if (TextUtils.isEmpty(lang)){
			MyLog.e("client language is unknown!");
			lang = MobileInfo.getMobileLanguage(context);
		}
		
		return lang;
	}
	
	public static String getChannelId() {
		return CHANNEL;
	}

	public static Map<String, Boolean> overrideModuleDefaults() {
		return sClientInfo.overrideModuleDefaults();
	}

	public static String getUID() {
		return CommonsPreference.getInstance().getUID();
	}

	public static void setUID(String uid) {
		CommonsPreference.getInstance().setUID(uid);
	}

	public static String getPassport() {
		return CommonsPreference.getInstance().getPassport();
	}

	public static void setPassport(String passport) {
		CommonsPreference.getInstance().setPassport(passport);
	}

	public static boolean isUserLogin() {
		if (TextUtils.isEmpty(getUID()) || TextUtils.isEmpty(getPassport())) {
			return false;
		} else {
			return true;
		}
	}

}
