package com.moinapp.wuliao.modules.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.ui.BaseFragmentActivity;
import com.moinapp.wuliao.utils.AnimationUtil;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.DisplayUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujiancheng on 15/7/1.
 */
public class MyEmojiActivity extends BaseFragmentActivity implements View.OnClickListener {
    private static final ILogger MyLog = LoggerFactory.getLogger("MyEmojiActivity");
    private static final String COLUMN = "column";
    private static final int COLUMN_COUNT = 2; //2个栏目
    private static final int CURSOR_ANIMATION_MILLISECONDS = 300;
    private Context mContext;

    //
    private ViewPager viewPager;
    private TextView tvColumnCosplay, tvColumnEmoji;
    private ImageView ivCursor;
    private int screenWidth;
    private int cursorWidth;
    private int cursorLastX;
    private int cursorPadingPx;
    private RelativeLayout.LayoutParams lpCursor;
    // 数据
    private int currIndex = 0;// 主题列表ViewPager当前页卡编号
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();

    private ImageLoader mImageLoader;
    private TextView mEditCosplayEmj, mEditMyEmj;//编辑的文本view
    private ImageView mBack;//回退的图片
    private int mMode[] = {MineConstants.MODE_NONE, MineConstants.MODE_NONE};
    FragmentMyCosplay fragmentMyCosplay;
    FragmentMyEmoji fragmentMyEmoji;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyLog.i("WoPostListActivity........... onCreate");
        super.onCreate(savedInstanceState);
        mContext = MyEmojiActivity.this;

        //初始化图片缓存
        mImageLoader = ImageLoader.getInstance();
        if(!mImageLoader.isInited()) {
            mImageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }

        cursorPadingPx = DisplayUtil.dip2px(mContext, 6);
        setContentView(R.layout.my_emoji);
        findViews();
    }

    private void findViews() {
        // 初始化栏目指示器，因为有2个栏目和左右各一个图标，所以简单计算滑动条的宽度为屏幕宽度／2
//        ivCursor = (ImageView) findViewById(R.id.iv_cursor);
//        screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;// 获取分辨率宽度
//        cursorWidth = (int) (screenWidth / 2 + 0.5f);
//        cursorLastX = 0;//初始化时滑动条位置在第一个栏目（back图标右边）
//        lpCursor = new RelativeLayout.LayoutParams(cursorWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
//        ivCursor.setPadding(5 * cursorPadingPx, 0, cursorPadingPx, 0);
//        ivCursor.setLayoutParams(lpCursor);
//        // 初始化栏目
//        tvColumnCosplay = (TextView) findViewById(R.id.cosplay_emoji);
//        tvColumnCosplay.setTextColor(getResources().getColor(R.color.common_text_main));
//        tvColumnEmoji = (TextView) findViewById(R.id.resource_emoji);
        mEditCosplayEmj = (TextView) findViewById(R.id.id_edit_cosplay_emj);
        mEditMyEmj = (TextView) findViewById(R.id.id_edit_my_emj);
        mBack = (ImageView) findViewById(R.id.back);

//        fragmentMyCosplay= new FragmentMyCosplay();
        fragmentMyEmoji = new FragmentMyEmoji();
//        mFragmentList.add(fragmentMyCosplay);
        mFragmentList.add(fragmentMyEmoji);

        // 初始化栏目列表
        viewPager = (ViewPager) findViewById(R.id.vp_list);
        FragmentManager fm = getSupportFragmentManager();
        viewPager.setAdapter(new MyFragmentPagerAdapter(fm));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new EmojiOnPageChangeListener());

//        tvColumnCosplay.setOnClickListener(this);
//        tvColumnEmoji.setOnClickListener(this);
        mEditCosplayEmj.setOnClickListener(this);
        mEditMyEmj.setOnClickListener(this);
        mBack.setOnClickListener(this);
        //初始化指示器指向column
//        setIndicator(0);
    }

    private void setIndicator(int position) {
        currIndex = position;

        tvColumnCosplay.setTextColor(getResources().getColor(R.color.common_title_grey));
        tvColumnEmoji.setTextColor(getResources().getColor(R.color.common_title_grey));
        mEditCosplayEmj.setVisibility(currIndex == 0 ? View.VISIBLE : View.GONE);
        mEditMyEmj.setVisibility(currIndex == 1 ? View.VISIBLE : View.GONE);

        switch (currIndex) {
            case 0:
                tvColumnCosplay.setTextColor(getResources().getColor(R.color.common_text_main));
                AnimationUtil.moveTo(ivCursor, CURSOR_ANIMATION_MILLISECONDS, cursorLastX, 0, 0, 0);
                ivCursor.setPadding(0, 0, 0, 0);
                cursorLastX = 0;
                break;
            case 1:
                tvColumnEmoji.setTextColor(getResources().getColor(R.color.common_text_main));
                AnimationUtil.moveTo(ivCursor, CURSOR_ANIMATION_MILLISECONDS, cursorLastX, cursorWidth, 0, 0);
                ivCursor.setPadding(0, 0, 0, 0);
                cursorLastX = cursorWidth;
                break;
            default:
                break;
        }
    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return mFragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

    public class EmojiOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            MyLog.i("onPageSelected");
//            setIndicator(position);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
//        if (id == R.id.cosplay_emoji) {
//            viewPager.setCurrentItem(0);
//        } else if (id == R.id.resource_emoji) {
//            viewPager.setCurrentItem(1);
//        } else if (id == R.id.id_edit_cosplay_emj || id == R.id.id_edit_my_emj) {
//            int index = currIndex;
//            if (mMode[index] == MineConstants.MODE_NONE) {
//                mMode[index] = MineConstants.MODE_EDIT;
//                if(index == 0) {
//                    mEditCosplayEmj.setText(getString(android.R.string.cancel));
//                    fragmentMyCosplay.setMode(mMode[index]);
//                } else if(index == 1) {
//                    mEditMyEmj.setText(getString(android.R.string.cancel));
//                    fragmentMyEmoji.setMode(mMode[index]);
//                }
//
//            } else if (mMode[index] == MineConstants.MODE_EDIT) {
//                mMode[index] = MineConstants.MODE_NONE;
//                if(index == 0) {
//                    mEditCosplayEmj.setText(getString(R.string.edit));
//                    fragmentMyCosplay.setMode(mMode[index]);
//                } else if(index == 1) {
//                    mEditMyEmj.setText(getString(R.string.edit));
//                    fragmentMyEmoji.setMode(mMode[index]);
//                }
//            }
//        } else if (id == R.id.back) {
//            finish();
//        }
         if (id == R.id.id_edit_cosplay_emj || id == R.id.id_edit_my_emj) {
            int index = currIndex;
            if (mMode[index] == MineConstants.MODE_NONE) {
                mMode[index] = MineConstants.MODE_EDIT;
                mEditMyEmj.setText(getString(android.R.string.cancel));
                fragmentMyEmoji.setMode(mMode[index]);
            } else if (mMode[index] == MineConstants.MODE_EDIT) {
                mMode[index] = MineConstants.MODE_NONE;
                mEditMyEmj.setText(getString(R.string.edit));
                fragmentMyEmoji.setMode(mMode[index]);
            }
        } else if (id == R.id.back) {
            finish();
        }
    }
}
