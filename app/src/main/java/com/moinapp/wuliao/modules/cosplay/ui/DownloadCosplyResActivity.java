package com.moinapp.wuliao.modules.cosplay.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.modal.BaseResource;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.commons.ui.XListView;
import com.moinapp.wuliao.modules.cosplay.CosplayManager;
import com.moinapp.wuliao.modules.cosplay.listener.GetCosplayResListener;
import com.moinapp.wuliao.modules.cosplay.model.CosplayResource;
import com.moinapp.wuliao.modules.cosplay.model.CosplayThemeInfo;
import com.moinapp.wuliao.modules.cosplay.model.CosplayThemes;
import com.moinapp.wuliao.modules.wowo.WowoContants;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
* Created by liujiancheng on 15/6/29.
*/
public class DownloadCosplyResActivity extends BaseActivity implements XListView.IXListViewListener {
    // ===========================================================
    // Constants
    // ===========================================================
    ILogger MyLog = LoggerFactory.getLogger(DownloadCosplyResActivity.class.getSimpleName());

    public static final String COSPLAY_RES_LIST = "cosplay_res_list";
    public static final String FROM = "from";
    // ===========================================================
    // Fields
    // ===========================================================

    /**
     * 大咖秀资源列表
     */
    private List<CosplayResource> mCosplayResList = new ArrayList<CosplayResource>();

    private XListView mList;
    private TextView mEmptyTv;
    private MyListAdapter mAdapter;
    private Handler mHandler = new Handler();
    protected Context mContext;
    private ImageLoader mImageLoader;
    private int mFrom;//从主入口来0；1,从ip详情来
    // ===========================================================
    // Constructors
    // ===========================================================
    public DownloadCosplyResActivity() {
        super();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.cosplay_res_list);

        //初始化图片缓存
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(BitmapUtil.getImageLoaderConfiguration());

        initTopBar("大咖秀库列表");
        initLeftBtn(true, R.drawable.back_white);

        mList = (XListView)findViewById(R.id.cosplay_list);
        mEmptyTv = (TextView)findViewById(R.id.empty_tv);
        mAdapter = new MyListAdapter(DownloadCosplyResActivity.this);
        mList.setAdapter(mAdapter);
        mList.setXListViewListener(this);
        mList.setPullLoadEnable(true);
        mList.setPullRefreshEnable(false);

        getFrom();
        if (mFrom == 1) {
            mCosplayResList = getCosplayResListFromIntent();
            if (mCosplayResList == null || mCosplayResList.size() == 0) {
                MyLog.i("mCosplayList == null or size == 0");
                finish();
            }
        } else {
            getCosplayResList();
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    private void updateListView() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getFrom() {
        Intent intent = getIntent();
        if (intent == null) {
            mFrom = 0;
            return;
        }

        mFrom = intent.getIntExtra(FROM, 0);
    }

    private List<CosplayResource> getCosplayResListFromIntent() {
        Intent intent = getIntent();
        if (intent == null)
            return null;

        return (List<CosplayResource>) intent.getSerializableExtra(COSPLAY_RES_LIST);
    }

    /**
     * 联网获取大咖秀资源列表
     */
    private void getCosplayResList() {
        CosplayManager.getInstance().getCosplayResource(null, getLastId(), new GetCosplayResListener() {
            @Override
            public void getCosplaySucc(List<CosplayResource> cosplayResourceList) {
                if (cosplayResourceList == null || cosplayResourceList.size() == 0) {
                    return;
                }
                MyLog.i("getCosplaySucc...cosplayResourceList.size=" + cosplayResourceList.size());
                mCosplayResList.addAll(cosplayResourceList);
                updateListView();
            }

            @Override
            public void onNoNetwork() {

            }

            @Override
            public void onErr(Object object) {

            }
        });
    }

    private String getLastId() {
        if (mCosplayResList.size() == 0) {
            return null;
        }
        return mCosplayResList.get(mCosplayResList.size() - 1).getId();
    }

    private void onLoad() {
        mList.stopRefresh();
        mList.stopLoadMore();
        mList.setRefreshTime(StringUtil.humanDate(System.currentTimeMillis(), WowoContants.COMMENT_TIMESTAMP_PATTERN));
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {
        getPost();
    }

    private void getPost() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getCosplayResList();
                onLoad();
            }
        }, 1000);
    }

    private class MyListAdapter extends BaseAdapter {
        private Activity mContext = null;
        private LayoutInflater mLayoutInflater;

        public MyListAdapter(Activity context) {
            mContext = context;
            if (mLayoutInflater == null) {
                mLayoutInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
        }

        @Override
        public int getCount() {
            return mCosplayResList != null ? mCosplayResList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mCosplayResList != null ? mCosplayResList.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.cosplay_res_list_item, null);
                viewHolder = getListViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ListViewHolder) convertView.getTag();
            }

            setContentView(viewHolder, position);

            return convertView;
        }

        private void setContentView(ListViewHolder viewHolder, int position) {
            if (mCosplayResList != null && mCosplayResList.size() > 0) {
                final CosplayResource cosplay = mCosplayResList.get(position);
                //设置每一个view
                viewHolder.mDownload.init(cosplay);
                DownloadItemView.InitCosplayListener mInitCosplayCallback = new DownloadItemView.InitCosplayListener() {
                    @Override
                    public void onInit() {
                        mContext.finish();
                    }
                };
                viewHolder.mDownload.setInitCallback(mInitCosplayCallback);

                if (cosplay.getPics() != null && cosplay.getPics().getCover() != null)
                    mImageLoader.displayImage(cosplay.getPics().getCover().getUri(), viewHolder.mCosplayCover, BitmapUtil.getImageLoaderOption());

                viewHolder.mCosplayName.setText(cosplay.getName());
                viewHolder.mCosplaySize.setText(StringUtil.formatSize(cosplay.getSize()));
                viewHolder.mCosplayResNum.setText(String.valueOf(cosplay.getThemeNum()));
            }
        }

        private ListViewHolder getListViewHolder(View convertView) {
            ListViewHolder holder = new ListViewHolder();
            holder.mCosplayCover = (ImageView) convertView.findViewById(R.id.cosplay_cover);
            holder.mCosplayName = (TextView) convertView.findViewById(R.id.cosplay_res_name);
            holder.mCosplaySize = (TextView) convertView.findViewById(R.id.cosplay_res_size);
            holder.mCosplayResNum = (TextView) convertView.findViewById(R.id.cosplay_element_number);
            holder.mDownload = (DownloadItemView) convertView.findViewById(R.id.download);
            return holder;
        }

        public class ListViewHolder {
            public ImageView mCosplayCover; //大咖秀资源封面
            public TextView mCosplayName; //大咖秀资源名字
            public TextView mCosplaySize; //大咖秀资源大小
            public TextView mCosplayResNum; //大咖秀资源个数
            public DownloadItemView mDownload; //下载按钮

        }
    }


}
