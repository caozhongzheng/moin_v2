package com.moinapp.wuliao.modules.mine;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.EditText;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.modules.login.LoginManager;
import com.moinapp.wuliao.utils.MD5;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;

/**
 * Created by moying on 15/6/26.
 */
public class ChangePwdActivity extends BaseActivity {

    private EditText password1_et, password2_et;
    private String pwd1_str, pwd2_str;

    private final int  NULL = 0, ERROR = -1, CORRECT = 1;
    private int pwd1_state = NULL, pwd2_state = NULL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.change_password_layout);
        
        initTopBar(getString(R.string.account_security));
        initLeftBtn(true, R.drawable.back_gray);
        initRightBtn(false, "");
        
        initView();
    }

    private void initView() {
        password1_et = (EditText) this.findViewById(R.id.password1);
        password2_et = (EditText) this.findViewById(R.id.password2);

        password1_et.setKeyListener(new NumberKeyListener() {
            @Override
            protected char[] getAcceptedChars() {
                return StringUtil.passwordDigits();
            }

            @Override
            public int getInputType() {
                return password1_et.getInputType();
            }
        });
        password2_et.setKeyListener(new NumberKeyListener() {
            @Override
            protected char[] getAcceptedChars() {
                return StringUtil.passwordDigits();
            }

            @Override
            public int getInputType() {
                return password2_et.getInputType();
            }
        });

        password1_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                pwd1_str = arg0.toString();
                if (pwd1_str.length() == 0) {
                    pwd1_state = NULL;
                } else if (pwd1_str.length() < 6) {
                    pwd1_state = ERROR;
                } else {
                    pwd1_state = CORRECT;
                }
            }
        });
        password2_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                pwd2_str = arg0.toString();
                if (pwd2_str.length() == 0) {
                    pwd2_state = NULL;
                } else if (pwd2_str.length() < 6) {
                    pwd2_state = ERROR;
                } else {
                    pwd2_state = CORRECT;
                }
            }
        });

        this.findViewById(R.id.change_password_submit)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changePassword();
                    }
                });
    }

    private void changePassword() {
        if(pwd1_state == NULL) {
            ToastUtils.toast(ChangePwdActivity.this, R.string.retrieve_password_hint);
        } else if(pwd2_state == NULL) {
            ToastUtils.toast(ChangePwdActivity.this, R.string.retrieve_password_sure_hint);
        } else if(pwd1_state == CORRECT && pwd2_state == CORRECT) {
            if(pwd1_str.equals(pwd2_str)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        /**更新的密码 * 返回：result 0 失败，1 成功 * error: -1 找不到当前用户 -2 密码不正确或者session信息有误 -3 信息有误，保存数据库失败 */
                        LoginManager.getInstance().userUpdatePassword(MD5.md5(pwd1_str), new IListener() {
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
            } else {
                ToastUtils.toast(ChangePwdActivity.this, R.string.retrieve_err5);
            }
        } else {
            ToastUtils.toast(ChangePwdActivity.this, R.string.regist_password_tip);
        }
    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    ToastUtils.toast(ChangePwdActivity.this, R.string.retrieve_success);
                    finish();
                    break;
                case -1:
                    ToastUtils.toast(ChangePwdActivity.this, R.string.retrieve_fail);
                    break;
                case -2:
                    ToastUtils.toast(ChangePwdActivity.this, R.string.no_network);
                    break;
            }
        }
    };
}
