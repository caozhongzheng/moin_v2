package com.moinapp.wuliao.commons.https;

import java.util.Map;

public interface IHttpOperation {
	public void doGet(String url);
	
	public void doPut(String url, Map data);
	
	public void doPost(String url, Map data);
	
	public void doDelete(String url);

	public void uploadFile(String url, String key, String filePath);
}
