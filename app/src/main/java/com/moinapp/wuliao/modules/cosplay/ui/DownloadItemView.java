package com.moinapp.wuliao.modules.cosplay.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.mydownloadmanager.MyDownloadManager;
import com.moinapp.wuliao.commons.receiver.DownloadCompleteEvent;
import com.moinapp.wuliao.modules.cosplay.CosplayManager;
import com.moinapp.wuliao.modules.cosplay.model.CosplayResource;
import com.moinapp.wuliao.utils.NetworkUtils;
import com.moinapp.wuliao.utils.ToastUtils;
import com.moinapp.wuliao.utils.Tools;

import java.util.List;

/**
 * Created by liujiancheng on 15/6/29.
 */
public class DownloadItemView extends RelativeLayout implements OnClickListener {
    private static ILogger MyLog = LoggerFactory.getLogger("DownloadItemView");

    private CosplayResource mCosplay;
    private CosplayManager.Status mStatus;
    private List<Long> mDownloadIds;
    private InitCosplayListener mInitCallback;
    private int pos;
    private Handler mHandler = new Handler();
    private ImageView downloadImage;
    private TextView downloadText;

    public DownloadItemView(Context context) {
        super(context);
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public DownloadItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DownloadItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(CosplayResource cosplay) {
        if (cosplay == null) {
            return;
        }

        EventBus.getDefault().register(this);
        downloadImage = (ImageView)this.findViewById(R.id.image_download);
        downloadText = (TextView)this.findViewById(R.id.text_download);
        mCosplay = cosplay;
        updateViews();
        setOnClickListener(this);

    }

    private void updateViews() {
        new UpdateViewTask().execute(null, null, null);
    }

    private class UpdateViewTask extends AsyncTask<Void, Void, Void> {

        Context context = getContext();

        @Override
        protected Void doInBackground(Void... params) {
            mStatus = CosplayManager.getInstance().getStatus(mCosplay);
            MyLog.i(mCosplay.getName() + " doInBackground: statusCode="
                    + mStatus.statusCode);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            MyLog.i(mCosplay.getName() + " onPostExecute: statusCode="
                    + mStatus.statusCode);
            updateViewByStatus(mStatus.statusCode);
        }

    }

    private void updateViewByStatus(int appStatus) {
        Context context = getContext();
        switch (appStatus) {
            case MyDownloadManager.STATUS_NONE:
                MyLog.i("updateViewByStatus-->status=STATUS_NONE: mDownloadIds,mStatus=" + appStatus);
                downloadImage.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_download));
                downloadText.setText(getResources().getText(R.string.emoji_download));
                break;
            case MyDownloadManager.STATUS_RUNNING:
                if (mDownloadIds == null || mDownloadIds.size() == 0) {
                    mDownloadIds = CosplayManager.getInstance().getDownloadIds(mCosplay);
                    for (Long id: mDownloadIds) {
                        MyLog.i("updateViewByStatus-->status=STATUS_DOWNLOADING: mDownloadIds="+id);
                    }
                }
                MyLog.i("updateViewByStatus-->status=STATUS_RUNNING: mDownloadIds,mStatus="+appStatus);
                downloadText.setText(getResources().getText(R.string.emoji_downloading));
                downloadImage.setImageDrawable(null);
                downloadImage.setBackgroundResource(R.drawable.cosplay_download);
                AnimationDrawable anim = (AnimationDrawable)downloadImage.getBackground();
                anim.start();
                break;
            case MyDownloadManager.STATUS_SUCCESSFUL:
                if (mDownloadIds == null || mDownloadIds.size() == 0) {
                    mDownloadIds = CosplayManager.getInstance().getDownloadIds(mCosplay);
                    for (Long id: mDownloadIds) {
                        MyLog.i("updateViewByStatus-->status=STATUS_SUCCESSFUL: mDownloadIds="+id);
                    }
                }
                   MyLog.i("updateViewByStatus-->status=STATUS_SUCCESSFUL: mDownloadIds,mStatus=" + appStatus);
                downloadText.setText(getResources().getText(R.string.emoji_downloaded));
                downloadImage.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_select));
                break;
            default:
                downloadText.setText(getResources().getText(R.string.emoji_download));
                downloadImage.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_download));
                break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        if (Tools.isFastDoubleClick())
            return;
        int id = v.getId();
        Context context = getContext();
        if (id == this.getId()) {
            setEnabled(true);
            switch (mStatus.statusCode) {
                case MyDownloadManager.STATUS_NONE:
                    if (!NetworkUtils.isConnected(context)) {
                        ToastUtils.toast(context, "没有网络！");
                        return;
                    }

                    CosplayManager cosplayManager = CosplayManager.getInstance();
                    mDownloadIds = cosplayManager.downloadCosplayRes(mCosplay);
                    for (Long downloadid: mDownloadIds) {
                        MyLog.i("onClick-->begin to download: mDownloadIds="+downloadid);
                    }
                    break;
                case MyDownloadManager.STATUS_RUNNING:
                    setEnabled(false);
                    break;
                case MyDownloadManager.STATUS_SUCCESSFUL:
                    // 点击启动大咖秀, 回调所属activity去finish
                    CosplayManager.getInstance().initCosplay(mCosplay);
                    mInitCallback.onInit();
                    break;
                default:
                    break;
            }
            updateViews();
        }
    }

    public void onEvent(DownloadCompleteEvent event) {
        if (mDownloadIds == null || mDownloadIds.size() == 0) {
            return;
        }
        //如果下载成功的id属于这个大咖秀资源中的元素对应的download id list
        if (mDownloadIds.contains(event.getRefer())) {
//            for (Long id: mDownloadIds) {
//                MyLog.i("onEvent-->DownloadCompleteEvent mDownloadIds="+id);
//            }
            MyLog.i("onEvent-->DownloadCompleteEvent mDownloadIds="+event.getRefer());
//            mStatus = CosplayManager.getInstance().getStatus(mCosplay);
            mDownloadIds.remove(event.getRefer());
            if (mDownloadIds.size() == 0) {
                mStatus.statusCode = MyDownloadManager.STATUS_SUCCESSFUL;
            }
            MyLog.i("onEvent-->DownloadCompleteEvent mStatus=" + mStatus.statusCode);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateViewByStatus(mStatus.statusCode);
                }
            });
        }
    }

    public interface InitCosplayListener {
        void onInit();
    }

    public void setInitCallback(InitCosplayListener callback) {
        mInitCallback = callback;
    }
}

