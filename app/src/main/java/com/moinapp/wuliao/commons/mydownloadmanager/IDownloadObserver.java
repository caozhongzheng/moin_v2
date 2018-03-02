/**
 * 
 */
package com.moinapp.wuliao.commons.mydownloadmanager;

/**
 * 下载进度状态监听及查询
 */
public interface IDownloadObserver {
	/**
	 * 已取消
	 */
	int STATUS_CANCEL= -1;
	/**
	 * 下载进度或状态有变化时触发
	 * 
	 * @param downloadId - 下载表的id
	 * @param queryDownloanProgressSuccess - 能否查到进度信息
	 * @param status - MyDownloadManager的Status
	 * @param downloadedBytes - 已经下载的字节数
	 * @param totalBytes - 总字节数
	 * <br/>status-下载状态 ,one of below:<br/>
	 * {@link #STATUS_CANCEL}
	 * {@link android.app.DownloadManager#STATUS_PENDING}
	 * {@link android.app.DownloadManager#STATUS_RUNNING}
	 * {@link android.app.DownloadManager#STATUS_PAUSED}
	 * {@link android.app.DownloadManager#STATUS_SUCCESSFUL}
	 * {@link android.app.DownloadManager#STATUS_FAILED}
	 */
//	能否查到进度信息（1能,0不能），MyDownloadManager的Status，已下载字节数，总字节数
	public void onChange(long downloadId, boolean queryDownloanProgressSuccess, int status, long downloadedBytes, long totalBytes);
}
