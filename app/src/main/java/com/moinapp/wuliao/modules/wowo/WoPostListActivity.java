package com.moinapp.wuliao.modules.wowo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.MoinActivity;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.commons.ui.XListView;
import com.moinapp.wuliao.modules.ipresource.IPResourceConstants;
import com.moinapp.wuliao.modules.login.LoginActivity;
import com.moinapp.wuliao.modules.login.LoginConstants;
import com.moinapp.wuliao.modules.login.LoginSuccJump;
import com.moinapp.wuliao.modules.wowo.model.CommentInfo;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;
import com.moinapp.wuliao.modules.wowo.model.WowoInfo;
import com.moinapp.wuliao.utils.AnimationUtil;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.DisplayUtil;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujiancheng on 15/6/12.
 * 窝帖子列表页：分三个tab：全部帖子 精华贴 表情贴
 */
public class WoPostListActivity extends BaseActivity implements  View.OnClickListener {
    private static final ILogger MyLog = LoggerFactory.getLogger("wpl");
    private static final String COLUMN = "column";
    public static final String WO_ID = "wo_id";
    public static final String WO_STATUS = "wo_status";
    public static final String FROM = "from";
    private static final int COLUMN_COUNT = 2; //3个栏目
    private static final int CURSOR_ANIMATION_MILLISECONDS = 200;
    private static final int REFRESH_FROM_FIRST_TIME = 0;
    private static final int REFRESH_FROM_NEW_INTENT = 1;
    private static final int REFRESH_FROM_PULL_DOWN = 2;
    private static final int REFRESH_FROM_PULL_UP = 3;
    private static final int POSTS_PER_PAGE = 5; //每页5个帖子

    private Context mContext;

    private RelativeLayout mTitle;
    //
    private ViewPager viewPager;
    private TextView tvColumnAll, tvColumnSuggest, tvColumnEmoji;
    private ImageView ivCursor;
    private int screenWidth;
    private int cursorWidth;
    private int cursorLastX;
    private int cursorPadingPx;
    private int backWidth;
    private int newPostWidth;

    private RelativeLayout.LayoutParams lpCursor;
    // 数据
    private XListView[] mListView;
    private LinearLayout[] mListHeadView;
    private PostListAdapter[] mPostListAdapters;
    private int[] visibleLastIndex = {0, 0, 0};
    private int currIndex = 0;// 主题列表ViewPager当前页卡编号

    private boolean[] loadedFlags;
    private boolean[] isScrolling;
    private int scrollBy;

    private WowoInfo mWoInfo;
    private int mWoid;
    private WowoManager mWowoManager;
    private ImageLoader mImageLoader;
    private ImageView mNewPost;//发新贴的图片
    private ImageView mBack;//回退的图片

    private Handler mHandler = new Handler();

    private int[] mRefreshFlag = new int[COLUMN_COUNT];
    private boolean mRefreshData = true;

    private int mFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyLog.i("WoPostListActivity........... onCreate");
        super.onCreate(savedInstanceState);

        if(getIntent() != null) {
            mFrom = getIntent().getIntExtra(FROM, 0);
        }
        getWoInfo();
        mContext = WoPostListActivity.this;
        mWowoManager = WowoManager.getInstance();

        EventBus.getDefault().register(this);
        //初始化图片缓存
        mImageLoader = ImageLoader.getInstance();
        if (!mImageLoader.isInited()) {
            mImageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }

        cursorPadingPx = DisplayUtil.dip2px(mContext, 6);
        scrollBy = DisplayUtil.dip2px(mContext, 10);
        setContentView(R.layout.wo_post);

        for (int i = 0; i < COLUMN_COUNT; i++) {
            mRefreshFlag[i] = REFRESH_FROM_FIRST_TIME;
        }
        int column = getColumn();

        initLoadingView();
        setLoadingMode(MODE_LOADING);
        findViews(column);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        MyLog.i("WoPostListActivity........... onNewIntent");
        this.setIntent(intent);
        getWoInfo();
        //如果是从别的跳转过来，等于重新刷新所有三个tab的帖子
        for (int i = 0; i < COLUMN_COUNT; i++) {
            mRefreshFlag[i] = REFRESH_FROM_NEW_INTENT;
        }
        for (int i = 0; i < COLUMN_COUNT; i++) {
            getPostList(i);
        }
        int column = getColumn();
        if (viewPager.getCurrentItem() != column) {
            mRefreshData = false;
            viewPager.setCurrentItem(column);
        }
    }

    @Override
    protected void reloadHandle() {
        super.reloadHandle();
        for (int i = 0; i < COLUMN_COUNT; i++) {
            mPostListAdapters[i].emptyPostList();
            getPostList(i);
        }
    }

    private void getWoInfo() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }
        mWoid = intent.getIntExtra(WO_ID, 0);
        if (mWoid == 0) {
            finish();
        }
    }


    private int getColumn() {
        int column = 0;
        Intent intent = getIntent();
        if (intent != null) {
            column = intent.getIntExtra(COLUMN, 0);
        }
        return column;
    }

    private void findViews(int column) {

        // 初始化栏目指示器，因为有3个栏目和左右各一个图标，所以简单计算滑动条的宽度为屏幕宽度／5
        ivCursor = (ImageView) findViewById(R.id.iv_cursor);
        screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;// 获取分辨率宽度

        // 初始化栏目
        tvColumnAll = (TextView) findViewById(R.id.all_posts);
        tvColumnAll.setTextColor(getResources().getColor(R.color.common_text_main));
        tvColumnSuggest = (TextView) findViewById(R.id.hot_posts);
//        tvColumnEmoji = (TextView) findViewById(R.id.emoji_posts);
        mNewPost = (ImageView) findViewById(R.id.new_post);
        mBack = (ImageView) findViewById(R.id.back);
        // 初始化栏目列表
        viewPager = (ViewPager) findViewById(R.id.vp_list);
        viewPager.setAdapter(new PostListPagerAdapter());
        viewPager.setCurrentItem(column);
        viewPager.setOnPageChangeListener(new PostOnPageChangeListener());

        mPostListAdapters = new PostListAdapter[COLUMN_COUNT];
        for (int i = 0; i < COLUMN_COUNT; i++) {
            mPostListAdapters[i] = new PostListAdapter();
        }
        mListView = new XListView[COLUMN_COUNT];
        mListHeadView = new LinearLayout[COLUMN_COUNT];

        loadedFlags = new boolean[COLUMN_COUNT];
        isScrolling = new boolean[COLUMN_COUNT];
        tvColumnAll.setOnClickListener(this);
        tvColumnSuggest.setOnClickListener(this);
//        tvColumnEmoji.setOnClickListener(this);
        mNewPost.setOnClickListener(this);
        mBack.setOnClickListener(this);

        backWidth = getResources().getDimensionPixelOffset(R.dimen.wowo_post_margin_left);
        newPostWidth = getResources().getDimensionPixelOffset(R.dimen.wowo_post_margin_right);
        initCursorParams(column);
//        mBack.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            boolean isFirst = true;//默认调用两次，这里只让它执行一次回调
//
//            @Override
//            public void onGlobalLayout() {
//                if (isFirst) {
//                    isFirst = false;
//                    //现在布局全部完成，可以获取到任何View组件的宽度、高度、左边、右边等信息
//                    MyLog.i("Global W:" + mBack.getMeasuredWidth() + "  H:" + mBack.getMeasuredHeight());
//                    backWidth = mBack.getMeasuredWidth();
//                }
//                mBack.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//            }
//        });
//        mNewPost.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            boolean isFirst = true;
//
//            @Override
//            public void onGlobalLayout() {
//                if (isFirst) {
//                    isFirst = false;
//                    MyLog.i("Global W:" + mNewPost.getMeasuredWidth() + "  H:" + mNewPost.getMeasuredHeight());
//                    newPostWidth = mNewPost.getMeasuredWidth();
//                    initCursorParams(column);
//                }
//                mNewPost.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//            }
//        });

    }

    private void initCursorParams(int column) {
        cursorWidth = (int) ((screenWidth - backWidth - newPostWidth) / COLUMN_COUNT + 0.5f);
        cursorLastX = backWidth;//初始化时滑动条位置在第一个栏目（back图标右边）
        lpCursor = new RelativeLayout.LayoutParams(cursorWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        ivCursor.setLayoutParams(lpCursor);

        //初始化指示器
        setIndicator(column);
    }

    /**
     * 主题模块，栏目切换PagerAdapter
     */
    public class PostListPagerAdapter extends PagerAdapter {

        public PostListPagerAdapter() {
        }

        @Override
        public int getCount() {
            return COLUMN_COUNT;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            MyLog.i("WoPostListActivity........... instantiateItem...position=" + position);
            final LinearLayout postListLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.wo_posts_list, null);

            final int column = position;
            final XListView listView = (XListView) postListLayout.findViewById(R.id.lv_list);
            //设置窝的信息
            LinearLayout wo_headView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.wo_info_header, null);
            listView.addHeaderView(wo_headView);
            mListHeadView[position] = wo_headView;

            listView.setPullLoadEnable(true);
            listView.setPullRefreshEnable(true);
            listView.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    mRefreshFlag[column] = REFRESH_FROM_PULL_DOWN;
                    listView.resetLoadMore();
                    getPost(column);
                }

                @Override
                public void onLoadMore() {
                    mRefreshFlag[column] = REFRESH_FROM_PULL_UP;
                    getPost(column);
                }
            });
            mListView[position] = listView;
            listView.setAdapter(mPostListAdapters[position]);

            container.addView(postListLayout, 0);

            if (viewPager.getCurrentItem() == 0) {//只加载当前栏目
                getPostList(position);
            }

            if (mWoInfo != null && wo_headView != null)
                setListHeadView(wo_headView);
            return postListLayout;
        }
    }

    private void getPost(final int column) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getPostList(column);
                onLoad(column);
            }
        }, 0);
    }

    private void onLoad(int column) {
        mListView[column].stopRefresh();
//        mListView[column].stopLoadMore();
        mListView[column].setRefreshTime(StringUtil.humanDate(System.currentTimeMillis(), WowoContants.COMMENT_TIMESTAMP_PATTERN));
    }

    /**
     * 帖子列表viewPager，栏目切换监听。
     */
    public class PostOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            MyLog.i("onPageSelected");
            setIndicator(position);
            int offset = mPostListAdapters[currIndex].getPostList().size();
            if (offset == 0 && mRefreshData) {//ListView为空，需要重新加载
                MyLog.i("offset=0,ListView为空，需要重新加载" + position);
                getPostList(currIndex);
            } else {
                setListHeadView(mListHeadView[position]);
            }

            //恢复切换tab后如果没有数据的话获取帖子的标记
            if (!mRefreshData) {
                mRefreshData = true;
            }
        }
    }

    /**
     * 给窝信息赋值
     *
     * @param wo_headView
     */
    private void setListHeadView(LinearLayout wo_headView) {
        MyLog.i("setListHeadView is coming....mWowoInfo.isFavorite =" + mWoInfo.isFavorite());
        ImageView woCover = (ImageView) wo_headView.findViewById(R.id.wo_cover);
        TextView woName = (TextView) wo_headView.findViewById(R.id.wo_name);
        TextView commentCount = (TextView) wo_headView.findViewById(R.id.comment_count);
        TextView postNumber = (TextView) wo_headView.findViewById(R.id.post_number);
        LinearLayout tags = (LinearLayout) wo_headView.findViewById(R.id.wo_tags_ll);
        TextView woDesc = (TextView) wo_headView.findViewById(R.id.wo_desc);
        ImageView woRule = (ImageView) wo_headView.findViewById(R.id.wo_rule);
        ImageView woAttention = (ImageView) wo_headView.findViewById(R.id.image_attention);

        if (mWoInfo.getIcon() != null)
            try {
                mImageLoader.displayImage(mWoInfo.getIcon().getUri(), woCover, BitmapUtil.getImageLoaderOption());
            } catch (OutOfMemoryError e) {
                MyLog.e(e);
            } catch (Exception e) {
                MyLog.e(e);
            }
        woName.setText(mWoInfo.getName());
        commentCount.setText("关注：" + String.valueOf(mWoInfo.getUserCount()));
        postNumber.setText("帖子：" + String.valueOf(mWoInfo.getPostCount()));

        String tag = getTags();
        tags.removeAllViews();
        if(!StringUtil.isNullOrEmpty(tag)) {
            String[] tagarr = tag.split(",");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            params.rightMargin = getResources().getDimensionPixelSize(R.dimen.icon_margin);
            for (int j = 0; j < tagarr.length; j++) {
                if(!StringUtil.isNullOrEmpty(tagarr[j])) {
                    TextView tv_tag = new TextView(WoPostListActivity.this);
                    tv_tag.setText(tagarr[j]);
                    tv_tag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    tv_tag.setTextColor(getResources().getColor(R.color.common_text_grey));
                    tv_tag.setBackground(getResources().getDrawable(R.drawable.tag_btn_grey_bg));
                    tv_tag.setPadding(5, 1, 5, 1);
                    tags.addView(tv_tag, params);
                }
            }
        }

        woDesc.setText(mWoInfo.getIntro());

        if (ClientInfo.isUserLogin()) {
            if (mWoInfo.isFavorite()) {
                woRule.setVisibility(View.VISIBLE);
                woAttention.setVisibility(View.GONE);
            } else {
                woRule.setVisibility(View.GONE);
                woAttention.setVisibility(View.VISIBLE);
                woAttention.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //点击关注
                        attentionWo();
                    }
                });
            }
        } else {
            //如果是未登陆状态，则所有窝都认为是未关注，引导用户点击关注去登陆
            woRule.setVisibility(View.GONE);
            woAttention.setVisibility(View.VISIBLE);
            woAttention.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //进入登录界面
                    Bundle b = new Bundle();
                    b.putBoolean(LoginActivity.KEY_NEED_RETURN, true);
                    b.putInt(LoginActivity.KEY_FROM, LoginConstants.WO_POST_LIST_ATTENTION);
                    b.putInt(LoginActivity.KEY_ACTION, currIndex);
                    AppTools.toIntent(mContext, b, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            });
        }
        wo_headView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进入窝规则
                Bundle b = new Bundle();
                b.putSerializable("wowo_info", mWoInfo);
                AppTools.toIntent(mContext, b, WowoRulesActivity.class);
            }
        });
    }

    private void attentionWo() {
        WowoManager.getInstance().subscribeWo(mWoInfo.getId(), 1, new IWoListener() {
            @Override
            public void onErr(Object object) {

            }

            @Override
            public void onNoNetwork() {

            }

            @Override
            public void onGetWowoSucc(List<WowoInfo> wowoInfoList) {

            }

            @Override
            public void onSubscribeResult(int result) {
                MyLog.i("onSubscribeResult succ, result" + result);
                mWoInfo.setIsFavorite(true);
                if (result == 1) {
                    EventBus.getDefault().post(new WowoRulesActivity.AttentionEvent(mWoInfo, 1));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateListHeader();
                        }
                    });
                }
            }
        });
    }

    private void updateListHeader() {
        for (int i = 0; i < COLUMN_COUNT; i++) {
            if (mListHeadView[i] != null)
                setListHeadView(mListHeadView[i]);
        }
    }

    private String getTags() {
        String result = null;
        StringBuilder woTag = new StringBuilder();
        if (mWoInfo.getTags() != null) {
            for (String tag : mWoInfo.getTags()) {
                woTag.append(tag).append(",");
            }
            String tag = woTag.toString();
            if (!TextUtils.isEmpty(tag))
                result = tag.substring(0, tag.length() - 1);

        }
        return result;
    }

    private void setIndicator(int position) {
        currIndex = position;

        tvColumnAll.setTextColor(getResources().getColor(R.color.common_title_grey));
        tvColumnSuggest.setTextColor(getResources().getColor(R.color.common_title_grey));
//        tvColumnEmoji.setTextColor(getResources().getColor(R.color.common_title_grey));

        switch (currIndex) {
            case 0:
                tvColumnAll.setTextColor(getResources().getColor(R.color.common_text_main));
                AnimationUtil.moveTo(ivCursor, CURSOR_ANIMATION_MILLISECONDS, cursorLastX, backWidth, 0, 0);
                break;
            case 1:
                tvColumnSuggest.setTextColor(getResources().getColor(R.color.common_text_main));
                AnimationUtil.moveTo(ivCursor, CURSOR_ANIMATION_MILLISECONDS, cursorLastX, cursorWidth + backWidth, 0, 0);
                break;
//            case 2:
//                tvColumnEmoji.setTextColor(getResources().getColor(R.color.common_text_main));
//                AnimationUtil.moveTo(ivCursor, CURSOR_ANIMATION_MILLISECONDS, cursorLastX, 2 * cursorWidth + backWidth, 0, 0);
//                break;
            default:
                break;
        }
        cursorLastX = cursorWidth * currIndex + backWidth;
        ivCursor.setLayoutParams(lpCursor);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.all_posts) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.hot_posts) {
            viewPager.setCurrentItem(1);
//        } else if (id == R.id.emoji_posts) {
//            viewPager.setCurrentItem(2);
        } else if (id == R.id.new_post) {
            gotoNewPost();
        } else if (id == R.id.back) {
            if(mFrom == 1) {
                setResult(RESULT_OK, new Intent());
            }
            finish();
        } else if (id == R.id.iv_nonetwork) {
            getPostList(currIndex);
        } else if (id == R.id.iv_empty) {
            getPostList(currIndex);
        }
    }

    /**
     * 发新帖
     */
    private void gotoNewPost() {
        // if not login, jump to Login Activity
        if (!ClientInfo.isUserLogin()) {
            ToastUtils.toast(mContext, R.string.login_first);
            Bundle b = new Bundle();
            b.putBoolean(LoginActivity.KEY_NEED_RETURN, true);
            b.putInt(LoginActivity.KEY_FROM, LoginConstants.WO_POST_LIST_NEWPOST);
            AppTools.toIntent(mContext, b, LoginActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
            return;
        }

        Bundle b = new Bundle();
        b.putInt(PostActivity.KEY_WO_ID, mWoid);
        AppTools.toIntent(mContext, b, PostActivity.class);
    }

    /**
     *  回退到窝窝频道
     */
    private void gotoWowo() {
        Bundle b = new Bundle();
        //todo 跳转到窝窝频道的所在页面，column具体是几可能会会变
        b.putInt(MoinActivity.KEY_FRAGMENT_INDEX_TO_SHOW, 2);
        AppTools.toIntent(mContext, b, MoinActivity.class);
    }

    /**
     * 窝中列表Adapter
     */
    private class PostListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<PostInfo> mPostArrList;

        public PostListAdapter() {
            this.mInflater = LayoutInflater.from(mContext);
            this.mPostArrList = new ArrayList<PostInfo>();
        }

        public List<PostInfo> getPostList() {
            return mPostArrList;
        }


        public void emptyPostList() {
            mPostArrList.clear();
        }

        @Override
        public int getCount() {
            int count = 0;
            if (mPostArrList != null) {
                return mPostArrList.size();
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            return position;
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
                convertView = mInflater.inflate(R.layout.wo_post_list_item, null);
                holder.mPostItem = (LinearLayout) convertView.findViewById(R.id.post_item);
                holder.mAuthorAvatar = (ImageView) convertView.findViewById(R.id.author_avatar);
                holder.mAuthorName = (TextView) convertView.findViewById(R.id.author_name);
                holder.mAuthorLevel = (TextView) convertView.findViewById(R.id.author_level);
                holder.mPostTime = (TextView) convertView.findViewById(R.id.post_time);
                holder.mCommentCount = (TextView) convertView.findViewById(R.id.comment_count);
                holder.mSuggestImage = (ImageView) convertView.findViewById(R.id.post_suggest_image);
                holder.mPicImage = (ImageView) convertView.findViewById(R.id.post_pic_image);
                holder.mEmojiImage = (ImageView) convertView.findViewById(R.id.post_emoji_image);
                holder.mPostTitle = (TextView) convertView.findViewById(R.id.post_title);
                holder.mPostContent = (TextView) convertView.findViewById(R.id.post_desc);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 赋值
            final PostInfo postInfo = mPostArrList.get(position);
            if (postInfo != null) {
                if (postInfo.getAuthor().getAvatar() != null) {
                    try {
                        mImageLoader.displayImage(postInfo.getAuthor().getAvatar().getUri(), holder.mAuthorAvatar, BitmapUtil.getImageLoaderOption());
                    } catch (OutOfMemoryError e) {
                        MyLog.e(e);
                    } catch (Exception e) {
                        MyLog.e(e);
                    }
                    holder.mAuthorName.setText(postInfo.getAuthor().getNickname());
                } else {
                    holder.mAuthorAvatar.setImageResource(R.drawable.default_img);
                    holder.mAuthorName.setText(R.string.ip_author);
                }
                holder.mAuthorAvatar.setOnClickListener(null);
                holder.mAuthorName.setOnClickListener(null);

                if (postInfo.getAuthor() != null && postInfo.getAuthor().getLevel() != 10000) {
                    holder.mAuthorLevel.setVisibility(View.INVISIBLE);
                }
                holder.mPostTime.setText(StringUtil.formatDate(postInfo.getUpdatedAt(), IPResourceConstants.IP_RELEASE_DATE_FORMAT)); //todo 格式要做成人性化时间
                if (postInfo.getStatus() != 3) { //3是精华贴
                    holder.mSuggestImage.setVisibility(View.GONE);
                } else {
                    holder.mSuggestImage.setVisibility(View.VISIBLE);
                }
                if (!postInfo.isHasImage()) {
                    holder.mPicImage.setVisibility(View.GONE);
                } else {
                    holder.mPicImage.setVisibility(View.VISIBLE);
                }
                if (!postInfo.isHasEmoji()) {
                    holder.mEmojiImage.setVisibility(View.GONE);
                } else {
                    holder.mEmojiImage.setVisibility(View.VISIBLE);
                }

                holder.mPostTitle.setText(postInfo.getTitle());
                holder.mPostContent.setText(postInfo.getContent());
                holder.mCommentCount.setText("" + postInfo.getCommentCount());
                MyLog.i("ljc: WoPostActivity: post-->woid =" + postInfo.getWoid() + "id =" + postInfo.getPostid() + ", post title =" + postInfo.getTitle());
                holder.mPostItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 跳转帖子详情
                        Bundle b = new Bundle();
                        b.putSerializable(PostDetailActivity.KEY_WO_ID, mWoid);
                        b.putString(PostDetailActivity.KEY_POST_ID, postInfo.getPostid());
                        MyLog.i("跳转帖子详情:woid=" + postInfo.getWoid() + ", postid=" + postInfo.getPostid());
                        AppTools.toIntent(mContext, b, PostDetailActivity.class);
                    }
                });
            }

            return convertView;
        }

    }

    private static class ViewHolder {
        LinearLayout mPostItem;
        ImageView mAuthorAvatar;//作者头像
        TextView mAuthorName;//作者名称
        TextView mAuthorLevel;//作者级别
        TextView mPostTime;//帖子更新时间
        TextView mCommentCount;//评论数
        ImageView mSuggestImage; //帖子是精品贴的图标
        ImageView mPicImage; //帖子包含图片的图标
        ImageView mEmojiImage; //帖子包含表情的图标
        TextView mPostTitle;//帖子标题
        TextView mPostContent;//帖子内容
    }

    /**
     * 获取帖子列表
     */
    private void getPostList(int column) {
        MyLog.i("getPostList: column=" + column);

        mWowoManager.getWoPostList(mWoid, getLastPostid(column), column, new BasicPostListListener(column, true));
    }

    /**
     * 刷新列表视图
     */
    private void updatePostList(int column, List<PostInfo> postInfos) {
        MyLog.d("updatePostList: column=" + column);

        if (postInfos == null || postInfos.size() == 0) {
            return;
        }

        if (mRefreshFlag[column] == REFRESH_FROM_PULL_DOWN || mRefreshFlag[column] == REFRESH_FROM_NEW_INTENT) {
            //如果是下拉刷新，清空adaptor
            mPostListAdapters[column].emptyPostList();
        }
        // 去重处理
        if (mPostListAdapters[column].getPostList().size() > 0) {
            for (PostInfo post : postInfos) {
                if (!WowoManager.getInstance().isPostInList(post, mPostListAdapters[column].getPostList())) {
                    mPostListAdapters[column].getPostList().add(post);
                }
            }
        } else {
            mPostListAdapters[column].getPostList().addAll(postInfos);
        }

        mPostListAdapters[column].notifyDataSetChanged();

        //重置刷新flag
//        mRefreshFlag = REFRESH_FROM_FIRST_TIME;
    }

    /**
     * 从网络获取帖子列表监听
     */
    private class BasicPostListListener implements IPostListener {
        private int mColumn;
        private boolean mPullrefresh;

        public BasicPostListListener(int column, boolean pullrefresh) {
            mColumn = column;
            mPullrefresh = pullrefresh;
        }

        @Override
        public void onErr(Object object) {
            MyLog.d("onErr");
            isScrolling[currIndex] = false;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MyLog.d("onErr.runOnUiThread");

                    //如果错误，标志已经是最后一页了。 因为从服务器已经获取不到数据，返回空列表会回调到onError
                    loadedFlags[mColumn] = true;
                    setLoadingMode(MODE_OK);

                    mListView[mColumn].stopLoadMore();
                    // 往上拉刷新没有数据的话弹出全部加载完毕的toast
                    if (mRefreshFlag[mColumn] ==  REFRESH_FROM_PULL_UP && mPostListAdapters[mColumn].getPostList().size() > 0) {
                        mListView[mColumn].setLoadMoreOver();
                        ToastUtils.toast(mContext, R.string.list_end);
                    }
                }
            });
        }

        @Override
        public void onNoNetwork() {
            MyLog.d("onNoNetwork");
            isScrolling[currIndex] = false;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MyLog.d("onNoNetwork.runOnUiThread");

                    mListView[mColumn].stopLoadMore();

                    if (mPostListAdapters[mColumn].getPostList().size() == 0) {
                        setLoadingMode(MODE_RELOADING);
                    } else {
                        setLoadingMode(MODE_OK);
                    }
                    ToastUtils.toast(mContext, R.string.no_network);
                }
            });
        }

        @Override
        public void onGetPostListSucc(final List<PostInfo> postInfoList, final int filter) {

        }

        @Override
        public void onGetWoPostListSucc(final List<PostInfo> postInfoList, final WowoInfo woinfo, final int filter) {
            MyLog.i("onGetPostListSucc: filter=" + filter);
            isScrolling[currIndex] = false;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setLoadingMode(MODE_OK);
                    mListView[mColumn].stopLoadMore();

                    mWoInfo = woinfo;
                    if (mWoInfo != null) {
                        updateListHeader();
                    }

                    if ((postInfoList == null || postInfoList.size() < POSTS_PER_PAGE)) {
                        //认为是末页，不让再获取数据了
                        mListView[filter].setLoadMoreOver();

                        //当前数据大于一页才弹出全部加载完毕的toast
                        if (mPostListAdapters[filter].getPostList().size() > POSTS_PER_PAGE) {
                            ToastUtils.toast(mContext, R.string.list_end);
                        }
                    }

                    if (postInfoList != null) {
                        if (postInfoList.size() == 0 && mPostListAdapters[filter].getPostList().size() == 0) {
                            onErr(null);
                        } else {
                            updatePostList(filter, postInfoList);
                        }
                    }
                }
            });
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
    }

    @Override
    protected void leftBtnHandle() {
        if(mFrom == 1) {
            setResult(RESULT_OK, new Intent());
        }
        super.leftBtnHandle();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            leftBtnHandle();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregist(this);
    }

    public void loadMoreData() {
        //如果是自动加载,可以在这里放置异步加载数据的代码
        isScrolling[currIndex] = true;
        getPostList(currIndex);
    }

    private String getLastPostid(int column) {
        String lastid = null;
        int size = mPostListAdapters[column].getPostList().size();
        if (size == 0) {
            return null;
        }

        if (mRefreshFlag[column] == REFRESH_FROM_PULL_DOWN || mRefreshFlag[column] == REFRESH_FROM_NEW_INTENT) {
            //如果是下拉刷新，要重新获最新post
            lastid = null;
        } else {
            lastid = mPostListAdapters[column].getPostList().get(size - 1).getPostid();
        }
        MyLog.i("getLastIpid=" + lastid);
        return lastid;
    }

    public void onEvent(LoginSuccJump.LoginSuccessEvent event) {
        MyLog.i("onEvent:LoginSuccessEvent");

        //相当于重新获取数据，效果和下拉刷新一样
        mRefreshFlag[0] = REFRESH_FROM_PULL_DOWN;
        getPostList(0);
    }

    public void onEvent(LoginSuccJump.JumpWoPostNewPostEvent event) {
        long current = System.currentTimeMillis();
        long last = WowoPreference.getInstance().getLastLoginEventTime();
        WowoPreference.getInstance().setLastLoginEventTime(current);
        MyLog.i("onEvent:JumpWoPostNewPostEvent, current=" + current + "last=" + last);
        if (current - last > 1 * 1000) {
            gotoNewPost();
        }
    }

    public void onEvent(WowoRulesActivity.AttentionEvent event) {
        if (event != null && mWoInfo.getId() == event.getWo().getId()) {
            boolean ret = event.getStatus() == 1 ? true:false;
            MyLog.i("WoPostListActivity received onEvent:AttentionEvent!! ret="+ret);
            mWoInfo.setIsFavorite(ret);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateListHeader();
                }
            });
        }
    }
}