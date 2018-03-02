package com.moinapp.wuliao.modules.wowo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.AppConstants;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moinsocialcenter.MoinSocialFactory;
import com.moinapp.wuliao.commons.moinsocialcenter.UmengSocialCenter;
import com.moinapp.wuliao.commons.ui.XListView;
import com.moinapp.wuliao.modules.wowo.model.CommentInfo;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;
import com.moinapp.wuliao.modules.wowo.model.WowoInfo;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liujiancheng on 15/6/13.
 * 窝首页的推荐帖子列表tab
 */
public class FragmentSuggestPost extends Fragment implements XListView.IXListViewListener {
    // ===========================================================
    // Constants
    // ===========================================================
    ILogger MyLog = LoggerFactory.getLogger(FragmentSuggestPost.class.getSimpleName());

    // ===========================================================
    // Fields
    // ===========================================================

    /**
     * 推荐热帖列表
     */
    private List<PostInfo> mSuggestInfo = new ArrayList<PostInfo>();

    private ProgressDialog mWaitingDialog;
    private XListView mList;
    private MyListAdapter mAdapter;
    private TextView mEmptyTv;
    private Handler mHandler = new Handler();
    protected Context mContext;
    private ImageLoader mImageLoader;
    private boolean mPullDown = false;

    /**
     * loading需要的控件
     */
    RelativeLayout rl_enter;
    LinearLayout lv_loading;
    LinearLayout lv_reload;
    // ===========================================================
    // Constructors
    // ===========================================================
    public FragmentSuggestPost() {
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
        View downloadView = inflater.inflate(R.layout.wo_info_post, container, false);
        mList = (XListView) downloadView.findViewById(R.id.post_list);
        mEmptyTv = (TextView) downloadView.findViewById(R.id.empty_tv);

        mAdapter = new MyListAdapter(getActivity());
        mList.setAdapter(mAdapter);
        mList.setXListViewListener(this);
        mList.setPullLoadEnable(true);
        mList.setPullRefreshEnable(true);

        initLoadingView(downloadView);
        setLoadingMode(AppConstants.MODE_LOADING);
        getSuggestPostList(true);
        EventBus.getDefault().register(this);

        return downloadView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    private void initLoadingView(View root) {
        rl_enter = (RelativeLayout) root.findViewById(R.id.__enter);
        lv_loading = (LinearLayout) root.findViewById(R.id.__loading);
        lv_reload = (LinearLayout) root.findViewById(R.id.__reload);
        lv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoadingMode(AppConstants.MODE_LOADING);
                getSuggestPostList(false);
            }
        });
    }

    private void setLoadingMode(int mode) {
        rl_enter.setVisibility(mode == AppConstants.MODE_OK ? View.GONE : View.VISIBLE);
        lv_loading.setVisibility(mode == AppConstants.MODE_LOADING ? View.VISIBLE : View.GONE);
        lv_reload.setVisibility(mode == AppConstants.MODE_RELOADING ? View.VISIBLE : View.GONE);
    }

    private void showLoadingComplete() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setLoadingMode(AppConstants.MODE_OK);
                mList.stopLoadMore();
            }
        });
    }

    /**
     * 联网获取推荐的热帖列表
     */
    private void getSuggestPostList(final boolean isFirst) {
        //第一次先从缓存取
        if (isFirst) {
            final List<PostInfo> postLocal = WowoManager.getInstance().getSuggestPostFromCache(1);
            if (postLocal != null && postLocal.size() > 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mSuggestInfo.addAll(postLocal);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        WowoManager.getInstance().getSuggestPostList(getLastWoid(), getLastPostid(), 1, new IPostListener() {
            @Override
            public void onGetPostListSucc(final List<PostInfo> postInfoList, int column) {
                MyLog.i("getSuggestPostList.onGetPostListSucc.. postinfolist.size=" + postInfoList.size());
                showLoadingComplete();
                if (postInfoList == null || postInfoList.size() == 0) {
                    mPullDown = false;
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mPullDown || isFirst) {
                            mSuggestInfo.clear();
                        }
                        if (postInfoList.size() < 5) {
                            mList.setLoadMoreOver();
                            ToastUtils.toast(getActivity(), R.string.list_end);
                        }
                        mSuggestInfo.addAll(postInfoList);
                        mAdapter.notifyDataSetChanged();
                    }
                });

                //缓存到本地数据库
                WowoManager.getInstance().saveSuggestPostList(postInfoList, 1);
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

            }

            @Override
            public void onNoNetwork() {
                mPullDown = false;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mList.stopLoadMore();
                        ToastUtils.toast(getActivity(), R.string.no_network);
                        showReloading();
                    }
                });
            }

            @Override
            public void onErr(Object object) {
                mPullDown = false;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mList.stopLoadMore();
                        setLoadingMode(AppConstants.MODE_OK);
                        if (!isFirst && mSuggestInfo.size() > 0) {
                            mList.setLoadMoreOver();
                            ToastUtils.toast(getActivity(), R.string.list_end);
                        }
                    }
                });
            }
        });
    }

    private void showReloading() {
        if (mSuggestInfo.size() == 0) {
            setLoadingMode(AppConstants.MODE_RELOADING);
        } else {
            setLoadingMode(AppConstants.MODE_OK);
        }
    }

    private int getLastWoid() {
        if (mSuggestInfo.size() == 0) {
            return 0;
        }
        return mSuggestInfo.get(mSuggestInfo.size()-1).getWoid();
    }

    private String getLastPostid() {
        if (mSuggestInfo.size() == 0) {
            return null;
        }
        if (mPullDown) {
            return null;
        }
        return mSuggestInfo.get(mSuggestInfo.size()-1).getPostid();
    }

    private void onLoad() {
        mList.stopRefresh();
//        mList.stopLoadMore();
        mList.setRefreshTime(StringUtil.humanDate(System.currentTimeMillis(), WowoContants.COMMENT_TIMESTAMP_PATTERN));
    }

    @Override
    public void onRefresh() {
        mPullDown = true;
        mList.resetLoadMore();
        getPost();
    }

    @Override
    public void onLoadMore() {
        mPullDown = false;
        getPost();
    }

    private void getPost() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getSuggestPostList(false);
                onLoad();
            }
        }, 0);
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
            return mSuggestInfo != null ? mSuggestInfo.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mSuggestInfo != null ? mSuggestInfo.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.wo_info_post_item, null);
                viewHolder = getListViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ListViewHolder) convertView.getTag();
            }

            setContentView(viewHolder, position);

            return convertView;
        }

        private void setContentView(ListViewHolder viewHolder, int position) {
            if (mSuggestInfo != null && mSuggestInfo.size() > 0) {
                final PostInfo postInfo = mSuggestInfo.get(position);
                if (postInfo == null) {
                    return;
                }
                //设置每一个view
                if (postInfo.getWoicon() != null) {
                    try {
                        ImageLoader.getInstance().displayImage(postInfo.getWoicon().getUri(), viewHolder.mWoIcon, BitmapUtil.getImageLoaderOption());
                    } catch (OutOfMemoryError e) {
                        MyLog.e(e);
                    } catch (Exception e) {
                        MyLog.e(e);
                    }
                    //帖子封面点击
                    viewHolder.mPostCover.setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 跳转帖子详情
                                    gotoPostDetail(postInfo);
                                }
                    });
                }
                viewHolder.mWoName.setText(postInfo.getWoname());

                if (postInfo.getCover() != null) {
                    try {
                        ImageLoader.getInstance().displayImage(postInfo.getCover().getUri(), viewHolder.mPostCover, BitmapUtil.getImageLoaderOption());
                    } catch (OutOfMemoryError e) {
                        MyLog.e(e);
                    } catch (Exception e) {
                        MyLog.e(e);
                    }
                }
                if (postInfo.getAuthor().getAvatar() != null) {
                    try {
                        ImageLoader.getInstance().displayImage(postInfo.getAuthor().getAvatar().getUri(), viewHolder.mAuthorAvatar, BitmapUtil.getImageLoaderOption());
                    } catch (OutOfMemoryError e) {
                        MyLog.e(e);
                    } catch (Exception e) {
                        MyLog.e(e);
                    }
                }
                viewHolder.mAuthorAvatar.setOnClickListener(null);
                viewHolder.mAuthorName.setText(postInfo.getAuthor().getNickname());
                viewHolder.mAuthorName.setOnClickListener(null);

                if (postInfo.getAuthor().getLevel() != 10000) {
                    viewHolder.mAuthorLevel.setVisibility(View.INVISIBLE);
                }
                viewHolder.mPostUpdateAt.setText(StringUtil.humanDate(postInfo.getUpdatedAt(), WowoContants.COMMENT_TIMESTAMP_PATTERN));
                if (postInfo.getStatus() != 3) { //3是精华贴
                    viewHolder.mSuggestImage.setVisibility(View.GONE);
                }
                if (!postInfo.isHasImage()) {
                    viewHolder.mPicImage.setVisibility(View.GONE);
                }
                if (!postInfo.isHasEmoji()) {
                    viewHolder.mEmojiImage.setVisibility(View.GONE);
                }

                viewHolder.mPostTitle.setText(postInfo.getTitle());
                viewHolder.mPostDesc.setText(postInfo.getContent());

                // 推荐部分不可见
                viewHolder.mCommentLayout.setVisibility(View.GONE);

                // 设置分享
                final PostInfo post = postInfo;
                viewHolder.mShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UmengSocialCenter shareCenter = new UmengSocialCenter(getActivity());
                        shareCenter.setShareContent(post.getTitle(), post.getContent(), post.getShareUrl(), null);
                        shareCenter.openShare(mContext);
                    }
                });

                // 设置评论action
                viewHolder.mComment.setText(""  + postInfo.getCommentCount());
                viewHolder.mPostCotentArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 跳转帖子详情
                        gotoPostDetail(post);
                    }
                });
                viewHolder.mPostAuthorArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 跳转帖子详情
                        gotoPostDetail(post);
                    }
                });

                // 点击窝信息进入窝详情
                viewHolder.mWoInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle b = new Bundle();
                        b.putSerializable(WoPostListActivity.WO_ID, post.getWoid());
                        AppTools.toIntent(mContext, b, WoPostListActivity.class);
                    }
                });
            }
        }

        private void gotoPostDetail(PostInfo post) {
            Bundle b = new Bundle();
            b.putSerializable(PostDetailActivity.KEY_WO_ID, post.getWoid());
            b.putString(PostDetailActivity.KEY_POST_ID, post.getPostid());
            AppTools.toIntent(mContext, b, PostDetailActivity.class);
        }

        private ListViewHolder getListViewHolder(View convertView) {
            ListViewHolder holder = new ListViewHolder();
            holder.mWoInfo = (LinearLayout) convertView.findViewById(R.id.wo_info);
            holder.mPostAuthorArea = (RelativeLayout) convertView.findViewById(R.id.post_author_area);
            holder.mPostCotentArea = (LinearLayout) convertView.findViewById(R.id.post_content_area);
            holder.mWoIcon = (ImageView) convertView.findViewById(R.id.wo_cover);
            holder.mWoName = (TextView) convertView.findViewById(R.id.wo_name);
            holder.mPostCover = (ImageView) convertView.findViewById(R.id.post_cover);
            holder.mAuthorAvatar = (ImageView) convertView.findViewById(R.id.author_avatar);
            holder.mAuthorName = (TextView) convertView.findViewById(R.id.author_name);
            holder.mAuthorLevel = (TextView) convertView.findViewById(R.id.author_level);
            holder.mPostUpdateAt = (TextView) convertView.findViewById(R.id.post_time);
            holder.mSuggestImage = (ImageView) convertView.findViewById(R.id.post_suggest_image);
            holder.mPicImage = (ImageView) convertView.findViewById(R.id.post_pic_image);
            holder.mEmojiImage = (ImageView) convertView.findViewById(R.id.post_emoji_image);
            holder.mPostTitle = (TextView) convertView.findViewById(R.id.post_title);
            holder.mPostDesc = (TextView) convertView.findViewById(R.id.post_desc);
            holder.mCommentLayout = (LinearLayout) convertView.findViewById(R.id.recommend);
            holder.mShare = (ImageView) convertView.findViewById(R.id.wo_post_share);
            holder.mCommentImage = (ImageView) convertView.findViewById(R.id.wo_post_comment_image);
            holder.mComment = (TextView) convertView.findViewById(R.id.wo_post_comment);
            holder.mCommentView = (LinearLayout) convertView.findViewById(R.id.comment);

            return holder;
        }

        public class ListViewHolder {
            public LinearLayout mWoInfo; //窝info的layout，用于点击进入窝详情
            public RelativeLayout mPostAuthorArea; //帖子作者的点击区域
            public LinearLayout mPostCotentArea; //帖子内容的layout，用于点击进入帖子详情
            public ImageView mWoIcon; //窝 icon
            public TextView mWoName; //窝名字
            public ImageView mPostCover; //帖子封面
            public ImageView mAuthorAvatar; //帖子作者头像
            public TextView mAuthorName; //帖子作者
            public TextView mAuthorLevel; //帖子作者身份，版主等等
            public TextView mPostUpdateAt; //帖子发布时间
            public ImageView mSuggestImage; //帖子是精品贴的图标
            public ImageView mPicImage; //帖子包含图片的图标
            public ImageView mEmojiImage; //帖子包含表情的图标
            public TextView mPostTitle; //帖子标题
            public TextView mPostDesc; //帖子描述
            public LinearLayout mCommentLayout;//推荐部分的layout
            public ImageView mShare; //分享
            public LinearLayout mCommentView; //评论的view
            public TextView mComment; //帖子评论的个数
            public ImageView mCommentImage; //帖子评论的图标
        }
    }


}
