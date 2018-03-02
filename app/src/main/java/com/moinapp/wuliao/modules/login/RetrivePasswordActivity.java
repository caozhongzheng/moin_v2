package com.moinapp.wuliao.modules.login;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.moinapp.wuliao.MoinActivity;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.ui.BaseFragmentActivity;
import com.moinapp.wuliao.commons.ui.FragmentSkip;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.MD5;
import com.moinapp.wuliao.utils.ToastUtils;

import java.util.ArrayList;

/**
 * Created by moying on 15/5/6.
 */
public class RetrivePasswordActivity extends BaseFragmentActivity implements FragmentSkip {

    private ViewPager viewPager;
    private ArrayList<Fragment> fragments;
    private String phone, captcha, email, new_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.retrive_layout);
        initViewPager();
    }

    public void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragments = new ArrayList<Fragment>();
        Fragment fragment1 = new RetrievePassword1_Fragment();
        Fragment fragment2 = new RetrievePassword2_Fragment();
        fragments.add(fragment1);
        fragments.add(fragment2);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return fragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return fragments.get(arg0);
            }
        });

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            goback();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.back:
                goback();
        }
    }

    private void goback() {
        switch (viewPager.getCurrentItem()) {
            case 0:
//                        finish();
                if(!TextUtils.isEmpty(ClientInfo.getUID())) {
                    AppTools.toIntent(RetrivePasswordActivity.this, MoinActivity.class);
                } else {
                    finish();
                }
                break;
            case 1:
                viewPager.setCurrentItem(0);
                break;
            case 2:
                viewPager.setCurrentItem(1);
                break;
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ToastUtils.toast(RetrivePasswordActivity.this, R.string.retrieve_success);
                    // TODO skip to MoinActivity or just finish self?
//                    finish();
                    if(!TextUtils.isEmpty(ClientInfo.getUID())) {
                        AppTools.toIntent(RetrivePasswordActivity.this, MoinActivity.class);
                    } else {
                        finish();
                    }
                    break;
                case -1:
                    ToastUtils.toast(RetrivePasswordActivity.this, R.string.retrieve_fail);
                    break;
                case -2:
                    ToastUtils.toast(RetrivePasswordActivity.this, R.string.no_network);
                    break;
            }
        }
    };

    private int page;

    @Override
    public void skip(int position, String... params) {
        if (position == 1) {
            page = position;
            handler.post(runnable);
            phone = params[0];
            captcha = params[1];
            email = params[2];
        } else if (position == 2) {
            page = position;
            handler.post(runnable);
            new_password = params[0];
            new Thread(new Runnable() {
                @Override
                public void run() {
                    /**更新的密码 * 返回：result 0 失败，1 成功 * error: -1 找不到当前用户 -2 密码不正确或者session信息有误 -3 信息有误，保存数据库失败 */
                    LoginManager.getInstance().userUpdatePassword(MD5.md5(new_password), new IListener() {
                        @Override
                        public void onSuccess(Object obj) {
                            Message msg = handler.obtainMessage();
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onNoNetwork() {
                            Message msg = handler.obtainMessage();
                            msg.what = -2;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onErr(Object object) {
                            Message msg = handler.obtainMessage();
                            msg.what = -1;
                            handler.sendMessage(msg);
                        }
                    });

                }
            }).start();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            viewPager.setCurrentItem(page);
        }
    };
}
