package com.moinapp.wuliao.modules.stat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultLauncherStat {
	/**
	 * 返回默认桌面的包名，如果没有指定默认桌面，返回null
	 */
	public static String getPackageName(Context context){
        PackageManager pm = context.getPackageManager();
        Intent i = new Intent("android.intent.action.MAIN");
        i.addCategory("android.intent.category.HOME");
        List<ResolveInfo> list = pm.queryIntentActivities(i,0);
        List<IntentFilter> intentFilters = new ArrayList<IntentFilter>();
        List<ComponentName> componentNames = new ArrayList<ComponentName>();
        for(Iterator<ResolveInfo> iter = list.iterator();iter.hasNext();){
            ResolveInfo resolveInfo = (ResolveInfo) iter.next();
            intentFilters.clear();
            componentNames.clear();
            pm.getPreferredActivities(intentFilters,componentNames,resolveInfo.activityInfo.packageName);
            for(Iterator<IntentFilter> itt = intentFilters.iterator();itt.hasNext();){
                IntentFilter filter = (IntentFilter) itt.next();
                if(filter.hasAction("android.intent.action.MAIN") && filter.hasCategory("android.intent.category.HOME")){
                    return resolveInfo.activityInfo.packageName;
                }
            }
        }
        return "";
    }

}
