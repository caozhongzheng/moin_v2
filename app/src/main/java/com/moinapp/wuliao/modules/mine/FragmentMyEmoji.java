package com.moinapp.wuliao.modules.mine;

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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keyboard.db.DBHelper;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.AppConstants;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moinsocialcenter.MoinSocialFactory;
import com.moinapp.wuliao.commons.moinsocialcenter.UmengSocialCenter;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.modules.ipresource.EmojiChangeEvent;
import com.moinapp.wuliao.modules.ipresource.EmojiUtils;
import com.moinapp.wuliao.modules.ipresource.IPResourceConstants;
import com.moinapp.wuliao.modules.ipresource.IPResourceManager;
import com.moinapp.wuliao.modules.ipresource.model.EmojiResource;
import com.moinapp.wuliao.modules.mine.listener.MyEmojiListener;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.FileUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.moinapp.wuliao.utils.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujiancheng on 15/7/1.
 */
public class FragmentMyEmoji extends Fragment {
    // ===========================================================
    // Constants
    // ===========================================================
    ILogger MyLog = LoggerFactory.getLogger(FragmentMyEmoji.class.getSimpleName());
    public static final int REFRESH_MESSAGE = 1;

    protected Context mContext;
    private ImageLoader mImageLoader;

    private ListView mList;
    private static RelativeLayout mBottomLy;
    private static Button mDeleteBtn;
    private static MyEmojiAdapter mAdapter;
    /**
     * 所有的表情
     */
    private List<EmojiResource> mEmojiList;
    /**
     * 用户选择的表情
     */
    public static ArrayList<EmojiResource> mSelectedPost = new ArrayList<EmojiResource>();

    static int count;

    public static int mMode;
    private MyHandler mHandler;
    private HandlerThread mHandlerThread;
    /**
     * loading需要的控件
     */
    RelativeLayout rl_enter;
    LinearLayout lv_loading;
    LinearLayout lv_reload;
    // ===========================================================
    // Constructors
    // ===========================================================
    public FragmentMyEmoji() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity.getApplicationContext();

        //初始化图片缓存
        mImageLoader = ImageLoader.getInstance();
        if(!mImageLoader.isInited()) {
            mImageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MyLog.i("FragmentWoList: onCreateView........................");
        View view = inflater.inflate(R.layout.my_emoji_list, container, false);
        mList = (ListView) view.findViewById(R.id.emoji_list);
        mBottomLy = (RelativeLayout) view.findViewById(R.id.id_bottom_ly);
        mDeleteBtn = (Button) view.findViewById(R.id.id_delete);
        mDeleteBtn.setOnClickListener(deleteListener);

        mEmojiList = new ArrayList<EmojiResource>();
        mAdapter = new MyEmojiAdapter();
        mList.setAdapter(mAdapter);
        mMode = MineConstants.MODE_NONE;

        mHandlerThread = new HandlerThread("LocateHandlerThread");
        mHandlerThread.start();
        mHandler = new MyHandler(mHandlerThread.getLooper());

        initLoadingView(view);
        setLoadingMode(AppConstants.MODE_LOADING);
        getMyEmojiList();
//        EventBus.getDefault().register(this);

        return view;
    }

    private void initLoadingView(View root) {
        rl_enter = (RelativeLayout) root.findViewById(R.id.__enter);
        lv_loading = (LinearLayout) root.findViewById(R.id.__loading);
        lv_reload = (LinearLayout) root.findViewById(R.id.__reload);
        lv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoadingMode(AppConstants.MODE_LOADING);
                getMyEmojiList();
            }
        });
    }

    private void setLoadingMode(int mode) {
        rl_enter.setVisibility(mode == AppConstants.MODE_OK ? View.GONE : View.VISIBLE);
        lv_loading.setVisibility(mode == AppConstants.MODE_LOADING ? View.VISIBLE : View.GONE);
        lv_reload.setVisibility(mode == AppConstants.MODE_RELOADING ? View.VISIBLE : View.GONE);
    }

    private void showReloading() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLoadingMode(AppConstants.MODE_RELOADING);
                ToastUtils.toast(getActivity(), R.string.no_network);
            }
        });

    }

    private void getMyEmojiList() {
        MineManager.getInstance().getMyEmoji(new MyEmojiListener() {
            @Override
            public void getMyEmojiSucc(List<EmojiResource> emojis) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoadingMode(AppConstants.MODE_OK);
                    }
                });

                if (emojis == null || emojis.size() == 0) {
                    MyLog.i("getMyEmojiSucc: emojis list is empty");
                    return;
                }
                MyLog.i("getMyEmojiSucc: emoji list.size =" + emojis.size());
                mEmojiList = emojis;
                downloadAll(emojis);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void delEmojiSucc(int result) {

            }

            @Override
            public void onNoNetwork() {
                showReloading();
            }

            @Override
            public void onErr(Object object) {
                showReloading();
            }
        });
    }

    private void downloadAll(List<EmojiResource> emojis) {
        IPResourceManager.getInstance().downloadAllEmojis(emojis);
        IPResourceManager.getInstance().addEmojiIntoDB(ClientInfo.getUID(),emojis);
    }

    public class MyEmojiAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;

        public MyEmojiAdapter() {
            if (mLayoutInflater == null) {
                mLayoutInflater = LayoutInflater.from(mContext);
            }
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
            final holder holder;
            if (convertView == null) {
                holder = new holder();
                convertView = mLayoutInflater.inflate(R.layout.my_emoji_item, null);
                holder.mClickItem = (LinearLayout) convertView.findViewById(R.id.emoji_item);
                holder.emojiPoster = (ImageView) convertView.findViewById(R.id.emoji_poster);
                holder.mSelectIcon = (ImageView) convertView.findViewById(R.id.id_item_select);
                holder.mTitle = (TextView) convertView.findViewById(R.id.text_title);
                holder.mAuthor = (TextView) convertView.findViewById(R.id.text_author);
                holder.mNumber = (TextView) convertView.findViewById(R.id.text_emojinum);
                holder.mDesc = (TextView) convertView.findViewById(R.id.emoji_desc);
                holder.mZan = (TextView) convertView.findViewById(R.id.text_zan);
                holder.mImageZan = (ImageView) convertView.findViewById(R.id.image_zan);

                convertView.setTag(holder);
            } else {
                holder = (holder) convertView.getTag();
            }

            // 赋值
            final EmojiResource emoji = mEmojiList.get(position);
            if (emoji == null) {
                return convertView;
            }

            final String mStrDel = getString(R.string.emoji_delete);
            try {
                if (emoji.getPics() != null && emoji.getPics().getBanner() != null) {
                    try {
                        mImageLoader.displayImage(emoji.getPics().getBanner().getUri(), holder.emojiPoster, BitmapUtil.getImageLoaderOption());
                    } catch (OutOfMemoryError e) {
                        MyLog.e(e);
                    } catch (Exception e) {
                        MyLog.e(e);
                    }
                }
                holder.mTitle.setText(emoji.getName());
                if (!TextUtils.isEmpty(emoji.getAuthor())) {
                    holder.mAuthor.setText(emoji.getAuthor());
                }
                holder.mNumber.setText(emoji.getEmojis().size() + getString(R.string.emoji_unit));
                holder.mDesc.setText(getString(R.string.emoji_desc) + emoji.getDesc());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(mMode == MineConstants.MODE_EDIT) {
                holder.mSelectIcon.setVisibility(View.VISIBLE);
                if (mSelectedPost.contains(emoji)) {
                    holder.mSelectIcon.setImageResource(R.drawable.icon_select);
                } else {
                    holder.mSelectIcon.setImageResource(R.drawable.icon_unselect);
                }
                holder.mClickItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mSelectedPost.contains(emoji)) {
                            holder.mSelectIcon.setImageResource(R.drawable.icon_unselect);
                            mSelectedPost.remove(emoji);
                            count--;

                        } else {
                            holder.mSelectIcon.setImageResource(R.drawable.icon_select);
                            mSelectedPost.add(emoji);
                            count++;
                        }
                        if (count > 0) {
                            mDeleteBtn.setText(mStrDel + " (" + count + ")");
                            mDeleteBtn.setClickable(true);
                            mDeleteBtn.setBackground(getResources().getDrawable(R.drawable.tag_btn_solid_bg));
                        } else {
                            mDeleteBtn.setText(mStrDel);
                            mDeleteBtn.setClickable(false);
                            mDeleteBtn.setBackground(getResources().getDrawable(R.drawable.tag_btn_solid_grey_bg));
                        }
                    }
                });
            } else if(mMode == MineConstants.MODE_NONE) {
                holder.mSelectIcon.setVisibility(View.GONE);
                holder.mClickItem.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //进入表情图片页面
                        Intent i = new Intent(mContext, MyEmojiDetailActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
                        );
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable(MyEmojiDetailActivity.EMOJI, emoji);
                        i.putExtras(mBundle);
                        mContext.startActivity(i);
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
            }

            
            return convertView;
        }


        public class holder {
            public LinearLayout mClickItem;
            public ImageView emojiPoster;//表情主题封面
            public ImageView mSelectIcon;//表情被选中图标
            public TextView mTitle;//表情专题名称
            public TextView mAuthor;//表情专题作者
            public TextView mNumber;//表情个数
            public TextView mDesc;//表情专题描述
            public ImageView mShare;//分享的图片
            public TextView mZan;//点赞的文字
            public ImageView mImageZan;//点赞图片
        }
    }

    private View.OnClickListener deleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (Tools.isFastDoubleSend()) {
                return;
            }
            if(mSelectedPost != null && mSelectedPost.size() > 0) {
                List<String> emjIDs = new ArrayList<>();
                for (int i = 0; i < mSelectedPost.size(); i++) {
                    emjIDs.add(mSelectedPost.get(i).getId());
                }
                MineManager.getInstance().delMyEmoji(emjIDs, new MyEmojiListener() {
                    @Override
                    public void getMyEmojiSucc(List<EmojiResource> emojis) {

                    }

                    @Override
                    public void delEmojiSucc(int result) {
                        DBHelper dbHelper = new DBHelper(mContext);
                        for (int i = 0; i < mSelectedPost.size(); i++) {
                            // 删除发帖时用的表情
                            dbHelper.deleteEmoticonSet(mSelectedPost.get(i).getId());
                            FileUtil.delAllFilesInFolder(EmojiUtils.getEmjSetFolder(mSelectedPost.get(i).getId()));
                            mEmojiList.remove(mSelectedPost.get(i));
                        }
                        mSelectedPost.clear();
                        count = 0;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                mDeleteBtn.setText(getString(R.string.emoji_delete));
                            }
                        });

                        // 发送我的表情数据有变化的事件，通知我噻频道更新数据
                        EventBus.getDefault().post(new EmojiChangeEvent());
                    }

                    @Override
                    public void onNoNetwork() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.toast(mContext, R.string.no_network);
                            }
                        });
                    }

                    @Override
                    public void onErr(Object object) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.toast(mContext, R.string.connection_failed);
                            }
                        });
                    }
                });
            }
        }
    };

    private void setZanImage(ImageView imageview, boolean bZan) {
        if (bZan) {
            imageview.setImageDrawable(getResources().getDrawable(R.drawable.btn_like_on));
        } else {
            imageview.setImageDrawable(getResources().getDrawable(R.drawable.btn_like));
        }
    }

    public ArrayList<EmojiResource> getSelectedPost() {
        return mSelectedPost;
    }

    public void setSelectedPost(ArrayList<EmojiResource> selectedPost) {
        mSelectedPost = selectedPost;
        count = mSelectedPost.size();
    }

    public void setMode(int mode) {
        mMode = mode;
        mBottomLy.setVisibility(mMode == MineConstants.MODE_EDIT ? View.VISIBLE : View.GONE);
        if (mMode == MineConstants.MODE_NONE) {
            mSelectedPost.clear();
            mDeleteBtn.setText(R.string.delete);
        } else {
            if (count == 0) {
                mDeleteBtn.setClickable(false);
                mDeleteBtn.setBackground(getResources().getDrawable(R.drawable.tag_btn_solid_grey_bg));
            }
        }
        setSelectedPost(new ArrayList<EmojiResource>());
        mAdapter.notifyDataSetChanged();
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
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                        break;
                }
            } catch(Exception e){
                MyLog.e(e);
            }
        }
    }
}
