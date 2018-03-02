package com.moinapp.wuliao.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

import java.util.List;

public class ProcessUtils {
	private static ILogger MyLog = LoggerFactory.getLogger("ProcessUtils");
    public static String getCurrentProcessName(Context context) {  
    	int pid = android.os.Process.myPid();  
    	ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
    	for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
	    	if (appProcess.pid == pid) {  
	    		return appProcess.processName;  
	    	}  
    	}  
    	return null;  
    }
    
    public static void killProcess(Context context, String processName, boolean immediately) {
    	int pid = -1;
    	
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        int myUid = android.os.Process.myUid();
        for(ActivityManager.RunningAppProcessInfo info:list){
            if(info.uid == myUid && info.processName.equals(processName)){
                pid = info.pid;
                break;
            }
        }
        
        if(pid == -1)
        	return;
        
        MyLog.i("killProcess pid=" + pid + " ,processName=" + processName + " ,immediately=" + immediately);

        
        if (immediately){
        	android.os.Process.killProcess(pid);
        	return;
        }

        final int ppid = pid;
		ApplicationContext.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (ppid != 0) {
					try {
						android.os.Process.killProcess(ppid);
					} catch (Exception e) {
						MyLog.e(e);
					}
				}

			}
		}, 1000);
	}
}
