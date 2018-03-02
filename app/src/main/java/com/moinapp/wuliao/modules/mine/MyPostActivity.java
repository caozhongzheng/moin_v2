package com.moinapp.wuliao.modules.mine;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.modules.mine.listener.MyPostListener;
import com.moinapp.wuliao.modules.mine.listener.MyReplyListener;
import com.moinapp.wuliao.modules.mine.model.PostId;
import com.moinapp.wuliao.modules.wowo.model.PostInfo;
import com.moinapp.wuliao.utils.AnimationUtil;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.DisplayUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moying on 15/6/30.
 */
public class MyPostActivity extends BaseActivity implements  View.OnClickListener {
    private ILogger MyLog = LoggerFactory.getLogger(MineModule.MODULE_NAME);
    private static final String COLUMN = "column";
    private static final int COLUMN_COUNT = 2;
    //
    private ViewPager viewPager;
    private TextView tvColumnPost, tvColumnReply, tvEditPost, tvEditReply;
    private ImageView ivCursor;
    private int screenWidth;
    private int cursorWidth;
    private int cursorLastX;
    private int cursorPadingPx;
    private RelativeLayout.LayoutParams lpCursor;
    private ImageView mBack;
    //
    private ListView[] mListView;
    private RelativeLayout[] mBottomLy;
    private Button[] mDeleteBtn;
    private int[] mCount = {0, 0};

    private int mMode[] = {MineConstants.MODE_NONE, MineConstants.MODE_NONE};
    private int currIndex = 0;
    private ImageLoader mImageLoader;
    private MyPostAdapter mPostListAdapter;
    private MyReplyAdapter mReplyListAdapter;
    /**
     * 所有的帖子/回复
     */
    private List<PostInfo>[] mPosts;
    /**
     * 已选择的帖子/回复
     */
    private ArrayList<PostInfo>[] mSelectPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageLoader = ImageLoader.getInstance();
        if(!mImageLoader.isInited()) {
            mImageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }

        cursorPadingPx = DisplayUtil.dip2px(MyPostActivity.this, 6);
        setContentView(R.layout.my_post);

        if(getIntent() != null) {
            currIndex = getIntent().getIntExtra(COLUMN, 0);
        }
        initLoadingView();
        setLoadingMode(MODE_LOADING);
        findViews(currIndex);
    }

    private void findViews(int column) {
        // 初始化栏目指示器
        ivCursor = (ImageView) findViewById(R.id.iv_cursor);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        cursorWidth = (int) (screenWidth / 2 + 0.5f);
        cursorLastX = 0;
        lpCursor = new RelativeLayout.LayoutParams(cursorWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        ivCursor.setPadding(5 * cursorPadingPx, 0, cursorPadingPx, 0);
        ivCursor.setLayoutParams(lpCursor);
        // 初始化栏目
        tvColumnPost = (TextView) findViewById(R.id.id_my_posts);
        tvColumnPost.setTextColor(getResources().getColor(R.color.common_text_main));
        tvColumnReply = (TextView) findViewById(R.id.id_my_replys);
        tvEditPost = (TextView) findViewById(R.id.id_edit_post);
        tvEditReply = (TextView) findViewById(R.id.id_edit_reply);
        mBack = (ImageView) findViewById(R.id.back);
        // 初始化栏目列表
        viewPager = (ViewPager) findViewById(R.id.vp_list);
        viewPager.setAdapter(new MyPostPagerAdapter());
        viewPager.setCurrentItem(column);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        //初始化指示器指向column
        setIndicator(column);

        mPosts = new ArrayList[COLUMN_COUNT];
        mSelectPosts = new ArrayList[COLUMN_COUNT];
        for(int i=0; i<COLUMN_COUNT; i++){
            mPosts[i] = new ArrayList<>();
            mSelectPosts[i] = new ArrayList<>();
        }
        mPostListAdapter = new MyPostAdapter(getApplicationContext(), mPosts[0], 0);
        mReplyListAdapter = new MyReplyAdapter(getApplicationContext(), mPosts[1], 1);

        mListView = new ListView[COLUMN_COUNT];
        mBottomLy = new RelativeLayout[COLUMN_COUNT];
        mDeleteBtn = new Button[COLUMN_COUNT];

        tvColumnPost.setOnClickListener(this);
        tvColumnReply.setOnClickListener(this);
        tvEditPost.setOnClickListener(this);
        tvEditReply.setOnClickListener(this);
        mBack.setOnClickListener(this);
    }

    private class MyPostPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return COLUMN_COUNT;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            MyLog.i("........... instantiateItem...position="+position);
            final LinearLayout postListLayout = (LinearLayout) LayoutInflater.from(MyPostActivity.this).inflate(R.layout.my_posts_list, null);

            final ListView listView = (ListView) postListLayout.findViewById(R.id.id_myposts_lv);
            mListView[position] = listView;

            final RelativeLayout bootom = (RelativeLayout) postListLayout.findViewById(R.id.id_bottom_ly);
            mBottomLy[position] = bootom;

            final Button deleteBtn = (Button) postListLayout.findViewById(R.id.id_delete);
            mDeleteBtn[position] = deleteBtn;
            mDeleteBtn[position].setOnClickListener(deleteListener);

//            listView.setAdapter(mPostListAdapter[position]);

            container.addView(postListLayout, 0);

            if (position == 0) {
                MyLog.i("getData》getPostList");
                getPostList();
            } else if (position == 1) {
                MyLog.i("getData》getReplyList");
                getReplyList();
            }
            return postListLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            MyLog.i("onPageSelected " + position);
            setIndicator(position);
            if(position == 0) {
                if(mPostListAdapter.getPostList().size() == 0) {
                    MyLog.i("offset=0, PostListView为空，需要重新加载" + position);
                    getPostList();
                }
            } else if(position == 1) {
                if(mReplyListAdapter.getPostList().size() == 0) {
                    MyLog.i("offset=0, ReplyListView为空，需要重新加载" + position);
                    getReplyList();
                }
            }
        }
    }

    private View.OnClickListener deleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (currIndex == 0) {
                if (MyPostAdapter.getSelectedPost().size() > 0) {
                    List<PostId> postIDs = new ArrayList<>();
                    for (int i = 0; i < MyPostAdapter.getSelectedPost().size(); i++) {
                        postIDs.add(new PostId(MyPostAdapter.getSelectedPost().get(i).getWoid(),
                                MyPostAdapter.getSelectedPost().get(i).getPostid()));
                        MyLog.i(i + " deleted: " + MyPostAdapter.getSelectedPost().get(i).getTitle());
                    }
                    MineManager.getInstance().delMyPost(postIDs, new MyPostListener() {
                        @Override
                        public void getMyPostSucc(List<PostInfo> postInfoList) {

                        }

                        @Override
                        public void delMyPostSucc(int result) {
                            // 通知handler 删除成功
                            mHandler.sendEmptyMessage(0x130);
                        }

                        @Override
                        public void onNoNetwork() {
                            mHandler.sendEmptyMessage(0x119);
                        }

                        @Override
                        public void onErr(Object object) {
                            mHandler.sendEmptyMessage(0x120);
                        }
                    });
                }

            } else if (currIndex == 1) {
                if (MyReplyAdapter.getSelectedPost().size() > 0) {
                    List<PostId> postIDs = new ArrayList<>();
                    for (int i = 0; i < MyReplyAdapter.getSelectedPost().size(); i++) {
                        postIDs.add(new PostId(MyReplyAdapter.getSelectedPost().get(i).getWoid(),
                                MyReplyAdapter.getSelectedPost().get(i).getPostid()));
                        MyLog.i(i + " deleted: " + MyReplyAdapter.getSelectedPost().get(i).getTitle());
                    }
                    MineManager.getInstance().delMyReply(postIDs, new MyReplyListener() {
                        @Override
                        public void getMyCommentSucc(List<PostInfo> postInfoList) {

                        }

                        @Override
                        public void delMyCommentSucc(int result) {
                            // 通知handler 删除成功
                            mHandler.sendEmptyMessage(0x130);
                        }

                        @Override
                        public void onNoNetwork() {
                            mHandler.sendEmptyMessage(0x119);
                        }

                        @Override
                        public void onErr(Object object) {
                            mHandler.sendEmptyMessage(0x120);
                        }
                    });
                }
            }
        }
    };

    private void getData() {
        if(currIndex == 0) {
            MyLog.i("getData》getPostList");
            getPostList();
        } else if(currIndex == 1) {
            getReplyList();
        }
    }

    @Override
    protected void reloadHandle() {
        super.reloadHandle();
        currIndex = 0;
        getPostList();
    }

    private void getPostList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MineManager.getInstance().getMyPost(new MyPostListener() {
                    @Override
                    public void getMyPostSucc(List<PostInfo> postInfoList) {
                        int position = 0;
                        mPosts[position] = postInfoList;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setLoadingMode(MODE_OK);
                            }
                        });
                        // 通知Handler获取帖子完成
                        mHandler.sendEmptyMessage(0x110);

                        if (postInfoList == null || postInfoList.size() == 0) {
                            MyLog.i("getPostList succ: postInfoList is null ? " + (postInfoList == null));
                        } else {
                            MyLog.i("getPostList succ: postInfoList.size=" + postInfoList.size());
                            for (int i = 0; i < mPosts[position].size(); i++) {
                                MyLog.i("第" + (i + 1) + "个:woid=" + mPosts[position].get(i).getWoid()
                                        + ", postid=" + mPosts[position].get(i).getPostid()
                                        + ", status=" + mPosts[position].get(i).getStatus()
                                        + ", woname=" + mPosts[position].get(i).getWoname()
                                        + ", title=" + mPosts[position].get(i).getTitle()
                                        + ", content=" + mPosts[position].get(i).getContent());

                            }

                        }

                    }

                    @Override
                    public void delMyPostSucc(int result) {

                    }

                    @Override
                    public void onNoNetwork() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setLoadingMode(MODE_RELOADING);
                            }
                        });
                        mHandler.sendEmptyMessage(0x119);
                    }

                    @Override
                    public void onErr(Object object) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setLoadingMode(MODE_RELOADING);
                            }
                        });
                        mHandler.sendEmptyMessage(0x120);
                    }
                });
            }
        }).start();
    }

    private void getReplyList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                MineManager.getInstance().getMyReply(new MyReplyListener() {
                    @Override
                    public void getMyCommentSucc(List<PostInfo> postInfoList) {
                        int position = 1;
                        mPosts[position] = postInfoList;
                        // 通知Handler获取回复完成
                        mHandler.sendEmptyMessage(0x111);

                        if(postInfoList == null || postInfoList.size() == 0) {
                            MyLog.i("getMyCommentSucc succ: postInfoList is null ? " + (postInfoList == null));
                        } else {
                            MyLog.i("getMyCommentSucc succ: postInfoList.size=" + postInfoList.size());
                            for (int i = 0; i < mPosts[position].size(); i++) {
                                MyLog.i("第"+(i+1)+"个:woid="+mPosts[position].get(i).getWoid()
                                        +", postid="+mPosts[position].get(i).getPostid()
                                        +", status="+mPosts[position].get(i).getStatus()
                                        +", woname="+mPosts[position].get(i).getWoname()
                                        +", title="+mPosts[position].get(i).getTitle()
                                        +", content="+mPosts[position].get(i).getContent());
                                if(mPosts[position].get(i).getAuthor() != null) {
                                    MyLog.i("第" + (i + 1) + "个:nickname=" + mPosts[position].get(i).getAuthor().getNickname()
                                            + ", id=" + mPosts[position].get(i).getAuthor().getId()
                                            + ", level=" + mPosts[position].get(i).getAuthor().getLevel()
                                            + ", avatar=" + mPosts[position].get(i).getAuthor().getAvatar().getUri());
                                }
                            }

                        }
                    }

                    @Override
                    public void delMyCommentSucc(int result) {

                    }

                    @Override
                    public void onNoNetwork() {
                        mHandler.sendEmptyMessage(0x119);
                    }

                    @Override
                    public void onErr(Object object) {
                        mHandler.sendEmptyMessage(0x120);
                    }
                });
            }
        }).start();
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x119:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.toast(MyPostActivity.this, R.string.no_network);
                        }
                    });

                    break;
                case 0x120:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.toast(MyPostActivity.this, R.string.connection_failed);
                        }
                    });
                    break;
                case 0x110:
                    // 为View绑定数据
                    data2View(0);
                    break;
                case 0x111:
                    // 为View绑定数据
                    data2View(1);
                    break;
                case 0x130:
                    if(currIndex == 0) {
                        for (int i = 0; i < MyPostAdapter.getSelectedPost().size(); i++) {
                            mPosts[currIndex].remove(MyPostAdapter.getSelectedPost().get(i));
                        }
                    } else if (currIndex == 1) {
                        for (int i = 0; i < MyReplyAdapter.getSelectedPost().size(); i++) {
                            mPosts[currIndex].remove(MyReplyAdapter.getSelectedPost().get(i));
                        }
                    }
                    udpateMode(currIndex);
                    break;
                default:
                    break;
            }
        }
    };

    private void udpateMode(int index) {
        if (mPosts[index] == null || mPosts[index].size() == 0 || mSelectPosts[index] == null || mSelectPosts[index].size() == 0) {
            MyLog.i((index==0?"发帖":"评论") + " 的个数=0 ");
//                return;
        }
        if (mMode[index] == MineConstants.MODE_NONE) {
            mMode[index] = MineConstants.MODE_EDIT;
            if(index == 0) {
                tvEditPost.setText(getString(android.R.string.cancel));
            } else if(index == 1) {
                tvEditReply.setText(getString(android.R.string.cancel));
            }
            mBottomLy[index].setVisibility(View.VISIBLE);
            mDeleteBtn[index].setText(getString(R.string.delete));
            mDeleteBtn[index].setClickable(false);
            mDeleteBtn[index].setBackground(getResources().getDrawable(R.drawable.tag_btn_solid_grey_bg));
        } else if (mMode[index] == MineConstants.MODE_EDIT) {
            mMode[index] = MineConstants.MODE_NONE;
            if(index == 0) {
                tvEditPost.setText(getString(R.string.edit));
            } else if(index == 1) {
                tvEditReply.setText(getString(R.string.edit));
            }
            mBottomLy[index].setVisibility(View.GONE);
        }
        if(index == 0) {
            mSelectPosts[index].clear();
            MyPostAdapter.setMode(mMode[index]);
            MyPostAdapter.setSelectedPost(mSelectPosts[index]);
            mPostListAdapter.notifyDataSetChanged();
        } else if(index == 1) {
            mSelectPosts[index].clear();
            MyReplyAdapter.setMode(mMode[index]);
            MyReplyAdapter.setSelectedPost(mSelectPosts[index]);
            mReplyListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 为View绑定数据
     */
    private void data2View(int index) {
        if (mPosts[index] == null || mPosts[index].size() == 0) {
            if(index == 0 && this.currIndex == 0) {
                ToastUtils.toast(getApplicationContext(), R.string.no_mypost);
                MyLog.i(getString(R.string.no_mypost));
            } else if(index == 1 && this.currIndex == 1) {
                ToastUtils.toast(getApplicationContext(), R.string.no_myreply);
                MyLog.i(getString(R.string.no_myreply));
            }
            return;
        }

        setPostAdapter(index);
    }

    private void setPostAdapter(final int index) {
        if(index == 0) {
            mPostListAdapter = new MyPostAdapter(getApplicationContext(), mPosts[index], index);
            if (mPostListAdapter == null) {
                MyLog.i("currIndex " + index + " adapter is null");
            } else {
                MyLog.i("currIndex " + index + " adapter size is " + mPostListAdapter.getPostList().size());
            }

            MyPostAdapter.setSelectedPost(mSelectPosts[index]);
            MyPostAdapter.setMode(mMode[index]);
            mPostListAdapter.setTextCallback(new MyPostAdapter.TextCallback() {
                @Override
                public void onListen(int count) {
                    if(count > 0) {
                        mDeleteBtn[0].setText(getString(R.string.delete) + " (" + count + ")");
                        mDeleteBtn[0].setClickable(true);
                        mDeleteBtn[0].setBackground(getResources().getDrawable(R.drawable.tag_btn_solid_bg));
                    } else {
                        mDeleteBtn[0].setText(getString(R.string.delete));
                        mDeleteBtn[0].setClickable(false);
                        mDeleteBtn[0].setBackground(getResources().getDrawable(R.drawable.tag_btn_solid_grey_bg));
                    }
                }
            });
            mListView[index].setAdapter(mPostListAdapter);
        } else if(index == 1) {
            mReplyListAdapter = new MyReplyAdapter(getApplicationContext(), mPosts[index], index);
            if (mReplyListAdapter == null) {
                MyLog.i("currIndex " + index + " adapter is null");
            } else {
                MyLog.i("currIndex " + index + " adapter size is " + mReplyListAdapter.getPostList().size());
            }

            MyReplyAdapter.setSelectedPost(mSelectPosts[index]);
            MyReplyAdapter.setMode(mMode[index]);
            mReplyListAdapter.setTextCallback(new MyReplyAdapter.TextCallback() {
                @Override
                public void onListen(int count) {
                    if (count > 0) {
                        mDeleteBtn[1].setText(getString(R.string.delete) + " (" + count + ")");
                        mDeleteBtn[1].setClickable(true);
                        mDeleteBtn[1].setBackground(getResources().getDrawable(R.drawable.tag_btn_solid_bg));
                    } else {
                        mDeleteBtn[1].setText(getString(R.string.delete));
                        mDeleteBtn[1].setClickable(false);
                        mDeleteBtn[1].setBackground(getResources().getDrawable(R.drawable.tag_btn_solid_grey_bg));
                    }
                }
            });
            mListView[index].setAdapter(mReplyListAdapter);
        }

    }

    private void setIndicator(int position) {
        currIndex = position;

        tvColumnPost.setTextColor(getResources().getColor(R.color.common_title_grey));
        tvColumnReply.setTextColor(getResources().getColor(R.color.common_title_grey));
        tvEditPost.setVisibility(currIndex == 0 ? View.VISIBLE : View.GONE);
        tvEditReply.setVisibility(currIndex ==1 ? View.VISIBLE : View.GONE);

        switch (currIndex) {
            case 0:
                tvColumnPost.setTextColor(getResources().getColor(R.color.common_text_main));
                AnimationUtil.moveTo(ivCursor, 300, cursorLastX, 0, 0, 0);
                ivCursor.setPadding(0, 0, 0, 0);
                cursorLastX = 0;
                break;
            case 1:
                tvColumnReply.setTextColor(getResources().getColor(R.color.common_text_main));
                AnimationUtil.moveTo(ivCursor, 300, cursorLastX, cursorWidth, 0, 0);
                ivCursor.setPadding(0, 0, 0, 0);
                cursorLastX = cursorWidth;
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.id_my_posts) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.id_my_replys) {
            viewPager.setCurrentItem(1);
        } else if (id == R.id.id_edit_post || id == R.id.id_edit_reply) {
            if (mPosts[currIndex] == null || mPosts[currIndex].size() == 0) {
                MyLog.i((currIndex==0?"发帖":"评论") + " 的个数=0 ");
                return;
            }
            udpateMode(currIndex);
        } else if (id == R.id.back) {
            finish();
        } else if (id == R.id.iv_nonetwork) {
            getData();
        } else if (id == R.id.iv_empty) {
            getData();
        }
    }

}
