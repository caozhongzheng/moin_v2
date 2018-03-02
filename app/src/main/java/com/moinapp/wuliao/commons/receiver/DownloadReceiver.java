package com.moinapp.wuliao.commons.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.moinapp.wuliao.commons.AppConstants;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.service.BackgroundService;
import com.moinapp.wuliao.modules.push.PushActionLogConstants;
import com.moinapp.wuliao.modules.push.model.Push;
import com.moinapp.wuliao.modules.stat.StatManager;
import com.moinapp.wuliao.utils.CommonMethod;
import com.moinapp.wuliao.utils.NetworkUtils;

public class DownloadReceiver extends BroadcastReceiver{
	private static ILogger MyLog = LoggerFactory.getLogger("DownloadReceiver");

	public static final String ACTION_CLICK_PUSH = "moin.click.push";
    public static final String ACTION_DOWNLOAD_PUSH = "moin.download.push";
    public static final String ACTION_UNLIKE_PUSH = "moin.unlike.push";
    /** 资源类型：0应用 1壁纸 2主题 */
	public static final String KEY_PUSH = "push";
	public static final String KEY_FROM_PUSH = "from_push";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		MyLog.i("DownloadReceiver receive action=" + action);
		if (action.equals(android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			long reference = intent.getLongExtra(android.app.DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			MyLog.i("Download complete refer=" + reference);

            EventBus.getDefault().post(new DownloadCompleteEvent(reference));
		} else if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            MyLog.i("network changed!");
            if (NetworkUtils.isConnected(context)) {
                Intent i = new Intent(context,BackgroundService.class);
				i.setAction(BackgroundService.REQUEST_PERIOD_CHECK_ACTION);
				context.startService(i);
			}
            EventBus.getDefault().post(new ConnectivityChangeEvent());
//		} else if(action.equals(Intent.ACTION_PACKAGE_ADDED)){
//            String packageName = intent.getDataString().substring(8);
//            MyLog.i("installed packagename: " + packageName);
//            EventBus.getDefault().post(new PackageAddedEvent(packageName));
//
        } else if(action.equals(ACTION_DOWNLOAD_PUSH) || action.equals(ACTION_CLICK_PUSH)){
        	Push push = (Push) intent.getSerializableExtra(KEY_PUSH);
        	if(push == null){
        		MyLog.i("push is null when click item or click download btn");
        		return;
        	}
        	processPushClick(context, push);
        	cancelPushNotification(context, push.getStrId());
        	if(action.equals(ACTION_DOWNLOAD_PUSH))
                StatManager.getInstance().onAction(StatManager.TYPE_AD_STAT,
                		PushActionLogConstants.ACTION_LOG_1905, push.getStrId(), StatManager.ACTION_CLICK, null);
        	else
                StatManager.getInstance().onAction(StatManager.TYPE_AD_STAT,
                		PushActionLogConstants.ACTION_LOG_1902, push.getStrId(), StatManager.ACTION_CLICK, null);
        	
        } else if(action.equals(ACTION_UNLIKE_PUSH)){
        	Push push = (Push) intent.getSerializableExtra(KEY_PUSH);
        	if(push == null){
        		MyLog.i("push is null when click unlike push");
        		return;
        	}
        	String pushid = push.getStrId();
        	cancelPushNotification(context, pushid);
            StatManager.getInstance().onAction(StatManager.TYPE_AD_STAT,
            		PushActionLogConstants.ACTION_LOG_1904, pushid, StatManager.ACTION_CLICK, null);
            MyLog.i("unlike push ,pushid: " + pushid);
//        }else if(action.equals(Intent.ACTION_PACKAGE_REMOVED)){
//            String packageName = intent.getDataString().substring(8);
//            EventBus.getDefault().post(new PackageRemoveEvent(packageName));
        }else if(action.equals(Intent.ACTION_BOOT_COMPLETED)){
            EventBus.getDefault().post(new BootCompletedEvent());
        }
	}

    private void cancelPushNotification(Context context, String pushid) {
		// TODO Auto-generated method stub
    	String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
		mNotificationManager.cancel(pushid.hashCode());
	}

	private void processPushClick(Context context, Push push) {
		// TODO Auto-generated method stub
		switch(push.getJumpType()){
		// push信息是应用广告的话,那么跳转事件应该是限制进入store详情,进入时,取不到广告详情.[白板]
		case 3://3--打开浏览器
			pushOpenBrowser(context, push.getDownUrl());
			break;
		case 0://0--打开store详情 
			/** 资源类型：0应用 1壁纸 2主题 */
			pushOpenStore(context, push);
			break;
		case 2://2 直接下载（本地下载）
			/** 资源类型：0应用 1壁纸 2主题 */
			String linkResId = push.getLinkResourceId();
			if(linkResId == null || linkResId.isEmpty()) {
				if(push.getDownUrl() == null || TextUtils.isEmpty(push.getDownUrl())){
		    		MyLog.i("push download url is null");
		    		return;
		    	} else {
		    		MyLog.i("pushDirectDownload url is:" + push.getDownUrl());
		    		pushDirectDownload(context, push);
                    StatManager.getInstance().onAction(StatManager.TYPE_AD_STAT,
                    		PushActionLogConstants.ACTION_LOG_1903, push.getStrId(), StatManager.ACTION_CLICK, null);
		    	}
			} else {
				pushOpenDownload(context, push);
			}
			break;
		default:
			break;
		}
		
		
	}
	/**进入push的直接下载*/
	private void pushDirectDownload(Context context, Push push) {
//		// TODO Auto-generated method stub
//		String SDCardPath = CommonMethod.getSDcardPath(context);
//        if(SDCardPath == null)
//        	SDCardPath = CommonMethod.getSDcardPathFromPref(context);
//        long longSize = -1;
//
//        App app = new App();
//		app.setStrId(push.getStrId());
//		app.setStrAppUrl(push.getDownUrl());
//		String strAppPath = new StringBuilder().append(SDCardPath).append(AppConstant.STORE_IMAGE_LOCAL_PATH_APP).toString();
//        app.setStrAppPath(new StringBuilder().append(strAppPath).append(push.getStrId())
//                .append(".apk").toString());
//		app.setStrName(push.getTitle());
//		app.setLongSize(longSize);
//		app.setIntSourceType(AppConstants.STORE_MODULE_TYPE_PUSH);
//		app.setIntDownloadActionType(AppManager.DOWNLOAD_ACTION_TYPE_DIRECT);
//		app.setStrIconUrl(push.getIcon());
//        app.setStrPackageName(push.getPackageName());
//
//		Long downloadId = AppManager.getInstance(context).downloadApp(app);
	}

	/**进入push的下载store连接资源*/
	private void pushOpenDownload(Context context, Push push) {
		// TODO Auto-generated method stub
		String SDCardPath = CommonMethod.getSDcardPath(context);
        if(SDCardPath == null)
        	SDCardPath = CommonMethod.getSDcardPathFromPref(context);
        long longSize = -1;
		switch(push.getIntType()){
    	case AppConstants.PUSH_CACHE_TYPE_APP:
//    		App app = new App();
//			app.setStrId(push.getLinkResourceId());
//			app.setStrAppUrl(push.getDownUrl());
//			String strAppPath = new StringBuilder().append(SDCardPath).append(AppConstant.STORE_IMAGE_LOCAL_PATH_APP).toString();
//			app.setStrAppPath(strAppPath);
//			app.setStrName(push.getTitle());
//			app.setLongSize(longSize);
//			app.setIntSourceType(AppConstants.STORE_MODULE_TYPE_PUSH);
//
//   			MyDownloadManager.getInstance(context).downloadApp(app);
            StatManager.getInstance().onAction(StatManager.TYPE_AD_STAT,
            		PushActionLogConstants.ACTION_LOG_1903, push.getStrId(), StatManager.ACTION_CLICK, null);
   			break;
    	}
	}

	/**进入push的浏览器详情*/
	private void pushOpenBrowser(Context context, String url){
		if(url == null || url.isEmpty()){
			MyLog.i("url is null or empty");
		}
		Uri uri = Uri.parse(url);
        Intent browser = new Intent(Intent.ACTION_VIEW, uri);
        browser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(browser);
	}
	
	/**进入push的store详情*/
	private void pushOpenStore(Context context, Push push){
		if(push == null)
			return;
    	MyLog.i("view push detail in store:type=" + push.getIntType() + ",pushid=" + push.getStrId() + ",resid=" + push.getLinkResourceId());
    	Intent detailIntent = new Intent();
    	switch(push.getIntType()){
		case AppConstants.PUSH_CACHE_TYPE_APP:
//			detailIntent.setClass(context, AppDetailActivity.class);
//			detailIntent.setAction(AppDetailActivity.INTENT_ACTION);
//			detailIntent.putExtra(AppDetailActivity.KEY_APP_ID, push.getLinkResourceId());
//			detailIntent.putExtra(AppDetailActivity.KEY_APP_SOURCE_TYPE, AppConstants.STORE_MODULE_TYPE_PUSH);
			break;

		}
    	detailIntent.putExtra(KEY_FROM_PUSH, true);
    	detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(detailIntent);
	}


}
