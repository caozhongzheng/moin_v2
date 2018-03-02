package com.moinapp.wuliao.utils;

public interface HttpObserver {

	public void onRecvProgress(byte[] data, int length);
	
	public void onHttpResult(int resultCode, byte[] respone);
	
	public void onContentLength(long length);
}
