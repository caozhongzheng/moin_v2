package com.moinapp.wuliao.commons.banner;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.ipresource.model.BannerInfo;
import com.moinapp.wuliao.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class BannerUI implements ViewPager.OnPageChangeListener {
    private static final ILogger MyLog = LoggerFactory.getLogger("");

    private Context context;
    private View mParentView;
    private View mBannerView;
    private View mLoadingView;
    private View mFailedView;
    private ViewPager mViewPager;
    private LinearLayout mDotsIndicator;

    private PagerAdapter mAdapter;
    private List<BannerInfo> mBanners;
    private int mLastPosition = 0;// 记录上一次位置
    private int mSavedPosition = 0;// 记录被隐藏时Banner的位置
    private static final int SLIDE_INTERVAL = 5 * 1000;
    private Handler mHandler;
    private Runnable mAutoSlideRunnable;
    private boolean isAutoSlideEnabled;
    private boolean isAutoSliding;

    private int mNormalDot;
    private int mSelectedDot;

// ===========================================================
// Constructors
// ===========================================================

    public BannerUI(View view, Handler handler) {
        mParentView = view;
        mHandler = handler;
        context = mParentView.getContext();

        initView();
        initData();
    }

    // ===========================================================
// Getter & Setter
// ===========================================================
    public Context getContext() {
        return context;
    }

    public List<BannerInfo> getBanners() {
        return mBanners;
    }

    public void setBanners(List<BannerInfo> mBanners) {
        this.mBanners = mBanners;
    }

    public PagerAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(PagerAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public int getNormalDot() {
        return mNormalDot;
    }

    public void setNormalDot(int mNormalDot) {
        this.mNormalDot = mNormalDot;
    }

    public int getSelectedDot() {
        return mSelectedDot;
    }

    public void setSelectedDot(int mSelectedDot) {
        this.mSelectedDot = mSelectedDot;
    }

// ===========================================================
// Methods
// ===========================================================

    private void initView() {
        mBannerView = mParentView.findViewById(R.id.rl_banner);
        mLoadingView = (RelativeLayout) mParentView.findViewById(R.id.ll_banner_loading);
        mFailedView = (LinearLayout) mParentView.findViewById(R.id.ll_banner_error);
        mDotsIndicator = (LinearLayout) mParentView.findViewById(R.id.ll_banner_point);
        mViewPager = (ViewPager) mParentView.findViewById(R.id.vp_banner);
        mViewPager.setOnPageChangeListener(this);

        mFailedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingView.setVisibility(View.VISIBLE);
                mFailedView.setVisibility(View.GONE);
                mBannerView.setVisibility(View.GONE);
                loadBanners();
            }
        });
    }

    private void initData() {
        mBanners = new ArrayList<BannerInfo>();

        setNormalDot(R.drawable.banner_point);
        setSelectedDot(R.drawable.banner_point_on);

        mAutoSlideRunnable = new Runnable() {
            @Override
            public void run() {
                int position = mViewPager.getCurrentItem();
                int count = mAdapter.getCount();
                position = position + 1;
                if (position >= count) {
                    position = 0;
                }
                mViewPager.setCurrentItem(position, true);
                mHandler.postDelayed(mAutoSlideRunnable,
                        SLIDE_INTERVAL);
                isAutoSliding = true;
            }
        };
    }

    /**
     * 初始化banner框架
     */
    public abstract void loadBanners();
//	{
//		MyLog.d("loadBanners mPlate:" + mPlate);

//		BannerManager bannerManager = BannerManager.getInstance(mParentView.getContext());
//		List<Banner> banners = bannerManager.getBannerListFromCache(mPlate);
//		MyLog.d("loadBanners isCacheExpired:" + bannerManager.isCacheExpired(mPlate) + " banners:" + banners);
//		if (banners != null && banners.size() > 0) {
//			displayBanners(banners);
//		}
//		if (bannerManager.isCacheExpired(mPlate) || banners == null || banners.size() == 0) {
//			mLoadingView.setVisibility(View.VISIBLE);
//			mFailedView.setVisibility(View.GONE);
//			mBannerView.setVisibility(View.GONE);
//			bannerManager.getBannerList(mPlate, this);
//		}
//	}

    public void enableAutoSlide() {
        isAutoSlideEnabled = true;
    }

    public void startAutoSlide() {
        if (isAutoSlideEnabled && !isAutoSliding && mHandler != null && mBanners.size() > 1) {
            isAutoSliding = true;
            mHandler.removeCallbacks(mAutoSlideRunnable);
            mHandler.postDelayed(mAutoSlideRunnable, SLIDE_INTERVAL);
        }
    }

    public void disableAutoSlide() {
        isAutoSlideEnabled = false;
        if (isAutoSliding) {
            stopAutoSlide();
        }
    }

    public void stopAutoSlide() {
        if (mHandler != null) {
            isAutoSliding = false;
            mHandler.removeCallbacks(mAutoSlideRunnable);
        }
    }

    /**
     * 销毁
     */
    public void recycle() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mAutoSlideRunnable);
        }
        mParentView = null;
        mBannerView = null;
        mLoadingView = null;
        if (mFailedView != null) {
            mFailedView.setOnClickListener(null);
            mFailedView = null;
        }
        mDotsIndicator = null;
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
            mViewPager.setAdapter(null);
            mViewPager = null;
        }
        mViewPager = null;
        if (mBanners != null) {
            mBanners.clear();
            mBanners = null;
        }
        if (mAdapter != null) {
            mAdapter = null;
        }
    }

    public void onHide() {
        mViewPager.setAdapter(null);
        stopAutoSlide();
    }

    public void onShow() {
        if (mAdapter == null) {
            loadBanners();
        } else {
            mViewPager.setAdapter(mAdapter);
            mViewPager.setCurrentItem(mSavedPosition);
            startAutoSlide();
        }
    }

    public void onRefreshing() {
        mLoadingView.setVisibility(View.VISIBLE);
        mFailedView.setVisibility(View.GONE);
        if (mBanners != null && mBanners.size() > 0) {
            mBannerView.setVisibility(View.VISIBLE);
            mLoadingView.setBackgroundResource(0);
        } else {
            mBannerView.setVisibility(View.GONE);
            mLoadingView.setBackgroundResource(R.drawable.banner_empty);
        }
    }

    public void displayBanners(final List<BannerInfo> banners) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                runnableDisplayBanners(banners);
            }
        });
    }

    public void runnableDisplayBanners(List<BannerInfo> banners) {
        mLoadingView.setVisibility(View.GONE);
        mFailedView.setVisibility(View.GONE);
        mBannerView.setVisibility(View.VISIBLE);
        if (banners == null || banners.size() <= 0) {
            return;
        }

        mBanners = banners;

        MyLog.d("displayBanners");
        Context context = mParentView.getContext();
        // 初始化圆点指示器
        mDotsIndicator.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = DisplayUtil.dip2px(context, 3);
        params.setMargins(margin, 0, margin, 0);
        int dotLayoutId = R.layout.banner_viewpager_dots;
        for (int i = 0; i < mBanners.size(); i++) {
            ImageView ivDot = (ImageView) LayoutInflater.from(context).inflate(
                    dotLayoutId, null);
            ivDot.setLayoutParams(params);
            mDotsIndicator.addView(ivDot);
        }
        mDotsIndicator.getChildAt(0).setBackgroundResource(mSelectedDot);
        if (mBanners.size() <= 1) {
            mDotsIndicator.getChildAt(0).setVisibility(View.GONE);
        } else {
            mDotsIndicator.getChildAt(0).setVisibility(View.VISIBLE);
        }

        // 初始化ViewPager
//		mAdapter = new BannerAdapter(context, mBanners);
        mViewPager.setAdapter(mAdapter);
        mLastPosition = 0;
        mViewPager.setCurrentItem(mBanners.size() * 10000);

        if (isAutoSlideEnabled && !isAutoSliding) {
            startAutoSlide();
        }

        // 初始化时发送行为日志
        if (mBanners.size() > 0) {
//            StatManager.getInstance().onAction(StatManager.TYPE_STORE_ACTION,
//					BannerActionConstants.ACTION_LOG_1401,
//					mBanners.get(mLastPosition).getStrId(), 0, mPlate + "_0");
        }
    }
// ===========================================================
// Methods for/from SuperClass/Interfaces
// ===========================================================

    @Override
    public void onPageSelected(int position) {
        mSavedPosition = position;
        int newPosition = position % mBanners.size();
        mDotsIndicator.getChildAt(mLastPosition).setBackgroundResource(mNormalDot);
        mDotsIndicator.getChildAt(newPosition).setBackgroundResource(mSelectedDot);
        mLastPosition = newPosition;
        if (isAutoSlideEnabled && !isAutoSliding) {
            startAutoSlide();
        }

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_DRAGGING
                && isAutoSlideEnabled && isAutoSliding) {
            stopAutoSlide();
        } else if (isAutoSlideEnabled && !isAutoSliding) {
            startAutoSlide();
        }
    }

    public void runOnGetFailed() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mLoadingView.setVisibility(View.GONE);
                mFailedView.setVisibility(View.VISIBLE);
                mBannerView.setVisibility(View.GONE);
            }
        });
    }

    public void runOnGetSuccess(final List<BannerInfo> banners) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                MyLog.d("onGetBannerListSucc: banners.size:" + banners.size());
                displayBanners(banners);
            }
        });
    }

}
