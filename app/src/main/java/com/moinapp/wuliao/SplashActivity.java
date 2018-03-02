package com.moinapp.wuliao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by moying on 15/7/17.
 */
public class SplashActivity extends Activity {
    private final static int SWITCH_MAIN_ACTIVITY = 1000;
    private final static int SWITCH_GUIDE_ACTIVITY = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_layout);

        if(MoinPreference.getInstance().isFirstEnter()) {
            mHandler.sendEmptyMessageDelayed(SWITCH_GUIDE_ACTIVITY, 1500);
        } else {
            mHandler.sendEmptyMessageDelayed(SWITCH_MAIN_ACTIVITY, 1500);
        }

    }

    @Override
    public void onBackPressed() {
        mHandler.removeMessages(SWITCH_MAIN_ACTIVITY);
        finish();
    }

    //*************************************************
    // Handler:跳转至不同页面
    //*************************************************
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SWITCH_MAIN_ACTIVITY:
                    Intent mIntent = new Intent();
                    mIntent.setClass(SplashActivity.this, MoinActivity.class);
                    startActivity(mIntent);
                    finish();
                    break;
                case SWITCH_GUIDE_ACTIVITY:
                    Intent mIntent2 = new Intent();
                    mIntent2.setClass(SplashActivity.this, GuideActivity.class);
                    startActivity(mIntent2);
                    finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
