package com.moinapp.wuliao.modules.cosplay;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.keyboard.bean.EmoticonBean;
import com.keyboard.bean.EmoticonSetBean;
import com.keyboard.db.DBHelper;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.image.ImageLoader;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moduleframework.AbsManager;
import com.moinapp.wuliao.commons.mydownloadmanager.IDownloadObserver;
import com.moinapp.wuliao.commons.mydownloadmanager.MyDownloadManager;
import com.moinapp.wuliao.modules.cosplay.listener.CosplayInitEditListener;
import com.moinapp.wuliao.modules.cosplay.listener.GetCosplayResListener;
import com.moinapp.wuliao.modules.cosplay.model.CosplayResource;
import com.moinapp.wuliao.modules.cosplay.model.CosplayThemeInfo;
import com.moinapp.wuliao.modules.cosplay.network.GetCosplayResProtocal;
import com.moinapp.wuliao.modules.cosplay.network.GetCosplayResService;
import com.moinapp.wuliao.modules.cosplay.ui.CosplayConstants;
import com.moinapp.wuliao.modules.cosplay.ui.CosplayShareActivity;
import com.moinapp.wuliao.modules.cosplay.ui.DownloadCosplyResActivity;
import com.moinapp.wuliao.modules.ipresource.EmojiShowActivity;
import com.moinapp.wuliao.modules.ipresource.EmojiUtils;
import com.moinapp.wuliao.modules.ipresource.model.EmojiInfo;
import com.moinapp.wuliao.modules.ipresource.model.EmojiResource;
import com.moinapp.wuliao.modules.mine.MineManager;
import com.moinapp.wuliao.modules.mine.listener.CosplayListener;
import com.moinapp.wuliao.modules.mine.model.EmojiCosPlay;
import com.moinapp.wuliao.modules.wowo.IPostListener;
import com.moinapp.wuliao.modules.wowo.WowoManager;
import com.moinapp.wuliao.modules.wowo.model.CommentInfo;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;
import com.moinapp.wuliao.modules.wowo.model.WowoInfo;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.EmoticonsUtils;
import com.moinapp.wuliao.utils.FileUtil;
import com.moinapp.wuliao.utils.HttpUtil;
import com.moinapp.wuliao.utils.NetworkUtils;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Handler;

import org.w3c.dom.Text;

/**
* Created by liujiancheng on 15/6/26.
*/
public class CosplayManager extends AbsManager {
    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================
    private static ILogger MyLog = LoggerFactory.getLogger("Cosplay");
    private static CosplayManager mInstance;
    private Context mContext;
    private Gson mGson = new Gson();
    private CosplayInitEditListener mCosplayInitEditListner = null;

    private String mCurrentCosplayId;
    private Handler mHandler = new Handler();
    // ===========================================================
    // Constructors
    // ===========================================================
    private CosplayManager() {
        mContext = ApplicationContext.getContext();
        init();
    }

    public static synchronized CosplayManager getInstance() {
        if (mInstance == null) {
            mInstance = new CosplayManager();
        }

        return mInstance;
    }

    // ===========================================================
    // public methods or interface
    // ===========================================================
    @Override
    public void init() {
        EventBus.getDefault().register(this);
    }

    /**
     * 获取大咖秀资源，给大咖秀模块调用
     */
    public void getips() {
        AppTools.toIntent(mContext, DownloadCosplyResActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);



    }

    public void getips(String ipid) {
        CosplayManager.getInstance().getCosplayResource(ipid, null, new GetCosplayResListener() {
            @Override
            public void getCosplaySucc(List<CosplayResource> cosplayResourceList) {
                if (cosplayResourceList == null || cosplayResourceList.size() == 0) {
                    return;
                }
                MyLog.i("getCosplaySucc...cosplayResourceList.size=" + cosplayResourceList.size());
            }

            @Override
            public void onNoNetwork() {

            }

            @Override
            public void onErr(Object object) {

            }
        });
    }

    /**
     * 获取大咖秀资源，给大咖秀模块调用,没有动画，add by wufan
     */
    public void getIpsNoAnimation()
    {
        AppTools.toIntent(mContext, DownloadCosplyResActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
    }


    /**
     *大咖秀传递res监听，
     */
    public void setCosplayInitEditListener(CosplayInitEditListener editListener)
    {
        mCosplayInitEditListner = editListener;
    }

    /**
     * 启动大咖秀，传入选好的大咖秀资源
     */
    public void initCosplay(CosplayResource cosplay) {
        MyLog.i("initCosplay: cosplay=" + mGson.toJson(cosplay));

        if(mCosplayInitEditListner != null)
        {
            mCosplayInitEditListner.initCosplay(cosplay);
        }
    }

    /**
     * 导出大咖秀资源，弹出ui，可以分享到微信 qq和保存大咖秀表情
     */
    public void exportCosplay(int width, int height, long size, String ipid, String cosplay) {
        // 判断大咖秀gif文件存在不存在
        if (TextUtils.isEmpty(cosplay))
            return;

        File file = new File(cosplay);
        if (file == null || !file.exists()) {
            MyLog.i("cosplay save file does not exist!!");
            return;
        }

        if (ClientInfo.getUID() != null) {
            MyLog.i("cosplay save file exist!!");
            uploadGif(file, ipid);
        } else {
            toastSaveCosplayResult(false);
        }

        Intent i = new Intent(mContext, EmojiShowActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
        );
        Bundle b = new Bundle();
        b.putSerializable(EmojiShowActivity.EMOJI_PATH, file.getAbsolutePath());
        i.putExtras(b);
        mContext.startActivity(i);
    }

    /**
     * 导出大咖秀资源，弹出ui，可以分享到微信 qq和保存大咖秀表情
     */
    public void exportCosplay(int width, int height, long size, String ipid, InputStream cosplay) {
        //第一步把大咖秀的流存成文件
        if (cosplay == null)
            return;

        File file = saveGif(cosplay);

        //TEST
//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/2.gif");
        // 第二部上传文件到服务器
        if (file != null && file.exists() && ClientInfo.getUID() != null) {
            MyLog.i("cosplay save file exist!!");

            uploadGif(file, ipid);

            // 弹出分享界面
//            Bundle b = new Bundle();
//            b.putString("cosplay_path", file.getAbsolutePath());
//            AppTools.toIntent(mContext, b, CosplayShareActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent i = new Intent(mContext, EmojiShowActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP
            );
            Bundle b = new Bundle();
            b.putSerializable(EmojiShowActivity.EMOJI_PATH, file.getAbsolutePath());
            i.putExtras(b);
            mContext.startActivity(i);
        } else {
            toastSaveCosplayResult(false);
        }
    }

    /**
     * 联网获取大咖秀资源，外部ui调用
     */
    public void getCosplayResource(String ipid, String lastid, GetCosplayResListener listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(CosplayConstants.IP_ID, ipid);
        map.put(CosplayConstants.LAST_ID, lastid);
        new GetCosplayResService().GetCosplayService(CosplayConstants.GET_COSPLAY_RES_URL, map, listener);
    }

    /**
     * 注册下载进度监听
     * @param downloadId
     * @param observer
     */
    public void registerDownloadObserver(Long downloadId, IDownloadObserver observer) {
        if (downloadId == null || observer == null) {
            return;
        }

        MyDownloadManager downloader = MyDownloadManager
                .getInstance(mContext);
        downloader.registerDownloadObserver(downloadId, observer);
    }

    /**
     * 取消下载进度监听
     * @param downloadId
     */
    public void unregisterDownloadObserver(Long downloadId) {
        if (downloadId == null) {
            return;
        }

        MyDownloadManager downloader = MyDownloadManager
                .getInstance(mContext);
        downloader.unregisterDownloadObserver(downloadId);
    }

    public static class Status {
        public int statusCode;
        public long downloadedBytes;
        public long totalBytes;
    }

    /**
     * 返回大咖秀资源的下载状态
     * 目前的想法是这样，轮询大咖秀资源的每一个部位内容，把所有的元素放到一个list里面，分别查每一个元素的
     * 下载状态
     * @param cosplay: 大咖秀资源
     * @return 总是返回非null
     */
    public Status getStatus(CosplayResource cosplay) {
        // 设置icon的本地路径
        if (TextUtils.isEmpty(cosplay.getIconLocalPath())) {
            cosplay.setIconLocalPath(CosplayConstants.COSPLAY_DOWNLOAD_FOLDER + cosplay.getId() +
                    cosplay.getVersion() + getFileName(cosplay.getIcon().getUri()));
        }

        List<CosplayThemeInfo> list = cosplayRes2ThemeList(cosplay);
        return getStatus(cosplay.getId(), cosplay.getVersion(), list);
    }

    /**
     * 下载大咖秀资源：需要把资源中的每一个元素拆分出来分别下载
     * @param cosplay: 大咖秀资源
     * @return 总是返回非null
     */
    public List<Long> downloadCosplayRes(CosplayResource cosplay) {
        // 下载icon
        cosplay.setIconLocalPath(CosplayConstants.COSPLAY_DOWNLOAD_FOLDER + cosplay.getId() +
                cosplay.getVersion() + getFileName(cosplay.getIcon().getUri()));
        downloadCosplayIcon(cosplay);

        List<CosplayThemeInfo> list = cosplayRes2ThemeList(cosplay);
        // 下载各个资源元素
        if (list != null && list.size() > 0) {
            MyLog.i("downloadCosplayRes...list.size="+list.size());
            List<Long> downloadIds = new ArrayList<Long>();
            for (CosplayThemeInfo themeInfo: list) {
                Long id = downloadCosplay(cosplay.getId(), cosplay.getVersion(), themeInfo);
                if (id != null) {
                    downloadIds.add(id);
                }
            }
            return downloadIds;
        }
        return null;
    }

    /**
     * 把处于下载状态中的大咖秀资源中的每一个元素对应的download id找出来
     * @param cosplay: 大咖秀资源
     * @return 总是返回非null
     */
    public List<Long> getDownloadIds(CosplayResource cosplay) {
        List<CosplayThemeInfo> list = cosplayRes2ThemeList(cosplay);
        if (list != null && list.size() > 0) {
            MyLog.i("getDownloadIds...list.size="+list.size());
            List<Long> downloadIds = new ArrayList<Long>();
            for (CosplayThemeInfo themeInfo: list) {
                Long id = MyDownloadManager.getInstance(mContext).getDownloadIdByRes(cosplay.getId(), cosplay.getVersion(), themeInfo.getResource().getUri());
                if (id != null) {
                    downloadIds.add(id);
                }
            }
            return downloadIds;
        }
        return null;
    }

    /**
     * 接收到服务器返回的大咖秀资源列表后进行json解析
     * @param event
     */
    public void onEvent(GetCosplayResProtocal.GetCosplayResSuccessEvent event) {
        String response = event.getResponse();
        GetCosplayResProtocal.GetCosplayResResponse res = mGson.fromJson(response, GetCosplayResProtocal.GetCosplayResResponse.class);
        if (res != null) {
            ((GetCosplayResListener)event.getTag()).getCosplaySucc(res.getCosplaylist());
        } else {
            ((GetCosplayResListener)event.getTag()).onErr(null);
        }
    }

    // ===========================================================
    // private methods
    // ===========================================================
    /**
     * 把一个大咖秀资源转成元素列表
     * @return
     */
    public List<CosplayThemeInfo> cosplayRes2ThemeList(CosplayResource cosplay) {
        List<CosplayThemeInfo> totalList = new ArrayList<CosplayThemeInfo>();

        //头信息
        List<CosplayThemeInfo> head = cosplay.getThemes().getHead();
        if (head != null && head.size() > 0) {
            totalList.addAll(head);
        }
        //身体
        List<CosplayThemeInfo> body = cosplay.getThemes().getBody();
        if (body != null && body.size() > 0) {
            totalList.addAll(body);
        }

        //手
        List<CosplayThemeInfo> hand = cosplay.getThemes().getHand();
        if (hand != null && hand.size() > 0) {
            totalList.addAll(hand);
        }

        //手臂
        List<CosplayThemeInfo> arm = cosplay.getThemes().getArm();
        if (arm != null && arm.size() > 0) {
            totalList.addAll(arm);
        }

        //脚
        List<CosplayThemeInfo> foot = cosplay.getThemes().getFoot();
        if (foot != null && foot.size() > 0) {
            totalList.addAll(foot);
        }

        //腿
        List<CosplayThemeInfo> leg = cosplay.getThemes().getLeg();
        if (leg != null && leg.size() > 0) {
            totalList.addAll(leg);
        }

        //气氛效果
        List<CosplayThemeInfo> feature = cosplay.getThemes().getFeature();
        if (feature != null && feature.size() > 0) {
            totalList.addAll(feature);
        }

        //道具
        List<CosplayThemeInfo> tool = cosplay.getThemes().getTool();
        if (tool != null && tool.size() > 0) {
            totalList.addAll(tool);
        }

        //other
        List<CosplayThemeInfo> other = cosplay.getThemes().getOther();
        if (other != null && other.size() > 0) {
            totalList.addAll(other);
        }

        //other1
        List<CosplayThemeInfo> other1 = cosplay.getThemes().getOther1();
        if (other1 != null && other1.size() > 0) {
            totalList.addAll(other1);
        }

        //other2
        List<CosplayThemeInfo> other2 = cosplay.getThemes().getOther2();
        if (other2 != null && other2.size() > 0) {
            totalList.addAll(other2);
        }

        //other3
        List<CosplayThemeInfo> other3 = cosplay.getThemes().getOther3();
        if (other3 != null && other3.size() > 0) {
            totalList.addAll(other3);
        }

        //给本地路径赋值
        if (totalList != null && totalList.size() > 0) {
            for (CosplayThemeInfo theme : totalList) {
                if (TextUtils.isEmpty(theme.getLocalPath())) {
                    setLocalPath(cosplay.getId(), cosplay.getVersion(), theme);
                }
            }
        }
        return totalList;
    }

    private void setLocalPath(String resId, int version, CosplayThemeInfo theme) {
        String local = CosplayConstants.COSPLAY_DOWNLOAD_FOLDER + resId + version + getFileName(theme.getResource().getUri());
        MyLog.i("setLocalPath=" + local);
        theme.setLocalPath(local);
    }

    private String getFileName(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     *
     * @param resId
     * @param version
     * @param theme: 头，身等内容对象里面的元素列表
     * @return 如果列表里
     */
    private Status getStatus(String resId, int version, List<CosplayThemeInfo> theme) {
        Status result = new Status();
        result.statusCode = MyDownloadManager.STATUS_NONE;
        int none_count = 0;
        int succ_count = 0;

        if (theme != null && theme.size() > 0) {
            for (CosplayThemeInfo head_item: theme) {
                Status s = getStatus(resId, version, head_item);
                if (s.statusCode == MyDownloadManager.STATUS_RUNNING) {
                    result.statusCode = MyDownloadManager.STATUS_RUNNING;
                    return result;
                } else if (s.statusCode == MyDownloadManager.STATUS_SUCCESSFUL) {
                    succ_count++;
                } else if (s.statusCode == MyDownloadManager.STATUS_NONE) {
                    none_count++;
                }

                // 如果同时有状态为未下载的和下载完成的，则返回整个内容对象的状态未下载中
                if (succ_count > 0 && none_count > 0) {
                    result.statusCode = MyDownloadManager.STATUS_RUNNING;
                    return result;
                }
            }
            if (succ_count == theme.size()) {
                result.statusCode = MyDownloadManager.STATUS_SUCCESSFUL;
            } else if (none_count == theme.size()) {
                result.statusCode = MyDownloadManager.STATUS_NONE;
            } else {
                result.statusCode = MyDownloadManager.STATUS_RUNNING;
            }
        }
        return result;
    }

    /**
     * 返回大咖秀资源的一个元素下载状态
     * @param resId: 大咖秀资源的id
     * @param version: 大咖秀资源的版本
     * @param themeInfo: 大咖秀资源中的一个元素，比如头 身元素列表中的一个元素
     * @return 总是返回非null
     */
    private Status getStatus(String resId, int version, CosplayThemeInfo themeInfo) {
        Status result = new Status();
        result.statusCode = MyDownloadManager.STATUS_NONE;

        if (themeInfo == null) {
            return null;
        }

        MyDownloadManager downloader = MyDownloadManager
                .getInstance(mContext);
        Long downloadId = downloader.getDownloadIdByRes(resId, version, themeInfo.getResource().getUri());
        MyLog.i("getStatus downloadId:" + downloadId);
        if (downloadId != null) {
            int[] bytesAndStatus = downloader.getBytesAndStatus(downloadId);
            MyLog.i("getStatus staus:"
                    + bytesAndStatus[0] + "," + bytesAndStatus[1] + ","
                    + bytesAndStatus[2] + "," + bytesAndStatus[3] + "]");
            if (bytesAndStatus[0] == 1) {
                result.statusCode = bytesAndStatus[1];
                if (result.statusCode == MyDownloadManager.STATUS_SUCCESSFUL) {
                    String filePath = themeInfo.getLocalPath();
                    if (!TextUtils.isEmpty(filePath)) {
                        File file = new File(filePath);
                        if (!file.exists()) {
                            result.statusCode = MyDownloadManager.STATUS_NONE;
                        }
                    }
                }
            }
        }
        return result;
    }


    /**
     * 下载大咖秀资源的每一条元素
     */
    private Long downloadCosplay(String resId, int version, CosplayThemeInfo themeInfo) {
        return downloadCosplay(resId, version, themeInfo, DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);
    }

    /**
     * 支持指定网络类型的下载cosplayResource方法
     * @param resId: 大咖秀资源的id
     * @param version: 大咖秀资源的版本
     * @param themeInfo: 大咖秀资源中的一个元素
     * @param networkflag
     * @return
     */
    private Long downloadCosplay(String resId, int version, CosplayThemeInfo themeInfo, int networkflag) {
        MyLog.i("downloadCosplay resId=" + resId);
        Long result = 0l;

        if (themeInfo == null || TextUtils.isEmpty(themeInfo.getResource().getUri())) {
            return result;
        }

        if (!NetworkUtils.isConnected(mContext)) {
            ToastUtils.toast(mContext, "没有网络");
            return result;
        }

        try {
            Long downloadId = MyDownloadManager.getInstance(mContext)
                    .downloadCosplayRes(resId, version, themeInfo, networkflag);
            if (downloadId != null) {
                result = downloadId;
            }
        } catch (Exception e) {
            MyLog.e(e);
        }

        return result;
    }

    /**
     * 保存gif 图片
     * @param cosplay
     * @return
     */
    private File saveGif(byte[] cosplay) {
        FileOutputStream fileout = null;
        String fileName = CosplayConstants.COSPLAY_EMOJI_FOLDER + "cosplay.gif";
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            fileout = new FileOutputStream(file);
            fileout.write(cosplay, 0, cosplay.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    /**
     * 保存gif 图片
     * @param inputStream
     * @return
     */
    private File saveGif(InputStream inputStream) {
        FileOutputStream fileout = null;
        String dir = CosplayConstants.COSPLAY_EMOJI_FOLDER;
        File CosplayDir = new File(dir);
        if(!CosplayDir.exists()){
            CosplayDir.mkdirs();
        }
        String fileName = CosplayConstants.COSPLAY_EMOJI_FOLDER + "cosplay.gif";
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            fileout = new FileOutputStream(file);
            byte[] b = new byte[1024];
            while((inputStream.read(b)) != -1){
                fileout.write(b);
            }
            inputStream.close();
            fileout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }
    /**
     * 上传大咖秀的gif图片
     * @param gifFile
     */
    private void uploadGif(final File gifFile, final String ipid) {
        WowoManager.getInstance().uploadImage(gifFile.getAbsolutePath(), new IPostListener() {
            @Override
            public void onGetPostListSucc(List<PostInfo> postInfoList, int column) {

            }

            @Override
            public void onGetWoPostListSucc(List<PostInfo> postInfoList, WowoInfo woinfo, int column) {

            }

            @Override
            public void onGetIPPostSucc(List<PostInfo> postInfoList) {

            }

            @Override
            public void onNewPostSucc(int woid, String postid) {

            }

            @Override
            public void onReplyPostSucc(int commentid) {

            }

            @Override
            public void onGetPostSucc(PostInfo postInfo, List<CommentInfo> commentInfos) {

            }

            @Override
            public void onUploadImageSucc(String picid) {
                MyLog.i("upload gif succeed, pic id =" + picid);
                createCosplay(gifFile, picid, ipid);
            }

            @Override
            public void onNoNetwork() {

            }

            @Override
            public void onErr(Object object) {
                toastSaveCosplayResult(false);
            }
        });
    }

    /**
     * 创建我的大咖秀资源
     */
    private void createCosplay(final File gifFile, final String picid, final String ipid) {
        MineManager.getInstance().createMyCosplay(null, picid, ipid, new CosplayListener() {
            @Override
            public void createCosplaySucc(int result, String id) {
                MyLog.i("createCosplaySucc: result = " + result);
                if (result == 1) {
                    mCurrentCosplayId = id;

                    //copy gif文件, 用服务器返回的大咖秀id作为文件名
                    String source = gifFile.getAbsolutePath();
                    String dest = CosplayConstants.COSPLAY_EMOJI_FOLDER + id + ".gif";
                    boolean ret = FileUtil.copyFile(source, dest);
                    if (ret) {
                        toastSaveCosplayResult(true);

                        //入本地数据库
                        EmojiCosPlay cosPlay = new EmojiCosPlay();
                        cosPlay.setId(picid);
                        cosPlay.setUid(ClientInfo.getUID());
                        addCosplayIntoDB(cosPlay);
                    } else {
                        toastSaveCosplayResult(false);
                    }
                } else {
                    toastSaveCosplayResult(false);
                }
            }

            @Override
            public void getCosplaySucc(List<EmojiCosPlay> cosPlayList) {

            }

            @Override
            public void delCosplaySucc(int result) {

            }

            @Override
            public void onNoNetwork() {

            }

            @Override
            public void onErr(Object object) {
                toastSaveCosplayResult(false);
            }
        });
    }

    private void toastSaveCosplayResult(boolean result) {
        final String s = result ? mContext.getString(R.string.cosplay_save_ok) : mContext.getString(R.string.cosplay_save_ko);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.toast(mContext, s);
            }
        });
    }

    private void downloadCosplayIcon(CosplayResource cosplay) {
        final String url = cosplay.getIcon().getUri();
        final String local = cosplay.getIconLocalPath();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUtil.download(url, local);
                } catch (NullPointerException e) {
                    MyLog.e(e);
                }
            }
        }).start();
    }

    public void saveCosplayBackGround()
    {
        Bitmap tBgBitmap = CosplayRuntimeData.getInstance().backGroundBitmap;
        if(tBgBitmap != null) {

            String fileName = BitmapUtil.getCosplayImagePath();
            File  f = new File(fileName);
            if (f.exists()) {
                f.delete();
            }

            try {
                FileOutputStream out = new FileOutputStream(f);
                tBgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                MyLog.i("已经保存");
            } catch (FileNotFoundException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    //从缓存里读取Cosplay,暂时不需要实现//
    public  void readCosplay()
    {
        if(CosplayRuntimeData.getInstance().backGroundBitmap != null)
        {
            return;
        }


        String fileName = BitmapUtil.getCosplayImagePath();
        File  f = new File(fileName);
        if (f.exists()) {

            CosplayRuntimeData.getInstance().backGroundBitmap =  BitmapFactory.decodeFile(fileName);

        }
    }

    public void startCosplay()
    {
        clear();
        AppTools.toIntent(ApplicationContext.getContext(), CosplayMainActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public void startCosplay(String gson)
    {
        clear();
        CosplayRuntimeData.getInstance().runtimeJson = gson;
        AppTools.toIntent(ApplicationContext.getContext(), CosplayMainActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
    }


    public void clear()
    {
        CosplayRuntimeData.getInstance().editViewGroup = null;
        CosplayRuntimeData.getInstance().runtimeJson = null;


    }

    public void addCosplayIntoDB(List<EmojiCosPlay> cosPlayList) {
        for(EmojiCosPlay cosPlay: cosPlayList) {
            addCosplayIntoDB(cosPlay);
        }
    }
    public void addCosplayIntoDB(final EmojiCosPlay cosPlay) {
        // TODO
        MyLog.i("addCosplayIntoDB");
        if(cosPlay == null || cosPlay.getId() == null) {
            MyLog.i("addCosplayIntoDB NG");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHelper dbHelper = new DBHelper(mContext);

                EmoticonBean emoticonBean = dbHelper.queryEmoticonBeanByID(cosPlay.getId());
                if(emoticonBean != null) {
                    MyLog.i("cosplay:" + cosPlay.getId() + " exists before insert: ");
                    return;
                }

                EmoticonBean tmp = new EmoticonBean();
                if (TextUtils.isEmpty(cosPlay.getUid())) {
                    tmp.setParentId(ClientInfo.getUID());
                } else {
                    tmp.setParentId(cosPlay.getUid());
                }
                tmp.setEventType(EmoticonBean.FACE_TYPE_NOMAL);
                tmp.setId(cosPlay.getId());
                tmp.setTags("");
                //缩略图
                tmp.setIconUri("file://" + EmojiUtils.getCosplayThumbPath(cosPlay.getId()));
                try {
                    tmp.setIconUrl(cosPlay.getIcon().getUri());
                } catch (Exception e) {
                    MyLog.e(e);
                }
                //gif
                tmp.setGifUri("file://" + EmojiUtils.getCosplayPath(cosPlay.getId()));
                try {
                    tmp.setGifUrl(cosPlay.getPicture().getUri());
                } catch (Exception e) {
                    MyLog.e(e);
                }
                tmp.setContent("");
                tmp.setUseStat(0);
                ContentValues[] contentValues = new ContentValues[1];
                contentValues[0] = dbHelper.buildEmoticonValues(tmp, "");
                long cnt = dbHelper.insertEmoticonBeans(contentValues);
                dbHelper.cleanup();
                if(cnt > 0) {
                    MyLog.i("cosplay:" + cosPlay.getId() + " is inserted into DB OK");
                } else {
                    MyLog.i("cosplay:" + cosPlay.getId() + " is inserted into DB NG");
                }
            }
        }).start();

    }
}
