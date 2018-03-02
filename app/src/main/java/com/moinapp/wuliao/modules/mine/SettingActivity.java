package com.moinapp.wuliao.modules.mine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.moinapp.wuliao.MoinActivity;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.modules.login.LoginManager;
import com.moinapp.wuliao.modules.login.LoginSuccJump;
import com.moinapp.wuliao.modules.mine.listener.UserTypeListener;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.ToastUtils;

import java.util.List;

/**
 * Created by moying on 15/6/26.
 */
public class SettingActivity extends BaseActivity {
    private ILogger MyLog = LoggerFactory.getLogger(MineModule.MODULE_NAME);
    MineManager mineManager;
    private boolean isGetUserType = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_layout);

        initTopBar(getString(R.string.setting));
        initLeftBtn(true, R.drawable.back_gray);
        initRightBtn(false, "");

        mineManager = MineManager.getInstance();
        findViewById(R.id.rl_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO change password, or bind qq/wx/sina account。 via 登录账号类型
                if (!isGetUserType) {
                    isGetUserType = true;

                    mineManager.getUserType(new UserTypeListener() {
                        @Override
                        public void getUserTypeSucc(int type, List<String> namelist) {
                            isGetUserType = false;
                            if (type == 1) {
                                AppTools.toIntent(SettingActivity.this, ChangePwdActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                        }

                        @Override
                        public void onNoNetwork() {

                        }

                        @Override
                        public void onErr(Object object) {
                            isGetUserType = false;
                        }
                    });

                }

            }
        });

        findViewById(R.id.rl_feedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppTools.toIntent(SettingActivity.this, JudgeActivity.class);
            }
        });

        findViewById(R.id.rl_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppTools.toIntent(SettingActivity.this, AboutMOINActivity.class);
            }
        });

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().userLogout(new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    public void onNoNetwork() {
                        handler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onErr(Object object) {
                        handler.sendEmptyMessage(3);
                    }
                });
            }
        });

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ToastUtils.toast(SettingActivity.this, R.string.logout_tip1);
                    MinePreference.getInstance().setNeedFetchNickname(true);
                    AppTools.toIntent(SettingActivity.this, MoinActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);

                    // 发送登陆成功时间通知窝列表重新刷新
                    EventBus.getDefault().post(new LoginSuccJump.LoginSuccessEvent());
                    break;
                case 2:
                    ToastUtils.toast(SettingActivity.this, R.string.no_network);
                    break;
                case 3:
                    ToastUtils.toast(SettingActivity.this, R.string.logout_tip2);
                    break;
            }
        }
    };
}
