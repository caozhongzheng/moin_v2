package com.moinapp.wuliao.modules.update;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.google.gson.Gson;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moduleframework.AbsManager;
import com.moinapp.wuliao.commons.net.UpdateListener;
import com.moinapp.wuliao.commons.receiver.DownloadCompleteEvent;
import com.moinapp.wuliao.modules.update.network.UpdateProtocol;
import com.moinapp.wuliao.modules.update.network.UpdateService;
import com.moinapp.wuliao.utils.PackageUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UpdateManager extends AbsManager{

    // ===========================================================
    // Constants
    // ===========================================================
	private static final ILogger MyLog = LoggerFactory.getLogger(UpdateModule.MODULE_NAME);
    // ===========================================================
    // Fields
    // ===========================================================

    private static UpdateManager mInstance;
    private Context mContext;
    private Gson mGson = new Gson();
    // ===========================================================
    // Constructors
    // ===========================================================

    private UpdateManager() {
        mContext = ApplicationContext.getContext();
    }

    public synchronized static UpdateManager getInstance() {
        if (mInstance == null) {
            mInstance = new UpdateManager();
        }
        return mInstance;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    /**
     * 联网进行版本更新请请求的方法
     * @param listener
     */
    public void checkUpdate(UpdateListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(UpdateConstants.VERSION_CODE, String.valueOf(PackageUtils.getCurrentVersion(mContext)));
        map.put(UpdateConstants.CHANNEL, ClientInfo.getChannelId());
        new UpdateService().checkUpdate(UpdateConstants.GET_UPDATE_URL, map, listener);
    }

    /**
     * 下载新版本的方法
     * @param downloadUrl
     * @param version
     */
    public void downloadApp(String downloadUrl, String version) {
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        UpdatePreference helper = UpdatePreference.getInstance();

        String updateName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UpdateConstants.UPDATE_CACHE + "Moin_" + version + ".apk";
        File file = new File(updateName);

        //如果上次的更新没有完成，删除下载任务和文件
        long ref = helper.getDownloadRefer();
        MyLog.i("ref=" + ref);
        if (ref != 0) {
            //如果下载已经完成直接安装
            boolean result = helper.getDownloadFinish();
            if (result && file.exists()) {
                installApk(file);
                return;
            } else {
                downloadManager.remove(ref);
                String fileName = helper.getUpdateFileName();
                File f = new File(fileName);
                if (f != null && f.exists()) {
                    f.delete();
                }
            }
        }

        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
            request.setTitle(mContext.getString(R.string.update_download_title));
            request.setMimeType("application/vnd.android.package-archive");
            request.setDestinationUri(Uri.fromFile(new File(updateName)));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setVisibleInDownloadsUi(true);

            //创建下载目录，如果不存在
            String dir = updateName.substring(0, updateName.lastIndexOf("/"));
            File downloadDir = new File(dir);
            if(!downloadDir.exists()) {
                downloadDir.mkdirs();
            }
            if (file.exists()){
                file.delete();
            }

            //开始下载
            long reference = downloadManager.enqueue(request);
            MyLog.i("checkUpdate:reference=" + reference);

            helper.setDownloadUrl(downloadUrl);
            helper.setUpdateFileName(updateName);
            helper.setDownloadFinish(false);
            helper.setDownloadRefer(reference);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收到的服务器返回版本检查的response
     * @param event
     */
    public void onEvent(UpdateProtocol.UpdateSuccessEvent event){
        String response = event.getResponse();
        UpdateProtocol.UpdateResponse res = mGson.fromJson(response, UpdateProtocol.UpdateResponse.class);
        if (res != null && res.getResult() == 1) {
            ((UpdateListener)event.getTag()).getUpdateSucc(res.getApp());
        } else {
            ((UpdateListener)event.getTag()).onErr(null);
        }
    }

    /**
     * 下载完成后安装
     * @param event
     */
    public void onEvent(DownloadCompleteEvent event) {
        long reference = event.mRefer;
        UpdatePreference helper = UpdatePreference.getInstance();
        if (reference != -1) {
            long refer = helper.getDownloadRefer();
            if (reference == refer) {
                helper.setDownloadFinish(true);
                File file = new File(helper.getUpdateFileName());

                //if is the fore download ,pop the install
                if (file != null && file.exists()) {
                    installApk(file);
                    return;
                }
            }
        }
    }

    private void installApk(File file) {
        Uri uri = Uri.fromFile(file); //这里是APK路径
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(uri, "application/vnd.android.package-archive");
        ApplicationContext.getContext().startActivity(i);
    }

    @Override
    public void init() {
        EventBus.getDefault().register(this);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
