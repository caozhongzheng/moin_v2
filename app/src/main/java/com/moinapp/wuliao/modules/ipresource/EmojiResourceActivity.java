package com.moinapp.wuliao.modules.ipresource;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moinsocialcenter.MoinSocialConstants;
import com.moinapp.wuliao.commons.moinsocialcenter.MoinSocialFactory;
import com.moinapp.wuliao.commons.moinsocialcenter.UmengSocialCenter;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.ui.AsyncImageView;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.commons.ui.MyGridView;
import com.moinapp.wuliao.modules.ipresource.model.EmojiInfo;
import com.moinapp.wuliao.modules.ipresource.model.EmojiResource;
import com.moinapp.wuliao.modules.login.LoginActivity;
import com.moinapp.wuliao.modules.login.LoginConstants;
import com.moinapp.wuliao.modules.login.LoginSuccJump;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.modules.wowo.WowoPreference;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.NetworkUtils;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujiancheng on 15/5/28.
 */
public class EmojiResourceActivity extends BaseActivity implements View.OnClickListener {
    private static final ILogger MyLog = LoggerFactory.getLogger("era");

    // constants
    public static final String IPID = "ipid";
    public static final String IPNAME = "ipname";
    public static final String EMOJI_ID = "emoji_id";//由猜你喜欢的表情专题跳转带来的emoji id
    public static final String EMOJI_DATA = "emoji_data";
    public static final String SINGLE_EMOJI_ID = "single_emoji_id";//获取单个表情专题时传入的表情专题id
    public static final int REFRESH_MESSAGE = 1;
    // private field
    private Context mContext;
    private ListView mList;//表情专题的列表
    private TextView mEmojiIPTitle;//整个IP的标题
    private TextView mTextZan,mTextShare;//点赞和分享文本
    private ImageView mImageZan;//点赞和分享image
    private AsyncImageView mGuessLike1, mGuessLike2;
    private Dialog mDialog;
    private View mListFooterView;

    private String mIpId;//ip的id
    private String mIPName;//ip的name
    private String mEmojiID;//emoji专辑的ID
    private String mSingleEmojiID;//emoji专辑的ID
    private List<EmojiResource> mDefaultEmojiList;//外部传入的表情专辑列表

    private EmojiListAdapter mEmojiListAdapter;
    private List<EmojiResource> mEmojiList;//ip相关的表情专题列表
    private List<EmojiResource> mSuggest;//推荐的2个表情专题

    private MyHandler mHandler;
    private HandlerThread mHandlerThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.emoji_subject);

        EventBus.getDefault().register(this);
        //得到ip的i的id参数
        Intent intent = getIntent();
        if (intent != null) {
            mIpId = getIntent().getStringExtra(IPID);
            mIPName = getIntent().getStringExtra(IPNAME);
            mEmojiID = getIntent().getStringExtra(EMOJI_ID);
            mSingleEmojiID = getIntent().getStringExtra(SINGLE_EMOJI_ID);
            mDefaultEmojiList = (List<EmojiResource>) getIntent().getSerializableExtra(EMOJI_DATA);
        }

        mEmojiListAdapter = new EmojiListAdapter();
        mHandlerThread = new HandlerThread("LocateHandlerThread");
        mHandlerThread.start();
        mHandler = new MyHandler(mHandlerThread.getLooper());

        initLoadingView();
        setLoadingMode(MODE_LOADING);
        findviews();
        getEmojiList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregist(this);
    }

    @Override
    protected void reloadHandle() {
        super.reloadHandle();

        getEmojiList();
    }

    private void getEmojiList() {
        if (mDefaultEmojiList != null ) {
            // 是由搜索跳转过来的
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setLoadingMode(MODE_OK);
                    mEmojiList = mDefaultEmojiList;
                    MyLog.i("mDefaultEmojiList.size="+mDefaultEmojiList.size());
                    for (int i = 0; i < mDefaultEmojiList.size(); i++) {
                        MyLog.i("emoji:"+ mDefaultEmojiList.get(i).getName());
                    }
                    mEmojiListAdapter.notifyDataSetChanged();
                }
            });
        } else {
            mEmojiList = new ArrayList<EmojiResource>();
            IPResourceManager.getInstance().getEmojiList(mIpId, mSingleEmojiID, new IPEmojiListListener() {

                @Override
                public void onGetEmojiListSucc(List<EmojiResource> emojiList, final List<EmojiResource> suggest) {
                    if (emojiList == null || emojiList.size() == 0) {
                        setLoadingMode(MODE_OK);
                        return;
                    }
                    //获取到表情专题列表并下载
                    mEmojiList = emojiList;
                    if (!StringUtil.isNullOrEmpty(mEmojiID)) {
                        for (int i = 0; i < emojiList.size(); i++) {
                            EmojiResource tmp = emojiList.get(i);
                            if (tmp.getId().equals(mEmojiID)) {
                                mEmojiList.remove(i);
                                mEmojiList.add(0, tmp);
                                break;
                            }
                        }
                    }
                    mSuggest = suggest;

                    if (NetworkUtils.isWifi(mContext)) {
                        IPResourceManager.getInstance().downloadAllEmojis(emojiList);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setLoadingMode(MODE_OK);
                            mEmojiListAdapter.notifyDataSetChanged();

                            // 更新猜你喜欢的表情图片
                            if (suggest != null) {
                                AsyncImageView[] arr = new AsyncImageView[]{mGuessLike1, mGuessLike2};
                                for (int i = 0; i < suggest.size(); i++) {
                                    arr[i].loadImage("",suggest.get(i).getPics().getCover().getUri(), null, R.drawable.default_img);
                                    final int index = i;
                                    arr[i].setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Bundle b = new Bundle();
                                            b.putString(IPID, suggest.get(index).getIpid());
                                            b.putString(IPNAME, suggest.get(index).getName());
                                            b.putString(EMOJI_ID, suggest.get(index).getId());
                                            AppTools.toIntent(mContext, b, EmojiResourceActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                                        }
                                    });
                                }
                            }
                        }
                    });
                }

                @Override
                public void onNoNetwork() {
                    MyLog.i("getEmojiList: onNoNetwork.....");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setLoadingMode(MODE_RELOADING);
                            ToastUtils.toast(EmojiResourceActivity.this, R.string.no_network);
                        }
                    });
                }

                @Override
                public void onErr(Object object) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setLoadingMode(MODE_RELOADING);
                            ToastUtils.toast(EmojiResourceActivity.this, R.string.connection_failed);
                        }
                    });
                }
            });
        }
    }

    private void findviews() {
        String title;
        if (mDefaultEmojiList != null) {
            //由搜索跳转而来，标题显示搜索的关键字，不加表情专题字样
            title = mIPName;
        } else {
            title = (StringUtil.isNullOrEmpty(mIPName) ? "" : (mIPName + "-"))
                    + getString(R.string.myemoji);
        }
        initTopBar(title);

        initLeftBtn(true, R.drawable.back_gray);

        mList = (ListView) findViewById(R.id.emoji_list);
        mListFooterView = View.inflate(mContext, R.layout.emoji_guess_like, null);
        mGuessLike1 = (AsyncImageView) mListFooterView.findViewById(R.id.image_guess_like1);
        mGuessLike2 = (AsyncImageView) mListFooterView.findViewById(R.id.image_guess_like2);

        if (mIpId != null) {
            mList.addFooterView(mListFooterView);
        }
        mList.setAdapter(mEmojiListAdapter);
    }

    @Override
    public void onClick(View v) {

    }

    public class EmojiListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private String defaultEmojisetId;

        public EmojiListAdapter() {
            if (mLayoutInflater == null) {
                mLayoutInflater = LayoutInflater.from(mContext);
            }
            defaultEmojisetId = WowoPreference.getInstance().getDefaultEmojisetId();
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
            if (emoji == null) {
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (result == 1) {
                                            ToastUtils.toast(EmojiResourceActivity.this, getString(R.string.emoji_download_succ));
                                            plusMyEmjCount(emoji.getId());
                                            IPResourceManager.getInstance().addEmojiIntoDB(ClientInfo.getUID(), emoji);

                                            //改变下载按钮的状态
                                            holder.mDownload.setText(getString(R.string.emoji_downloaded));
                                            holder.mDownload.setEnabled(false);
                                            holder.mDownload.setBackgroundResource(R.drawable.tag_btn_solid_grey_bg);
                                            mEmojiList.get(position).setIsDownload(true);

                                            // 发送我的表情数据有变化的事件，通知我噻频道更新数据
                                            EventBus.getDefault().post(new EmojiChangeEvent());
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
            setZanImage(holder.mImageZan, mEmojiList.get(position).isLike());
            holder.mZan.setText(String.valueOf(mEmojiList.get(position).getLikeCount()));

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

                    int action = mZan ? 1 : 0;
                    IPResourceManager.getInstance().likeResource(2, emoji.getId(), action, new IListener() {
                        @Override
                        public void onSuccess(Object obj) {
                            MyLog.i("EmojiResourceAcitivy.onSucess:result=" + (int) obj);
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
                    UmengSocialCenter shareCenter = new UmengSocialCenter(EmojiResourceActivity.this);
                    shareCenter.setShareContent(emoji.getName(), emoji.getDesc(),
                            shareUrl, emoji.getPics().getCover().getUri());
                    shareCenter.openShare(EmojiResourceActivity.this);
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


    public void onEvent(LoginSuccJump.JumpEmojiResourceEvent event) {
        if (mDefaultEmojiList == null) {
            MyLog.i("onEvent:JumpEmojiResourceEvent, action id=" + event.getIndex());
            mEmojiList.clear();
            getEmojiList();
        }
    }

    @Override
    public void onBackPressed() {
        MyLog.i("EmojiResourceActivity.onBackPressed");
        finish();
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
                        runOnUiThread(new Runnable() {
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        UMSocialService mController = UMServiceFactory.getUMSocialService(MoinSocialConstants.NAME_UMENG);
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
