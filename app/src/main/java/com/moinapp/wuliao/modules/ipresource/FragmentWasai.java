package com.moinapp.wuliao.modules.ipresource;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.AppConstants;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moinsocialcenter.MoinSocialFactory;
import com.moinapp.wuliao.commons.moinsocialcenter.UmengSocialCenter;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.preference.CommonsPreference;
import com.moinapp.wuliao.commons.ui.MyGridView;
import com.moinapp.wuliao.commons.ui.XListView;
import com.moinapp.wuliao.modules.ipresource.model.EmojiInfo;
import com.moinapp.wuliao.modules.ipresource.model.EmojiResource;
import com.moinapp.wuliao.modules.login.LoginActivity;
import com.moinapp.wuliao.modules.login.LoginConstants;
import com.moinapp.wuliao.modules.login.LoginSuccJump;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.modules.wowo.WowoContants;
import com.moinapp.wuliao.modules.wowo.WowoPreference;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.DisplayUtil;
import com.moinapp.wuliao.utils.NetworkUtils;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moying on 15/5/13.
 */
public class FragmentWasai extends Fragment implements XListView.IXListViewListener {

    private static final ILogger MyLog = LoggerFactory.getLogger(IPResourceModule.MODULE_NAME);

    // constants
    public static final int REFRESH_MESSAGE = 1;
    // private field
    private Context mContext;
    private XListView mList;//表情专题的列表

    private EmojiListAdapter mEmojiListAdapter;
    private List<EmojiResource> mEmojiList = new ArrayList<EmojiResource>();//表情专题列表

    private MyHandler mHandler;
    private HandlerThread mHandlerThread;
    private ImageLoader mImageLoader;

    /**
     * loading需要的控件
     */
    RelativeLayout rl_enter;
    LinearLayout lv_loading;
    LinearLayout lv_reload;

    Activity mActivity = getActivity();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity().getApplicationContext();
        if(ApplicationContext.getContext() == null) {
            ApplicationContext.setContext(mContext);
        }
        //初始化图片缓存
        mImageLoader = ImageLoader.getInstance();
        if(!mImageLoader.isInited()) {
            mImageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wasai_layout, container, false);

        mEmojiListAdapter = new EmojiListAdapter();
        mHandlerThread = new HandlerThread("LocateHandlerThread");
        mHandlerThread.start();
        mHandler = new MyHandler(mHandlerThread.getLooper());

        findviews(rootView);
        initLoadingView(rootView);
        setLoadingMode(AppConstants.MODE_LOADING);

        if (mActivity == null) {
            mActivity = getActivity();
        }
        getEmojiList(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initLoadingView(View root) {
        rl_enter = (RelativeLayout) root.findViewById(R.id.__enter);
        lv_loading = (LinearLayout) root.findViewById(R.id.__loading);
        lv_reload = (LinearLayout) root.findViewById(R.id.__reload);
        lv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEmojiList(false);
            }
        });
    }

    private void setLoadingMode(int mode) {
        rl_enter.setVisibility(mode == AppConstants.MODE_OK ? View.GONE : View.VISIBLE);
        lv_loading.setVisibility(mode == AppConstants.MODE_LOADING ? View.VISIBLE : View.GONE);
        lv_reload.setVisibility(mode == AppConstants.MODE_RELOADING ? View.VISIBLE : View.GONE);
    }

    private void findviews(View root) {
        //head
        TextView headText = (TextView)root.findViewById(R.id.tv_title);
        headText.setVisibility(View.VISIBLE);
        headText.setText(getString(R.string.myemoji));

        ImageView search = (ImageView) root.findViewById(R.id.tv_right_img);
        search.setVisibility(View.VISIBLE);
        search.setImageResource(R.drawable.main_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppTools.toIntent(mContext, SearchActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        });

        //list
        mList = (XListView) root.findViewById(R.id.emoji_list);
        mList.setAdapter(mEmojiListAdapter);
        mList.setXListViewListener(this);
        mList.setPullLoadEnable(false);
        mList.setPullRefreshEnable(true);
//        UiUtils.fixListViewHeight(mList);
        //test
//        int vh = CommonsPreference.getInstance().getVirtualKeyboardHeight();
//        if (vh < 0)
//            vh = 0;
//        MyLog.i("CommonsPreference.getInstance().getVirtualKeyboardHeight()="+ vh);
//        if (vh > 0) {
//            ViewGroup.LayoutParams params = mList.getLayoutParams();
//            params.height = getActivity().getWindowManager().getDefaultDisplay().getHeight() - DisplayUtil.dip2px(ApplicationContext.getContext(), 60) - vh;
//            mList.setLayoutParams(params);
//        }
    }

    private void showReloading() {
        if (mEmojiList.size() == 0) {
            setLoadingMode(AppConstants.MODE_RELOADING);
        } else {
            setLoadingMode(AppConstants.MODE_OK);
        }
    }

    private void setListViewPosition() {
        try {
            int vh = CommonsPreference.getInstance().getVirtualKeyboardHeight();
            if (vh < 0) {
                vh = 0;
            }
            MyLog.i("CommonsPreference.getInstance().getVirtualKeyboardHeight()=" + vh);
            if (vh > 0) {
                ViewGroup.LayoutParams params =  mList.getLayoutParams();
                params.height = DisplayUtil.getDisplayHeight(mContext) - DisplayUtil.dip2px(ApplicationContext.getContext(), 60);
    //            MyLog.i("device height ="+DisplayUtil.getDisplayHeight(mContext) );
    //            MyLog.i("before ="+getActivity().getWindowManager().getDefaultDisplay().getHeight());
    //            MyLog.i("params.height ="+params.height );

                mList.setLayoutParams(params);
                mList.setPadding(0, 0, 0, DisplayUtil.dip2px(ApplicationContext.getContext(), 72));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getEmojiList(final boolean isFirst) {
        // 先从缓存里获取数据
        if (isFirst) {
            final List<EmojiResource> list = IPResourceManager.getInstance().getEmojiListFromCache();
            if (list != null && list.size() > 0) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mEmojiList.clear();
                        mEmojiList.addAll(list);
                        mEmojiListAdapter.notifyDataSetChanged();
                        setListViewPosition();
                    }
                });
            }
        }

        IPResourceManager.getInstance().getEmojiList(null, null, new IPEmojiListListener() {

            @Override
            public void onGetEmojiListSucc(List<EmojiResource> emojiList, final List<EmojiResource> suggest) {
                if (emojiList == null || emojiList.size() == 0) {
                    setLoadingMode(AppConstants.MODE_OK);
                    return;
                }
                MyLog.i("onGetEmojiListSucc........ emojiList.size=" + emojiList.size());

                //获取到表情专题列表并在wifi下自动下载
                mEmojiList.clear();
                mEmojiList = emojiList;

                if (NetworkUtils.isWifi(mContext)) {
                    IPResourceManager.getInstance().downloadAllEmojis(emojiList);
                }

                mActivity.runOnUiThread(
                        new Runnable() {
                    @Override
                    public void run() {
                        if (isFirst) {
                            setLoadingMode(AppConstants.MODE_OK);
                        }
                        mEmojiListAdapter.notifyDataSetChanged();
                        setListViewPosition();
                    }
                });
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("getEmojiList: onNoNetwork.....");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showReloading();
                        ToastUtils.toast(getActivity(), R.string.no_network);
                    }
                });
            }

            @Override
            public void onErr(Object object) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showReloading();
                        ToastUtils.toast(getActivity(), R.string.connection_failed);
                    }
                });
            }
        });
    }

    public class EmojiListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private String defaultEmojisetId;

        public EmojiListAdapter() {
            if (mLayoutInflater == null) {
                mLayoutInflater = LayoutInflater.from(mContext);
            }
            defaultEmojisetId = WowoPreference.getInstance().getDefaultEmojisetId();
            MyLog.i("defaultEmojisetId="+defaultEmojisetId);
        }

        @Override
        public int getCount() {
            return mEmojiList != null ? mEmojiList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mEmojiList != null ? mEmojiList.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.emoji_list_item, null);
                holder.myGridView = (MyGridView) convertView.findViewById(R.id.emoji_grid);
                holder.emojiPoster = (ImageView) convertView.findViewById(R.id.emoji_poster);
                holder.mTitle = (TextView) convertView.findViewById(R.id.text_title);
                holder.mAuthor = (TextView) convertView.findViewById(R.id.text_author);
                holder.mNumber = (TextView) convertView.findViewById(R.id.text_emojinum);
                holder.mDownload = (Button) convertView.findViewById(R.id.btn_download);
                holder.mDesc = (TextView) convertView.findViewById(R.id.emoji_desc);
                holder.mZan = (TextView) convertView.findViewById(R.id.text_zan);
                holder.mShare = (TextView) convertView.findViewById(R.id.text_share);
                holder.mImageZan = (ImageView) convertView.findViewById(R.id.image_zan);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 赋值
            final EmojiResource emoji = mEmojiList.get(position);
            if(emoji == null) {
                return convertView;
            }
            if (emoji.getPics() != null && emoji.getPics().getBanner() != null)
                try {
                    ImageLoader.getInstance().displayImage(emoji.getPics().getBanner().getUri(), holder.emojiPoster, BitmapUtil.getImageLoaderOption());
                } catch (OutOfMemoryError e) {
                    MyLog.e(e);
                } catch (Exception e) {
                    MyLog.e(e);
                }
            holder.mTitle.setText(emoji.getName());
            if (!TextUtils.isEmpty(emoji.getAuthor())) {
                holder.mAuthor.setText(getString(R.string.myemj_author) + emoji.getAuthor());
            }
            holder.mNumber.setText(getString(R.string.myemj_num) + emoji.getEmojis().size() + getString(R.string.emoji_unit));
            holder.mDesc.setText(getString(R.string.emoji_desc) + emoji.getDesc());

            if (!emoji.isDownload() && !defaultEmojisetId.equals(emoji.getId())) {

                holder.mDownload.setText(getString(R.string.emoji_download) + " "  + convertPackageSize2M(emoji.getSize()));
                holder.mDownload.setEnabled(true);
                holder.mDownload.setBackgroundResource(R.drawable.tag_btn_solid_bg);
                holder.mDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!ClientInfo.isUserLogin()) {
                            ToastUtils.toast(mContext, R.string.emoji_download_tip);
                            Bundle b = new Bundle();
                            b.putBoolean(LoginActivity.KEY_NEED_RETURN, true);
                            b.putInt(LoginActivity.KEY_FROM, LoginConstants.EMOJI_RESOURCE);
                            b.putInt(LoginActivity.KEY_ACTION, position);
                            AppTools.toIntent(mContext, b, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                            return;
                        }
                        //下载表情
                        IPResourceManager.getInstance().downloadEmoji(emoji.getId(), new IListener() {
                            @Override
                            public void onSuccess(Object obj) {
                                final int result = (int) obj;
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (result == 1) {
                                            ToastUtils.toast(getActivity(), getString(R.string.emoji_download_succ));
                                            plusMyEmjCount(emoji.getId());
                                            IPResourceManager.getInstance().addEmojiIntoDB(ClientInfo.getUID(), emoji);

                                            //改变下载按钮的状态
                                            holder.mDownload.setText(getString(R.string.emoji_downloaded));
                                            holder.mDownload.setEnabled(false);
                                            holder.mDownload.setBackgroundResource(R.drawable.tag_btn_solid_grey_bg);
                                            mEmojiList.get(position).setIsDownload(true);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onNoNetwork() {

                            }

                            @Override
                            public void onErr(Object object) {

                            }
                        });
                    }
                });
            } else {
                holder.mDownload.setText(getString(R.string.emoji_downloaded));
                holder.mDownload.setEnabled(false);
                holder.mDownload.setBackgroundResource(R.drawable.tag_btn_solid_grey_bg);
                IPResourceManager.getInstance().addEmojiIntoDB(ClientInfo.getUID(), emoji);
            }

            List<EmojiInfo> list = emoji.getEmojis();
            IpDetailEmjGridAdapter grdiAdapter = new IpDetailEmjGridAdapter(mContext, list);
            holder.myGridView.setAdapter(grdiAdapter);
            //UiUtils.fixGridViewHeight(holder.myGridView, getResources().getDisplayMetrics().widthPixels);

            // 赞的点击事件
            setZanImage(holder.mImageZan, emoji.isLike());
            holder.mZan.setText(String.valueOf(emoji.getLikeCount()));

            holder.mImageZan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean mZan = mEmojiList.get(position).isLike();
                    int mZanCount = mEmojiList.get(position).getLikeCount();
                    mZan = !mZan;
                    if (mZan) {
                        mZanCount++;
                    } else {
                        mZanCount--;
                        if (mZanCount < 0)
                            mZanCount = 0;
                    }
                    mEmojiList.get(position).setIsLike(mZan);
                    mEmojiList.get(position).setLikeCount(mZanCount);
                    mHandler.sendEmptyMessage(REFRESH_MESSAGE);

                    int action = mZan ? 1:0;
                    IPResourceManager.getInstance().likeResource(2, emoji.getId(), action, new IListener() {
                        @Override
                        public void onSuccess(Object obj) {
                            MyLog.i("EmojiResourceAcitivy.onSucess:result="+(int)obj);
                        }

                        @Override
                        public void onNoNetwork() {

                        }

                        @Override
                        public void onErr(Object object) {

                        }
                    });
                }
            });

            // 分享的点击事件
            convertView.findViewById(R.id.image_share).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String shareUrl = IPResourceConstants.EMOJI_SHARE_URL + emoji.getId();
                    MyLog.i("share url=" + shareUrl);
                    UmengSocialCenter shareCenter = new UmengSocialCenter(getActivity());
                    shareCenter.setShareContent(emoji.getName(), emoji.getDesc(),
                            shareUrl, emoji.getPics().getCover().getUri());
                    shareCenter.openShare(getActivity());
                }
            });
            return convertView;
        }


        public class ViewHolder {
            public ImageView emojiPoster;//表情主题封面
            public TextView mTitle;//表情专题名称
            public TextView mAuthor;//表情专题作者
            public TextView mNumber;//表情个数
            public Button mDownload;//下载按钮
            public TextView mDesc;//表情专题作者
            public MyGridView myGridView;//表情图片的gridview
            public TextView mZan;//点赞的文字
            public TextView mShare;//分享的文字
            public ImageView mImageZan;//点赞图片
            public ImageView mImageShare;//分享的图片
        }
    }

    private void setZanImage(ImageView imageview, boolean bZan) {
        if (bZan) {
            imageview.setImageDrawable(getResources().getDrawable(R.drawable.btn_like_on));
        } else {
            imageview.setImageDrawable(getResources().getDrawable(R.drawable.btn_like));
        }
    }

    private void plusMyEmjCount(String id) {

        if(MinePreference.getInstance().isEmjDownloaded(id)) {
            MyLog.i(id + " is downloaded before");
            return;
        }

        MinePreference.getInstance().addEmjDownloadID(id);
        int count = MinePreference.getInstance().getEmjDownloadCount();
        MyLog.i(id + " is downloaded old count is " + count);
        MinePreference.getInstance().setEmjDownloadCount(count + 1);
        MyLog.i(id + " is downloaded new count is " + MinePreference.getInstance().getEmjDownloadCount());
    }

    private String convertPackageSize2M(long size) {
        double f = (double)size / 1024 / 1024;
        return String.format("%.1f", f) + "M";
    }

    /**
     * 收到用户点击下载而登陆的事件后要重新刷新表情列表
     * @param event
     */
    public void onEvent(LoginSuccJump.JumpEmojiResourceEvent event) {
        MyLog.i("onEvent:JumpEmojiResourceEvent, action id=" + event.getIndex());
        getEmojiList(false);
    }

    /**
     * 收到登陆事件后要重新刷新表情列表
     * @param event
     */
    public void onEvent(LoginSuccJump.LoginSuccessEvent event) {
        MyLog.i("onEvent:LoginSuccessEvent");
        getEmojiList(false);
    }

    /**
     * 收到用户下载了表情事件后要重新刷新表情列表
     * @param event
     */
    public void onEvent(EmojiChangeEvent event) {
        MyLog.i("onEvent:EmojiChangeEvent");
        getEmojiList(false);
    }

    /**
     * 收到用户下载了表情事件后要重新刷新表情列表
     * @param event
     */
    public void onEvent(SearchActivity.SearchEmojiEvent event) {
        MyLog.i("onEvent:SearchEmojiEvent");
        List<EmojiResource> list = event.getmEmojiList();
        if (list != null && list.size() > 0) {
            mEmojiList.clear();
            mEmojiList.addAll(list);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mEmojiListAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private class MyHandler extends Handler {

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try{
                switch (msg.what) {
                    case REFRESH_MESSAGE:
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mEmojiListAdapter.notifyDataSetChanged();
                            }
                        });
                        break;
                }
            } catch(Exception e){
                MyLog.e(e);
            }
        }
    }


    private void onLoad() {
        mList.stopRefresh();
        mList.stopLoadMore();
        mList.setRefreshTime(StringUtil.humanDate(System.currentTimeMillis(), WowoContants.COMMENT_TIMESTAMP_PATTERN));
    }

    @Override
    public void onRefresh() {
        long current = System.currentTimeMillis();
        long last = IPResourcePreference.getInstance().getLastGetEmojiTime();
        IPResourcePreference.getInstance().setLastGetEmojiTime(current);
        MyLog.i("onRefresh:emoji list, current=" + current + "last=" + last);
        if (current - last > 5 * 1000) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getEmojiList(false);
                }
            });
        }
        onLoad();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregist(this);
    }

}
