package com.moinapp.wuliao.commons.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.IBinder;

public class BaseService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
 	@Override
 	public AssetManager getAssets() {  
 	    return this.getApplicationContext().getAssets(); 
 	}  

 	@Override
 	public Resources getResources() {  
 	    return this.getApplicationContext().getResources();  
 	}
}
