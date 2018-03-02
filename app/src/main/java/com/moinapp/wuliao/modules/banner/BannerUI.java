package com.moinapp.wuliao.modules.banner;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BannerUI implements ViewPager.OnPageChangeListener, BannerListListener {
	private static final ILogger MyLog = LoggerFactory.getLogger(BannerModule.MODULE_NAME);
	
	private int mPlate, mColomn = 0;
	private View mParentView;
	private View mBannerView;
	private View mLoadingView;
	private View mFailedView;
	private ViewPager mViewPager;
	private LinearLayout mDotsIndicator;
	private BannerAdapter mAdapter;
	private List<Banner> mBanners;
	private int mLastPosition = 0;// 记录上一次位置
	private int mSavedPosition = 0;// 记录被隐藏时Banner的位置
	private static final int SLIDE_INTERVAL = 5*1000;
	private Handler mHandler;
	private Runnable mAutoSlideRunnable;
	private boolean isAutoSlideEnabled;
	private boolean isAutoSliding;
	private int mNormalDot;
	private int mSelectedDot;
	
	// 行为日志
 	private Map<String, Boolean> mActionLogs;
 	
	public BannerUI(View view, Handler handler, int plate) {
		mParentView = view;
		mHandler = handler;
		mPlate = plate;
		mBanners = new ArrayList<Banner>();
		
		mActionLogs = new ConcurrentHashMap<String, Boolean>();
		
		findViews();
		
		Context context = mParentView.getContext();	
		mNormalDot = R.drawable.banner_point;
		mSelectedDot = R.drawable.banner_point_on;

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

	private void findViews() {
		Context context = mParentView.getContext();	
		mBannerView = mParentView.findViewById(R.id.rl_banner);
		mLoadingView = mParentView.findViewById(R.id.ll_banner_loading);
		mFailedView = mParentView.findViewById(R.id.ll_banner_error);
		mDotsIndicator = (LinearLayout) mParentView
				.findViewById(R.id.ll_banner_point);
		mViewPager = (ViewPager) mParentView.findViewById(R.id.vp_banner);
		mViewPager.setOnPageChangeListener(this);
		
		mFailedView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mLoadingView.setVisibility(View.VISIBLE);
				mFailedView.setVisibility(View.GONE);
				mBannerView.setVisibility(View.VISIBLE);
				loadBanners();
			}
		});
	}
	
	/**
	 * 初始化banner框架
	 */
	public void loadBanners() {
		MyLog.d("loadBanners mPlate:" + mPlate);
		
		BannerManager bannerManager = BannerManager.getInstance(mParentView.getContext());
		List<Banner> banners = bannerManager.getBannerListFromCache(mPlate);
		MyLog.d("loadBanners isCacheExpired:" + bannerManager.isCacheExpired(mPlate) + " banners:" + banners);
		if (banners != null && banners.size() > 0) {
			displayBanners(banners);
		}
		if (bannerManager.isCacheExpired(mPlate) || banners == null || banners.size() == 0) {
			mLoadingView.setVisibility(View.VISIBLE);
			mFailedView.setVisibility(View.GONE);
			mBannerView.setVisibility(View.GONE);
			bannerManager.getBannerList(mPlate, this);
		}
	}
	
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
		if(mActionLogs != null){
			mActionLogs.clear();
			mActionLogs = null;
		}
	}
	
	public void onHide(){
		mViewPager.setAdapter(null);
		stopAutoSlide();
	}
	
	public void onShow(){
		if (mAdapter == null) {
			loadBanners();
		} else {
			mViewPager.setAdapter(mAdapter);
			mViewPager.setCurrentItem(mSavedPosition);
			startAutoSlide();
		}
	}

	@Override
	public void onErr(Object o) {
		MyLog.d("onErr");
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mLoadingView.setVisibility(View.GONE);
				mFailedView.setVisibility(View.VISIBLE);
				mBannerView.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void onNoNetwork() {
		MyLog.d("onNoNetwork");
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mLoadingView.setVisibility(View.GONE);
				mFailedView.setVisibility(View.VISIBLE);
				mBannerView.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public void onGetBannerListSucc(final List<Banner> banners) {
		MyLog.d("onGetBannerListSucc: banners=" + banners);
		if (banners == null || banners.size() == 0){
			onErr(null);//没获取到banner，当作获取失败处理
			return;
		}else {
			int fromPlat = banners.get(0).getFromPlate();
			if (fromPlat != mPlate) {
				loadBanners();
				return;
			}
		}
		
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				MyLog.d("onGetBannerListSucc: banners.size:" + banners.size());
				displayBanners(banners);
			}
		});
	}

	private void displayBanners(List<Banner> banners) {
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
		if (mBanners.size() <= 1){
			mDotsIndicator.getChildAt(0).setVisibility(View.GONE);
		} else {
			mDotsIndicator.getChildAt(0).setVisibility(View.VISIBLE);
		}
		
		// 初始化ViewPager
		mAdapter = new BannerAdapter(context, mBanners);
		mAdapter.setPlate(mPlate, mColomn);
		mViewPager.setAdapter(mAdapter);
		mLastPosition = 0;
		mViewPager.setCurrentItem(mBanners.size() * 10000);
		
		if (isAutoSlideEnabled  && !isAutoSliding){
			startAutoSlide();
		}
		
		// 初始化时发送行为日志
		if (mBanners.size() > 0) {
//            StatManager.getInstance().onAction(StatManager.TYPE_STORE_ACTION,
//					BannerActionConstants.ACTION_LOG_1401,
//					mBanners.get(mLastPosition).getStrId(), 0, mPlate + "_0");
		}
	}
	
	@Override
	public void onPageSelected(int position) {
		mSavedPosition = position;
		Context context = mParentView.getContext();
		int newPosition = position % mBanners.size();
		mDotsIndicator.getChildAt(mLastPosition).setBackgroundResource(mNormalDot);
		mDotsIndicator.getChildAt(newPosition).setBackgroundResource(mSelectedDot);
		mLastPosition = newPosition;
		if (isAutoSlideEnabled && !isAutoSliding) {
			startAutoSlide();
		}
		
		String key = new StringBuilder().append(mPlate).append(mBanners.get(mLastPosition).getStrId()).toString();
		if((null == mActionLogs.get(key) || !mActionLogs.get(key))){
			mActionLogs.put(key, true);
			
			// 自动滚动时发送行为日志
//            StatManager.getInstance().onAction(StatManager.TYPE_STORE_ACTION, BannerActionConstants.ACTION_LOG_1401,
//					mBanners.get(mLastPosition).getStrId(), 0, mPlate + "_" + mLastPosition);
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
		}
		else if (isAutoSlideEnabled && !isAutoSliding) {
			startAutoSlide();
		}
	}
	
}
