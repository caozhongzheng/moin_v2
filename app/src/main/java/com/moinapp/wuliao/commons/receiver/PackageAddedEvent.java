package com.moinapp.wuliao.commons.receiver;

public class PackageAddedEvent {
	private String mPackageName;
	
	public PackageAddedEvent(String packageName){
		mPackageName = packageName;
	}

	public String getPackageName() {
		return mPackageName;
	}	
}
