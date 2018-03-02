package com.moinapp.wuliao.commons.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.moinapp.wuliao.R;

public class MoveBackView extends FrameLayout {

	private static final int DURATION = 1200;
	private ImageView ballLeft, ballRight;
	private View animView, loading;
	private int off = 0;
	
	public MoveBackView(Context context) {
		this(context, null);
	}
	
	public MoveBackView(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}
	
	public MoveBackView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ballLeft = (ImageView) findViewById(R.id.ball_left);
        ballRight = (ImageView) findViewById(R.id.ball_right);
        
        animView = findViewById(R.id.anim);
        loading = findViewById(R.id.loading);
    }
	
	private boolean mIsShowLoading = false;
	public void startAnim(boolean isShowLoading) {
		mIsShowLoading = isShowLoading;
		if(!mIsShowLoading)	{
			if(off > 0)
				createAnim();
			animView.setVisibility(VISIBLE);
			loading.setVisibility(GONE);
			setVisibility(VISIBLE);
		}else{
			stopAnim();
			//loading.setVisibility(0);
		}
	}
	
	public void stopAnim(){
		animView.setVisibility(GONE);
		loading.setVisibility(GONE);
		setVisibility(GONE);
	}
	
    private void createAnim() {
		float density = getContext().getResources().getDisplayMetrics().density;

		TranslateAnimation left = new TranslateAnimation(0,density * 30,0,0);
		left.setDuration(1000L);
		left.setRepeatCount(Animation.INFINITE);
		left.setRepeatMode(Animation.REVERSE);
		ballLeft.startAnimation(left);

		TranslateAnimation right = new TranslateAnimation(0,-density*30,0,0);
		right.setDuration(1000L);
		right.setRepeatCount(Animation.INFINITE);
		right.setRepeatMode(Animation.REVERSE);
		ballRight.startAnimation(right);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	super.onLayout(changed, l, t, r, b);
    	if(off == 0){
			off  = ((ViewGroup)ballLeft.getParent()).getWidth() - ballLeft.getWidth();
			startAnim(mIsShowLoading);
    	}
    }
    
}
