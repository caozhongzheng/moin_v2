package com.moinapp.wuliao.commons.info;


public class CommonDefine {
	// 不同版本对应的标志（和服务器地址有关）
	public final static int VERSION_STYLE_RELEASE 		= 0;		// Release
	public final static int VERSION_STYLE_TEST 			= 1;		// Verify
	public final static String ANDROID_OSID_15 	= "350";			// 平台ID (Android 1.5: 350)
	public final static String ANDROID_OSID_20 	= "351";			// 平台ID (Android 2.0: 351)
	public final static String ANDROID_OSID_30 	= "352";			// 平台ID (Android 3.0: 352)
	public final static String ANDROID_OSID_40 	= "353";			// 平台ID (Android 4.0: 353)


	public static String LocationAPIURL;
	public static String UPDATE_URL;
	public static String APP_CN_URL;
	public static String WEATHER_CN_URL;
	public static String APP_EN_URL;
	public static String WEATHER_EN_URL;
	public static int APP_PORT;
	public static int WEATHER_PORT;

	private final static String BASE_URL_HEAD_RELEASE = "https://prd.mo-image.com/";
	private final static String BASE_URL_HEAD_TEST = "https://dev.mo-image.com/";

	private final static String BASE_SHARE_URL_RELEASE = "http://prd.mo-image.com/";
	private final static String BASE_SHARE_URL_TEST = "http://dev.mo-image.com/";

	/** DEBUG 是否打开 **/
	private static boolean debug = true;

	private static int VERSION_STYLE_TYPE;	// 不同版本

	/**
	 * 每次出包时，需要来修改下测试环境和正是环境的配置
	 */
    static{
    	setStyleType(CommonDefine.VERSION_STYLE_RELEASE);
//    	setStyleType(CommonDefine.VERSION_STYLE_TEST);
    }
    
	static void setStyleType(int versionType){
		CommonDefine.VERSION_STYLE_TYPE = versionType;
	}

	public static int getBuildType() {
		switch(CommonDefine.VERSION_STYLE_TYPE) {
			case CommonDefine.VERSION_STYLE_RELEASE:	// Release
				return CommonDefine.VERSION_STYLE_RELEASE;
			case CommonDefine.VERSION_STYLE_TEST:	// 测试
				return CommonDefine.VERSION_STYLE_TEST;
			default:
				return CommonDefine.VERSION_STYLE_RELEASE;
		}
	}

	public static String getBaseUrl(){
		switch(CommonDefine.VERSION_STYLE_TYPE) {
			case CommonDefine.VERSION_STYLE_RELEASE:	// Release
				return BASE_URL_HEAD_RELEASE;
			case CommonDefine.VERSION_STYLE_TEST:	// 测试
				return BASE_URL_HEAD_TEST;
			default:
				return BASE_URL_HEAD_RELEASE;
		}
	}

	public static String getBaseShareUrl(){
		switch(CommonDefine.VERSION_STYLE_TYPE) {
			case CommonDefine.VERSION_STYLE_RELEASE:	// Release
				return BASE_SHARE_URL_RELEASE;
			case CommonDefine.VERSION_STYLE_TEST:	// 测试
				return BASE_SHARE_URL_TEST;
			default:
				return BASE_SHARE_URL_RELEASE;
		}
	}
}
