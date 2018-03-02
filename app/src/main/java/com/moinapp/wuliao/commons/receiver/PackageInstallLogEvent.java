package com.moinapp.wuliao.commons.receiver;

/**
 * 应用安装成功发送的日记  1505
 * 
 */
public class PackageInstallLogEvent {
	private String mPackageName;
	private String mResId;
	
	public PackageInstallLogEvent(String packageName, String resId){
		mPackageName = packageName;
		mResId = resId;
	}

	public String getPackageName() {
		return mPackageName;
	}	
	
	public String getResId() {
		return mResId;
	}	
}
