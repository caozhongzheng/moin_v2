package com.moinapp.wuliao.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

import java.io.File;

public class NotificationUtil {
    private static ILogger MyLog = LoggerFactory.getLogger("NotificationUtil");
    public static final int NOTIF_ID_THEME = 1;
    public static final int NOTIF_ID_WALLPAPER = 2;
    public static final int NOTIF_ID_MUSTINSTALL = 3;
    public static final int NOTIF_ID_LOCKER = 4;
    public static final int NOTIF_ID_EX_POINT = 5;
    public static final int NOTIF_ID_GET_POINT = 6;
    public static final int NOTIF_ID_INSTALL_FINISH = 7;
    public static final int NOTIF_ID_POINT_THEME = 8;
    public static final int NOTIF_ID_INSTALL_APP = 9;
    public static final int NOTIF_ID_LAUNCH_APP = 10;
    public static final int NOTIF_ID_DOWNLOAD_NOTINSTALL = 11;
    public static final int NOTIF_ID_BATTERY = 12;//power full
    
    public static void showNoti(Context context,int iconId,int titleId,int contentId,Intent i,int notifyId){
    	iconId = getIconResourceIdInHost(context, iconId);            
        Notification notification = new Notification(iconId,context.getString(contentId),System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        PendingIntent intent = PendingIntent.getActivity(context, notifyId/*0*/, i, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context,context.getString(titleId), context.getString(contentId),intent);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.cancel(notifyId);
        nm.notify(notifyId, notification);
    }

    public static void showNoti(Context context,int iconId,String title,String content,Intent i,int notifyId){
    	iconId = getIconResourceIdInHost(context, iconId);
    	Notification notification = new Notification(iconId,content,System.currentTimeMillis());
        notification.flags = Notification.FLAG_AUTO_CANCEL;

        PendingIntent intent = PendingIntent.getActivity(context, notifyId/*0*/, i, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(context,title, content,intent);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.cancel(notifyId);
        nm.notify(notifyId, notification);
    }
    
    public static void cancleNoti(Context context,int notifyId){
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(notifyId);
    }
    
    public static void showInstallAppNotifi(Context context,String resid,String path){
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        String name = "app_name";//AppManager.getInstance(context).getAppNameByResId(resid);
        showNoti(context, R.drawable.download_notfi_icon,
                name,
                context.getString(R.string.install_app_tip),
                intent,
                NotificationUtil.NOTIF_ID_INSTALL_APP);
    }

    /**
     * 当桌面APK直接内嵌在某个宿主APK里时（比如内嵌在微乐壁纸里），对外发Notification时，iconId必须是宿主应用R类里的资源ID
     */
    private static int getIconResourceIdInHost(Context context, int iconResourceId){
    	String packageName = context.getPackageName();
    	
    	//不是内嵌在宿主APK里
    	if (packageName != null && packageName.equals(ClientInfo.getPackageName())){
    		return iconResourceId;
    	}
    	
    	String resourceName = context.getResources().getResourceEntryName(iconResourceId);
    	
    	int result = -1;
    	if (resourceName == null || resourceName.isEmpty()){
    		return result;
    	}
    	
		try {
			Resources resources = context.getPackageManager().getResourcesForApplication(packageName);
			if (resources != null) {
	        	result = resources.getIdentifier(resourceName, "drawable", packageName);
	        }
		} catch (NameNotFoundException e) {
			MyLog.e(e);
		}
        
        return result;
    }


}
