package com.moinapp.wuliao.commons.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.AppConstants;
import com.moinapp.wuliao.commons.info.MobileInfo;

/**
 * 所有包含表情键盘的Actvity都扩展自此类。
 */
public class BaseEmojActivity extends Activity{
	private LayoutInflater mCustomInflater;

	protected TextView tv_left_txt;
	protected ImageView tv_left_img;
	protected TextView tv_title;
	protected TextView tv_right_txt;
	protected ImageView tv_right_img;
	protected RelativeLayout tv_left;
	protected RelativeLayout tv_right;
	protected RelativeLayout rl_enter;
	protected LinearLayout lv_loading;
	protected LinearLayout lv_reload;

	protected final static int MODE_LOADING = AppConstants.MODE_LOADING;
	protected final static int MODE_RELOADING = AppConstants.MODE_RELOADING;
	protected final static int MODE_OK = AppConstants.MODE_OK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

    @Override
    protected void onResume() {
        super.onResume();
        if(MobileInfo.getDeviceName().equals("ZTE__ZTE__ZTE G718C")){
            getWindow().getDecorView().setPadding(0,50,0,0);
        }
    }

	@Override
	public Object getSystemService(final String name) {
	    if (Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
	        return getCustomLayoutInflater();
	    }
	    return super.getSystemService(name);
	}

	@Override
	public LayoutInflater getLayoutInflater() {
	    return getCustomLayoutInflater();
	}

	private LayoutInflater getCustomLayoutInflater() {
	    if (mCustomInflater == null) {
	    	LayoutInflater systemInflater = (LayoutInflater)super.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			if (FontFactory.hasSetDefaultTypeface()) {
//				mCustomInflater = new TypefaceLayoutInflater(systemInflater,
//						this);
//				mCustomInflater.setFactory(this);
//			} else {
				mCustomInflater = systemInflater;
//			}
	    }
	    
	    return mCustomInflater;
	}
	
 	@Override
 	public AssetManager getAssets() {  
 	    return this.getApplicationContext().getAssets(); 
 	}  

 	@Override
 	public Resources getResources() {  
 	    return this.getApplicationContext().getResources();  
 	}


	protected void initLoadingView() {
		rl_enter = (RelativeLayout) this.findViewById(R.id.__enter);
		lv_loading = (LinearLayout) this.findViewById(R.id.__loading);
		lv_reload = (LinearLayout) this.findViewById(R.id.__reload);
		lv_reload.setOnClickListener(onclick);
	}

	protected void setLoadingMode(int mode) {
		rl_enter.setVisibility(mode == MODE_OK ? View.GONE : View.VISIBLE);
		lv_loading.setVisibility(mode == MODE_LOADING ? View.VISIBLE : View.GONE);
		lv_reload.setVisibility(mode == MODE_RELOADING ? View.VISIBLE : View.GONE);
	}

	protected void initTopBar(String title) {
		tv_left = (RelativeLayout) this.findViewById(R.id.tv_left);
		tv_left_txt = (TextView) this.findViewById(R.id.tv_left_txt);
		tv_left_img = (ImageView) this.findViewById(R.id.tv_left_img);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_right = (RelativeLayout) this.findViewById(R.id.tv_right);
		tv_right_txt = (TextView) this.findViewById(R.id.tv_right_txt);
		tv_right_img = (ImageView) this.findViewById(R.id.tv_right_img);

		if (tv_title != null && title != null) {
			tv_title.setText(title);
			tv_title.setVisibility(View.VISIBLE);
		}
	}

	protected void initLeftBtn(boolean isVisibility, int bg) {
		if (tv_left == null) {
			return;
		}
		if (isVisibility) {
			tv_left.setVisibility(View.VISIBLE);
			tv_left_txt.setVisibility(View.GONE);
			tv_left_img.setVisibility(View.VISIBLE);
			tv_left.setOnClickListener(onclick);
		} else {
			tv_left.setVisibility(View.GONE);
		}
		tv_left_img.setImageResource(bg);
	}

	protected void initLeftBtn(boolean isVisibility, String str) {
		if (tv_left == null) {
			return;
		}
		if (isVisibility) {
			tv_left.setVisibility(View.VISIBLE);
			tv_left_txt.setVisibility(View.VISIBLE);
			tv_left_img.setVisibility(View.GONE);
			tv_left.setOnClickListener(onclick);
		} else {
			tv_left.setVisibility(View.GONE);
		}
		tv_left_txt.setText(str);
	}

	protected void initRightBtn(boolean isVisibility, int bg) {
		if (tv_right == null) {
			return;
		}
		if (isVisibility) {
			tv_right.setVisibility(View.VISIBLE);
			tv_right_img.setVisibility(View.VISIBLE);
			tv_right_txt.setVisibility(View.GONE);
			tv_right.setOnClickListener(onclick);
		} else {
			tv_right.setVisibility(View.GONE);
		}
		tv_right_img.setImageResource(bg);
	}

	protected void initRightBtn(boolean isVisibility, String str) {
		if (tv_right == null) {
			return;
		}
		if (isVisibility) {
			tv_right.setVisibility(View.VISIBLE);
			tv_right_img.setVisibility(View.GONE);
			tv_right_txt.setVisibility(View.VISIBLE);
			tv_right.setOnClickListener(onclick);
		} else {
			tv_right.setVisibility(View.GONE);
		}
		tv_right_txt.setText(str);
	}

	private View.OnClickListener onclick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.tv_left:
					leftBtnHandle();
					break;
				case R.id.tv_right:
					rightBtnHandle();
					break;
				case R.id.__reload:
					reloadHandle();
					break;
			}
		}
	};

	protected void reloadHandle() {
		setLoadingMode(MODE_LOADING);
	}

	protected void rightBtnHandle() { }

	protected void leftBtnHandle() {
		finish();
	}

}
