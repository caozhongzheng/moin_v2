package com.moinapp.wuliao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.moinapp.wuliao.utils.DisplayUtil;

/**
 * Created by moying on 15/8/4.
 */
public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener {
    /** Called when the activity is first created. */
    private ViewPager viewPager;
    private LinearLayout mDotsIndicator;

    private PicAdapter mAdapter;
    private Button enter_moin, enter_moin_2;
    private int mLastPosition = 0;

    private int mNormalDot;
    private int mSelectedDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide_layout);

        // 初始化圆点指示器
        mDotsIndicator = (LinearLayout) findViewById(R.id.ll_guide_point);

        setNormalDot(R.drawable.banner_point);
        setSelectedDot(R.drawable.banner_point_on);

        mDotsIndicator.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = DisplayUtil.dip2px(GuideActivity.this, 5);
        params.setMargins(margin, 0, margin, 0);
        int dotLayoutId = R.layout.banner_viewpager_dots;
        for (int i = 0; i < ids.length; i++) {
            ImageView ivDot = (ImageView) LayoutInflater.from(GuideActivity.this).inflate(
                    dotLayoutId, null);
            ivDot.setLayoutParams(params);
            ivDot.setScaleX(1.2f);
            ivDot.setScaleY(1.2f);
            mDotsIndicator.addView(ivDot);
        }
        mDotsIndicator.getChildAt(0).setBackgroundResource(mSelectedDot);
        if (ids.length <= 1) {
            mDotsIndicator.getChildAt(0).setVisibility(View.GONE);
        } else {
            mDotsIndicator.getChildAt(0).setVisibility(View.VISIBLE);
        }

        // 初始化viewPager
        viewPager = (ViewPager) findViewById(R.id.vp_guide);
        viewPager.setAdapter(mAdapter = new PicAdapter());
        viewPager.setOnPageChangeListener(this);

        // 初始化点击按钮
        enter_moin = (Button) findViewById(R.id.enter_moin);
        enter_moin_2 = (Button) findViewById(R.id.enter_moin_2);
        enter_moin_2.setClickable(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_OK);
            gotoMoin();
        }
        return super.onKeyDown(keyCode, event);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x11:
                    showAnim();
                    break;
                case 0x22:
                    hideAnim();
                    break;
            }
        }
    };

    private void showAnim() {

        TranslateAnimation ta = new TranslateAnimation(0, 0, 0, -DisplayUtil.dip2px(GuideActivity.this, 100));
        ta.setDuration(300);
        ta.setFillAfter(true);
        ta.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                enter_moin_2.setClickable(true);
                enter_moin_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gotoMoin();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        enter_moin.startAnimation(ta);

    }


    private void hideAnim() {
        TranslateAnimation ta = new TranslateAnimation(0, 0, -DisplayUtil.dip2px(GuideActivity.this, 100), 0);
        ta.setDuration(300);
        ta.setFillAfter(true);
        enter_moin_2.setClickable(false);
        enter_moin.startAnimation(ta);
    }

    public int[] ids = { R.drawable.welcome1, R.drawable.welcome2 };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int newPosition = position % ids.length;
        mDotsIndicator.getChildAt(mLastPosition).setBackgroundResource(mNormalDot);
        mDotsIndicator.getChildAt(newPosition).setBackgroundResource(mSelectedDot);

        if (newPosition >= (ids.length - 1)) {
            mHandler.sendEmptyMessage(0x11);
        } else if (mLastPosition == (ids.length - 1)) {
            mHandler.sendEmptyMessage(0x22);
        }
        mLastPosition = newPosition;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class PicAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return ids.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (ids.length < position && position < 100){
                return null;
            }

            View imageLayout = LayoutInflater.from(GuideActivity.this).inflate(R.layout.banner_viewpager_item, null);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
            imageView.setImageResource(ids[position % ids.length]);

            ((ViewPager) container).addView(imageLayout, 0);

            return imageLayout;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

    }

    private void gotoMoin() {
        MoinPreference.getInstance().setFirstEnter(false);

        Intent mIntent = new Intent();
        mIntent.setClass(GuideActivity.this, MoinActivity.class);
        startActivity(mIntent);
        finish();
    }

    public void setNormalDot(int mNormalDot) {
        this.mNormalDot = mNormalDot;
    }

    public void setSelectedDot(int mSelectedDot) {
        this.mSelectedDot = mSelectedDot;
    }
}
