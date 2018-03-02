package com.moinapp.wuliao.commons.mydownloadmanager;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.db.DataProvider;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moduleframework.AbsManager;
import com.moinapp.wuliao.commons.mydownloadmanager.table.DownloadTable;
import com.moinapp.wuliao.commons.receiver.DownloadCompleteEvent;
import com.moinapp.wuliao.modules.cosplay.model.CosplayResource;
import com.moinapp.wuliao.modules.cosplay.model.CosplayThemeInfo;
import com.moinapp.wuliao.utils.PackageUtils;
import com.moinapp.wuliao.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 下载类
 * 
 */
public class MyDownloadManager extends AbsManager{
	private static ILogger MyLog = LoggerFactory.getLogger("MyDownloadManager");
	private static MyDownloadManager sDlMgr;
	private Context mContext;
	private DownloadManager mDownloader;
	private ContentObserver mContentObserver; 
	private Map<Long, ObserverWrapper> mObservers;
	private static final Uri DOWNLOAD_MANAGER_CONTENT_URI = Uri.parse("content://downloads/my_downloads");

	public static final int STATUS_NONE = 0;// 无效的downloadId，没有找到该下载
	public static final int STATUS_PENDING = 1;// 尚未开始下载，等待中
	public static final int STATUS_RUNNING = 2;// 下载中
	public static final int STATUS_PAUSED = 3;// 下载暂停
	public static final int STATUS_FAILED = 4;// 下载失败
	public static final int STATUS_SUCCESSFUL = 5;// 已下载
	
	public static final String MIME_TYPE_APK = "application/vnd.android.package-archive";
	public static final int NO_NETWORK = 0;
	
	public static final int DOWNLOAD_TYPE_COSPLAY = 0;
    public static final int DOWNLOAD_FINISH = 1;

    private Set<Long> mExistedDownloadIds = new HashSet<Long>();

    @Override
    public void init() {
        EventBus.getDefault().register(this);
    }

    private class ObserverWrapper{
        public Set<IDownloadObserver> observer;
        public long bytesSoFar;
        public long status;

        public ObserverWrapper(){
            observer = new HashSet<IDownloadObserver>();
            bytesSoFar = -1;//unknown
            status = -1;//unknown
        }

        public void addObserver(IDownloadObserver ob){
            observer.add(ob);
        }
    }
	
	private MyDownloadManager(Context context) {
		mContext = context;
		mDownloader = (DownloadManager) mContext
				.getSystemService(Context.DOWNLOAD_SERVICE);
//		mDownloader = DownloadManager.getInstance(context.getContentResolver(), context.getPackageName());
		mContentObserver = new MyObserver();
		mObservers = new ConcurrentHashMap<Long, ObserverWrapper>();
        EventBus.getDefault().register(this);
	}

	public synchronized static MyDownloadManager getInstance(Context context) {
		if (sDlMgr == null) {
			sDlMgr = new MyDownloadManager(context.getApplicationContext());
		}
		return sDlMgr;
	}

    public static class DownloadParameter{
        public int type;
        public String resId;
		public int resVersion;
        public String downloadUrl;
        public String iconUrl;
        public String desPath;
        public String name;
        public long totalSize;
        public int networkflag;
        public boolean background;
        public String packageName;
    }
	
    private int getDownloadNetwork() {
		ConnectivityManager connectivity = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null == connectivity) {
			return NO_NETWORK;
		}
		
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (null == info) {
			return NO_NETWORK;
		}

		// if current network is WIFI, set download network type to NETWORK_WIFI, indicates download will only proceed under WIFI
		// if current network is MOBIE(2g/3g) , set download network type to NETWORK_WIFI|NETWORK_MOBILE, indicates download will always proceed
		if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			MyLog.i("current network: WIFI");
			return Request.NETWORK_WIFI;
		} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			MyLog.i("current network: 3g");
			return Request.NETWORK_MOBILE | Request.NETWORK_WIFI;
		} else {
			return NO_NETWORK;
		}

    }

    public void flagDownloadFinish(long downloadId){
        ContentValues value = new ContentValues();
        value.put(DownloadTable.DOWNLOAD_IS_FINISH,DOWNLOAD_FINISH);
        mContext.getContentResolver().update(DownloadTable.TABLE_URI,value,DownloadTable.DOWNLOAD_DOWNLOAD_ID + " = ?",new String[]{String.valueOf(downloadId)});
    }

    public boolean isDownloadCompleted(String resId) {
    	if (TextUtils.isEmpty(resId)) {
    		return false;
    	}
    	boolean result = false;
		Cursor cursor = null;
		try {
			ContentResolver contentResolver = mContext.getContentResolver();
			cursor = contentResolver.query(DownloadTable.TABLE_URI, null,
                    DownloadTable.DOWNLOAD_RES_ID + " = ?", new String[] { resId },
                    DownloadTable._ID + " DESC LIMIT 1 OFFSET 0");
			if (cursor != null && cursor.moveToNext()) {
                Long finish = cursor.getLong(cursor.getColumnIndex(DownloadTable.DOWNLOAD_IS_FINISH));
                if (finish == DOWNLOAD_FINISH) {
                	result = true;
                }
			}
		} catch (Exception e) {
			MyLog.e(e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
    	return result;
    }

	/**
	 * 下载大咖秀需要的资源
	 * @param cosplay
	 * @param networkflag
	 * @return
	 */
	public Long downloadCosplayRes(String resId, int version, CosplayThemeInfo cosplay,int networkflag) {
		if (cosplay == null) {
			return null;
		}

		DownloadParameter para = new DownloadParameter();
		para.type = DOWNLOAD_TYPE_COSPLAY;
		para.resId = resId;
		para.resVersion = version;
		para.downloadUrl = cosplay.getResource().getUri();
		if (cosplay.getIcon() != null) {
			para.iconUrl = cosplay.getIcon().getUri();
		}
		para.desPath = cosplay.getLocalPath();
		para.name = cosplay.getName();
		para.networkflag = networkflag;
		para.background = false;

		return download(para);
	}

	/**para isBackground: true 静默下载，不在通知栏显示进度信息
	 *
	*/
	public Long download(DownloadParameter para) {
		Long result = null;

		if (para.downloadUrl == null) {
			return result;
		}

		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
				|| para.desPath != null && para.desPath.startsWith("null")) {
			if (Looper.myLooper() == Looper.getMainLooper()) {
				ToastUtils.toast(mContext, mContext.getString(R.string.label_no_sdcard));
			}
			MyLog.i("MyDownloadManager download failed: no sdcard, url=" + para.downloadUrl + " ,destPath= " + para.desPath);
			return result;
		}
		try {
			Uri uri = Uri.parse(para.downloadUrl);
			Request request = new Request(uri);
			//request.setMimeType(MIME_TYPE_APK);
			request.setAllowedNetworkTypes(para.networkflag);
			if (!para.background) {
				request.setTitle(para.name);
				request.setDescription(" ");//为了使DownloadManager在通知栏显示的进度条包括百分比，好像Description需要设置内容
				request.setVisibleInDownloadsUi(true);
				request.setShowRunningNotification(true);
			} else{
				request.setVisibleInDownloadsUi(false);
				request.setShowRunningNotification(false);								
			}
            String dir = para.desPath.substring(0, para.desPath.lastIndexOf("/"));
            File downloadDir = new File(dir);
            if(!downloadDir.exists()){
                downloadDir.mkdirs();
            }

			File file = new File(para.desPath);
            if (file.exists()){
				file.delete();
			}
			request.setDestinationUri(Uri.fromFile(file));
        	
			Long id = getDownloadIdByRes(para.resId, para.resVersion, para.downloadUrl);
			long downloadId = mDownloader.enqueue(request);

			Builder b = null;
			ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
			ContentValues values = new ContentValues();
			values.put(DownloadTable.DOWNLOAD_DOWNLOAD_ID, downloadId);
			values.put(DownloadTable.DOWNLOAD_TYPE, para.type);
            values.put(DownloadTable.DOwNLOAD_ICON_URL,para.iconUrl);
            values.put(DownloadTable.DOWNLOAD_NAME,para.name);				
			if (id != null) {
				Builder del = ContentProviderOperation.newDelete(DownloadTable.TABLE_URI)
						.withSelection(DownloadTable.DOWNLOAD_DOWNLOAD_ID + " =? ", new String[]{String.valueOf(id)});
				ops.add(del.build());
			}
			values.put(DownloadTable.DOWNLOAD_DEST_PATH, para.desPath);
			values.put(DownloadTable.DOWNLOAD_TOTAL_SIZE, para.totalSize);
			values.put(DownloadTable.DOWNLOAD_URL, para.downloadUrl);
			values.put(DownloadTable.DOWNLOAD_RES_ID, para.resId);
			values.put(DownloadTable.DOWNLOAD_RES_VERSION, para.resVersion);
			values.put(DownloadTable.DOWNLOAD_PACKAGENAME,para.packageName);
			values.put(DownloadTable.DOWNLOAD_IS_FINISH, 0);
			values.put(DownloadTable.DOWNLOAD_SHOWFLAG, 0);
			b = ContentProviderOperation.newInsert(DownloadTable.TABLE_URI).withValues(values);
			ops.add(b.build());
			mContext.getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY, ops);
			
			MyLog.d("MyDownloadManager downloadId:" + downloadId + ", url= " + para.downloadUrl + " ,destPath = " + para.desPath);
			result = downloadId;
		} catch (Exception e) {
			MyLog.d("MyDownloadManager download failed: url= " + para.downloadUrl + " ,destPath = " + para.desPath);
			e.printStackTrace();
			MyLog.e(e);
		}
		return result;
	}
	
	public Long getDownloadId(String url) {
		Long result = null;
		Cursor cursor = null;
		try {
			ContentResolver contentResolver = mContext.getContentResolver();
			cursor = contentResolver.query(DownloadTable.TABLE_URI, null,
                    DownloadTable.DOWNLOAD_URL + " = ?", new String[] { url },
                    DownloadTable._ID + " DESC LIMIT 1 OFFSET 0");
			if (cursor != null && cursor.moveToNext()) {
                result = cursor.getLong(DownloadTable.DOWNLOAD_DOWNLOAD_ID_INDEX);
			}
		} catch (Exception e) {
			MyLog.e(e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return result;
	}
	
	public Long getDownloadIdByResID(String resId) {
		Long result = null;
		Cursor cursor = null;
		try {
			ContentResolver contentResolver = mContext.getContentResolver();
			cursor = contentResolver.query(DownloadTable.TABLE_URI, null,
                    DownloadTable.DOWNLOAD_RES_ID + " = ?", new String[] { resId },
                    DownloadTable._ID + " DESC LIMIT 1 OFFSET 0");
			if (cursor != null && cursor.moveToNext()) {
				result = cursor.getLong(DownloadTable.DOWNLOAD_DOWNLOAD_ID_INDEX);
			}
		} catch (Exception e) {
			MyLog.e(e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return result;
	}

	public Long getDownloadIdByRes(String resId, int resVersion, String uri) {
		Long result = null;
		Cursor cursor = null;
		try {
			ContentResolver contentResolver = mContext.getContentResolver();
			cursor = contentResolver.query(DownloadTable.TABLE_URI, null,
					DownloadTable.DOWNLOAD_RES_ID + " = ?" + " AND " + DownloadTable.DOWNLOAD_RES_VERSION + " = " + resVersion
					+ " AND " + DownloadTable.DOWNLOAD_URL + " = ?",
					new String[] { resId, uri },
					DownloadTable._ID + " DESC LIMIT 1 OFFSET 0");
			if (cursor != null && cursor.moveToNext()) {
				result = cursor.getLong(DownloadTable.DOWNLOAD_DOWNLOAD_ID_INDEX);
			}
		} catch (Exception e) {
			MyLog.e(e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return result;
	}

	// 取消一个下载
	public void cancelDownload(long downloadId){
		mDownloader.remove(downloadId);
		MyLog.d("cancelDownload downloadId=" + downloadId);
		ObserverWrapper observerWrapper = mObservers.get(downloadId);
		if (observerWrapper != null) {
			int[] ds = getBytesAndStatus(downloadId);
			for (IDownloadObserver ob : observerWrapper.observer) {
				ob.onChange(downloadId,ds[0]==1,ds[1],ds[2],ds[3]);
			}
		}
		
	}

	/**
	 * 将DownLoadManager的Status转成MyDownLoadManager的Status
	 * @param status
	 * @return MyDownLoadManager的STATUS_*
	 */
	private int convertStatus(int status){
		int result = STATUS_NONE;
		
		switch (status) {
		case DownloadManager.STATUS_FAILED:
			result = STATUS_FAILED;
			break;
		case DownloadManager.STATUS_PAUSED:
			result = STATUS_PAUSED;
			break;
		case DownloadManager.STATUS_PENDING:
			result = STATUS_PENDING;
			break;
		case DownloadManager.STATUS_RUNNING:
			result = STATUS_RUNNING;
			break;
		case DownloadManager.STATUS_SUCCESSFUL:
			result = STATUS_SUCCESSFUL;
			break;
		default:
			break;
		}
		
		return result;
	}

    public void registerDownloadObserver(Long downloadId, IDownloadObserver observer){
        if (downloadId == null){
            return;
        }

        int[] bytesAndStatus = getBytesAndStatus(downloadId);
        //Status为STATUS_SUCCESSFUL的就不用监控了
        if (bytesAndStatus[0] == 1 && bytesAndStatus[1] != STATUS_SUCCESSFUL){
            ObserverWrapper ob = mObservers.get(downloadId);
            if(ob != null){
                ob.addObserver(observer);
                mObservers.put(downloadId, ob);
            }else{
                ObserverWrapper o = new ObserverWrapper();
                o.addObserver(observer);
                mObservers.put(downloadId, o);
            }

            mContext.getContentResolver().registerContentObserver(DOWNLOAD_MANAGER_CONTENT_URI, true,
					mContentObserver);
        }
    }
	
	/**
	 * 去除一个下载进度监听
	 * @param downloadId
	 */
	public void unregisterDownloadObserver(Long downloadId){	
		if (downloadId == null){
			return;
		}
		
		mObservers.remove(downloadId);
		if (mObservers.size() <= 0) {
			mContext.getContentResolver().unregisterContentObserver(mContentObserver);
		}
	}
	
	
	/**
	 * 下载进度状态监听器
	 */
	class MyObserver extends ContentObserver {

		public MyObserver(){
			super(null);
		}

		@Override
		public void onChange(boolean selfChange) {
            notifyChange();
		}

	}
	
	private long[] toPrimitiveArray(Set<Long> values) {
	    long[] result = new long[values.size()];
	    int i = 0;
	    for (Iterator<Long> iter = values.iterator();iter.hasNext();){
            result[i++] = iter.next();
        }

	    return result;
	}
	
    public void notifyChange(){
        long[]  downloadIds = toPrimitiveArray(mObservers.keySet());
        if (downloadIds == null || downloadIds.length == 0) 
        	return;
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadIds);
        Cursor c = null;
		if(query == null)
			return;
		
		try {
			c = mDownloader.query(query);
            if (c == null){
				return;
			}
            mExistedDownloadIds.clear();
			while(c.moveToNext()){
				Long id = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID));
				mExistedDownloadIds.add(id);
				long bytesSoFar = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
				long totalBytes = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
	            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

				if (mObservers.containsKey(id)){
					ObserverWrapper wrapper = mObservers.get(id);
					long oldBytesSoFar = wrapper.bytesSoFar;
					long oldStatus = wrapper.status;
					MyLog.d(" id=" + id + " oldBytesSoFar:" + oldBytesSoFar + " bytesSoFar" + bytesSoFar +
							" oldStatus" + oldStatus + ", status:" + status);
					if (bytesSoFar != oldBytesSoFar || status != oldStatus){
						wrapper.bytesSoFar = bytesSoFar;
						wrapper.status = status;

                        for(Iterator<IDownloadObserver> iter = wrapper.observer.iterator();iter.hasNext();){
                            IDownloadObserver ob = iter.next();
                            try {
                            	ob.onChange(id,true, convertStatus(status), bytesSoFar,totalBytes);
    						} catch (Exception e) {
    							MyLog.e(e);
    						}
                        }
					}
					// remove observer when the download is failed or completed
					if (status == DownloadManager.STATUS_FAILED || status == DownloadManager.STATUS_SUCCESSFUL){
		            	mObservers.remove(id);
		            }	
				}
			}			
		}catch (Exception e) {
			MyLog.e("MyDownloadManager notifyChange err:" + e);
            e.printStackTrace();
		} finally {
			if (c != null) {
				c.close();
			}
		}
		
		//通知已被删除的下载
		for(long id : downloadIds){
			if (!mExistedDownloadIds.contains(id)){
				if (mObservers.containsKey(id)) {
					ObserverWrapper wrapper = mObservers.get(id);
					for (Iterator<IDownloadObserver> iter = wrapper.observer
							.iterator(); iter.hasNext();) {
						IDownloadObserver ob = iter.next();
						try { 
							int[] ds = getBytesAndStatus(id);
							ob.onChange(id,ds[0]==1,ds[1],ds[2],ds[3]);
						} catch (Exception e) {
							MyLog.e(e);
						}
					}
					mObservers.remove(id);
				}
			}
		}
    }
	
	
	/**
	 * 获取一个下载的进度信息
	 * @param downloadId
	 * @return int[4]{能否查到进度信息（1能,0不能），MyDownloadManager的Status，已下载字节数，总字节数}
	 */
	public int[] getBytesAndStatus(Long downloadId) {
		int[] bytesAndStatus = new int[] {0, -1, -1, 0 };
		if (downloadId == null){
			return bytesAndStatus;
		}	

		DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
		Cursor c = null;
		try {
			c = mDownloader.query(query);
			if (c != null && c.moveToFirst()) {
				bytesAndStatus[0] = 1;
				bytesAndStatus[1] = convertStatus(c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
				bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
				bytesAndStatus[3] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
				MyLog.d("mydownmanager downloadId:" + downloadId + ", staus:" + bytesAndStatus[1]
						+ ", downsize:" + bytesAndStatus[2] + " tot:" + bytesAndStatus[3]);
			}
			
			if (bytesAndStatus[1] == STATUS_SUCCESSFUL){
				String local_uri = c.getString((c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
				if (local_uri != null) {
					Uri uri = Uri.parse(local_uri);
					File destFile = new File(uri.getPath());
					if (!destFile.exists()) {// SD卡上的文件不存在、SD卡拔除、换SD卡等情况
						bytesAndStatus[0] = 0;
					}
				}
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return bytesAndStatus;
	}

    public void onEvent(DownloadCompleteEvent event){
        flagDownloadFinish(event.getRefer());
    }
    
}
