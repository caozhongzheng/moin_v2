package com.moinapp.wuliao.modules.login;

import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.BaseKeyListener;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.utils.AnimationUtil;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.MD5;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.moinapp.wuliao.utils.Tools;

/**
 * Created by moying on 15/5/6.
 */
public class RegistActivity extends BaseActivity implements View.OnClickListener {

    private ILogger MyLog = LoggerFactory.getLogger(LoginModule.MODULE_NAME);
    private ImageView phone_drawable, email_drawable, pwd_visible, ivCursor;
    private int screenWidth;
    private int cursorWidth;
    private int cursorLastX;
    private int cursorPadingPx;
    private RelativeLayout.LayoutParams lpCursor;
    private EditText phone_et, email_et, code_et, password_et;
    private String phone_str, email_str, code_str, password_str, resend_str;
    private LinearLayout register_phone_title, register_email_title;
    private TextView get_code_sms, get_code_email;
    private int recLen_sms = 60, recLen_email = 60;
    private boolean phone = true;

    private final int  NULL = 0, ERROR = -1, CORRECT = 1;
    private int phone_state = NULL, email_state = NULL, code_state = NULL, password_state = NULL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_layout);

        initView();
        initData();
    }

    private void initView() {
        // 初始化栏目指示器
        ivCursor = (ImageView) this.findViewById(R.id.r_cursor).findViewById(R.id.iv_cursor);
        screenWidth = getResources().getDisplayMetrics().widthPixels;// 获取分辨率宽度
        cursorWidth = (int) (screenWidth / 2 + 0.5f);
        lpCursor = new RelativeLayout.LayoutParams(cursorWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        ivCursor.setLayoutParams(lpCursor);

        register_phone_title = (LinearLayout) this.findViewById(R.id.register_phone_title);
        register_email_title = (LinearLayout) this.findViewById(R.id.register_email_title);
        phone_drawable = (ImageView) this.findViewById(R.id.phone_drawable);
        email_drawable = (ImageView) this.findViewById(R.id.email_drawable);
        pwd_visible = (ImageView) this.findViewById(R.id.pwd_visible);

        phone_et = (EditText) this.findViewById(R.id.phone);
        email_et = (EditText) this.findViewById(R.id.email);
        code_et = (EditText) this.findViewById(R.id.code);
        password_et = (EditText) this.findViewById(R.id.password);

        get_code_sms = (TextView) this.findViewById(R.id.get_code_sms);
        get_code_email = (TextView) this.findViewById(R.id.get_code_email);
        toggleTab(true);
    }

    private void initData() {

        resend_str = getString(R.string.regist_resend);
        phone_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                phone_str = arg0.toString();
                if (phone_str.length() == 0) {
                    phone_state = NULL;
//                    phone_ok.setVisibility(View.INVISIBLE);
                } else if (!StringUtil.isCellphone(phone_str)) {
                    phone_state = ERROR;
//                    phone_ok.setVisibility(View.INVISIBLE);
                } else {
                    phone_state = CORRECT;
//                    phone_ok.setVisibility(View.VISIBLE);
                }
            }
        });

        code_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                code_str = arg0.toString();
                if (code_str.length() == 0) {
                    code_state = NULL;
                } else {
                    code_state = ERROR;
                }
            }
        });

        password_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                password_str = arg0.toString();
                if (password_str.length() == 0) {
                    password_state = NULL;
//                    password_ok.setVisibility(View.INVISIBLE);
                } else if (password_str.length() < 6 || password_str.length() > 16) {
                    password_state = ERROR;
//                    password_ok.setVisibility(View.VISIBLE);
                } else {
                    password_state = CORRECT;
                }
            }
        });

        email_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                email_str = arg0.toString();
                if (email_str.length() == 0) {
                    email_state = NULL;
//                    email_ok.setVisibility(View.INVISIBLE);
                } else if (!StringUtil.isEmail(email_str)) {
                    email_state = ERROR;
//                    email_ok.setVisibility(View.INVISIBLE);
                } else {
                    email_state = CORRECT;
//                    email_ok.setVisibility(View.VISIBLE);
                }
            }
        });

        password_et.setKeyListener(keyListener);

        password_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                pwd_visible.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    stopCountDown();
                    ToastUtils.toast(RegistActivity.this, R.string.regist_mobile_existed);
                    phone_et.requestFocus();
                    break;
                case 1:
                    getSmsCode();
                    code_et.requestFocus();
                    break;
                case -2:
                    stopCountDown();
                    if(msg.arg1 == -2) {
                        ToastUtils.toast(RegistActivity.this, R.string.regist_email_format_err);
                    } else {
                        ToastUtils.toast(RegistActivity.this, R.string.regist_email_existed);
                    }
                    email_et.requestFocus();
                    break;
                case 2:
                    getEmailCode();
                    code_et.requestFocus();
                    break;
                case 8:
                    ToastUtils.toast(RegistActivity.this, R.string.regist_succress);

                    AppTools.toIntent(RegistActivity.this, PersonalInfoActivity.class);
                    break;
                case -3:// 注册失败
                    stopCountDown();
                    ToastUtils.toast(RegistActivity.this, msg.obj.toString());
                    code_et.requestFocus();
                    break;
                case -4:
                    stopCountDown();
                    ToastUtils.toast(RegistActivity.this, R.string.regist_get_verify_code_fail);
                    code_et.requestFocus();
                    break;
                case -5:
                    stopCountDown();
                    ToastUtils.toast(RegistActivity.this, R.string.no_network);
                    code_et.requestFocus();
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.register_phone_title:
                toggleTab(true);
                break;
            case R.id.register_email_title:
                toggleTab(false);
                break;
            case R.id.get_code_sms:
                if(!Tools.isFastDoubleClick()) {
                    get_code_sms.setClickable(false);
                    MyLog.i("fast click get sms:" + System.currentTimeMillis());
                    checkPhone();
                }
                break;
            case R.id.get_code_email:
                if(!Tools.isFastDoubleClick()) {
                    get_code_email.setClickable(false);
                    MyLog.i("fast click get email:" + System.currentTimeMillis());
                    checkEmail();
                }
                break;
            case R.id.pwd_visible:
                togglePwdVisible();
                break;
            case R.id.agreement:
                Bundle bundle = new Bundle();
                bundle.putString("agree", "fromRegister");
                AppTools.toIntent(this, bundle, AgreementActivity.class);
                break;
            case R.id.register_submit:
                if(!Tools.isFastDoubleClick()) {
                    if (phone) {
                        if (phone_state == CORRECT && code_state != NULL && password_state == CORRECT) {
                            if (!"".equals(code_str)) {
                                phoneRegister();
                            }
                        } else {
                            ToastUtils.toast(RegistActivity.this, R.string.regist_input_error);
                        }
                    } else {
                        if (email_state == CORRECT && code_state != NULL && password_state == CORRECT) {
                            if (!"".equals(code_str)) {
                                emailRegister();
                            }
                        } else {
                            ToastUtils.toast(RegistActivity.this, R.string.regist_input_error);
                        }
                    }
                }
                break;
        }
    }

    private void checkPhone() {
        phone_str = phone_et.getText().toString();
        if (phone_str.length() == 0) {
            ToastUtils.toast(RegistActivity.this, R.string.regist_phone_null);
            get_code_sms.setClickable(true);
        } else if (!StringUtil.isCellphone(phone_str)) {
            ToastUtils.toast(RegistActivity.this, R.string.regist_phone_format_err);
            get_code_sms.setClickable(true);
        } else {
            startCountDown();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 验证手机号有没有注册过
                        LoginManager.getInstance()
                                .userCheckPhone(phone_str, new IListener() {
                                    @Override
                                    public void onSuccess(Object obj) {
                                        if ((int) obj == 1) {
                                            Message msg = handler.obtainMessage();
                                            msg.what = 1;
                                            handler.sendMessage(msg);
                                        } else if ((int) obj == 0) {
                                            Message msg = handler.obtainMessage();
                                            msg.what = -1;
                                            handler.sendMessage(msg);
                                        }
                                    }

                                    @Override
                                    public void onNoNetwork() {
                                        Message msg = handler.obtainMessage();
                                        msg.what = -5;
                                        handler.sendMessage(msg);
                                    }

                                    @Override
                                    public void onErr(Object obj) {
                                        Message msg = handler.obtainMessage();
                                        msg.what = -1;
                                        handler.sendMessage(msg);
                                    }
                                });
                    } catch (Exception e) {
                        get_code_sms.setClickable(true);
                        e.printStackTrace();
                    } finally {
                    }

                }
            }).start();
        }
    }

    private void checkEmail() {
        email_str = email_et.getText().toString();
        if (email_str.length() == 0) {
            ToastUtils.toast(RegistActivity.this, R.string.regist_email_null);
            get_code_email.setClickable(true);
        } else if (!StringUtil.isEmail(email_str)) {
            ToastUtils.toast(RegistActivity.this, R.string.regist_email_format_err);
            get_code_email.setClickable(true);
        } else {
            startCountDown();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 验证邮箱有没有注册过
                        LoginManager.getInstance()
                                .userCheckmail(email_str, new IListener() {
                                    @Override
                                    public void onSuccess(Object obj) {
                                        MyLog.i("checkEmail:ok="+(int)obj);
                                        if ((int) obj == 1) {
                                            Message msg = handler.obtainMessage();
                                            msg.what = 2;
                                            handler.sendMessage(msg);
                                        } else if ((int) obj == 0) {
                                            Message msg = handler.obtainMessage();
                                            msg.what = -2;
                                            handler.sendMessage(msg);
                                        }
                                    }

                                    @Override
                                    public void onNoNetwork() {
                                        Message msg = handler.obtainMessage();
                                        msg.what = -5;
                                        handler.sendMessage(msg);
                                    }

                                    @Override
                                    public void onErr(Object obj) {
                                        MyLog.i("checkEmail:error="+(int)obj);
                                        Message msg = handler.obtainMessage();
                                        msg.what = -2;
                                        msg.arg1 = (int)obj;
                                        handler.sendMessage(msg);
                                    }
                                });
                    } catch (Exception e) {
                        get_code_email.setClickable(true);
                        e.printStackTrace();
                    } finally {
                    }

                }
            }).start();
        }
    }

    /**
     * 获取验证码
     */
    private void getSmsCode() {
        LoginManager.getInstance()
                .getSmsCode(phone_str, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        MyLog.i("getSmsCode=" + phone_str + ", recLen=" + recLen_sms);
                    }

                    @Override
                    public void onNoNetwork() {
                        Message msg = handler.obtainMessage();
                        msg.what = -5;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onErr(Object obj) {
                        Message msg = handler.obtainMessage();
                        msg.what = -4;
                        handler.sendMessage(msg);
                    }
                });

    }

    private void getEmailCode() {
        LoginManager.getInstance()
                .getEmailCode(email_str, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        MyLog.i("getEmailCode=" + email_str + ", recLen=" + recLen_email);
                    }

                    @Override
                    public void onNoNetwork() {
                        Message msg = handler.obtainMessage();
                        msg.what = -5;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onErr(Object obj) {
                        Message msg = handler.obtainMessage();
                        msg.what = -4;
                        handler.sendMessage(msg);
                    }
                });

    }

    private void startCountDown() {
        if(phone) {
            get_code_sms.setClickable(false);
            handler.removeCallbacks(runnable_sms);
            recLen_sms = 60;
            handler.postDelayed(runnable_sms, 1000);
        } else {
            get_code_email.setClickable(false);
            handler.removeCallbacks(runnable_email);
            recLen_email = 60;
            handler.postDelayed(runnable_email, 1000);
        }
    }

    private void stopCountDown() {
        if(phone) {
            handler.removeCallbacks(runnable_sms);
            get_code_sms.setText(R.string.regist_get_code);
            get_code_sms.setClickable(true);
        } else {
            handler.removeCallbacks(runnable_email);
            get_code_email.setText(R.string.regist_get_code);
            get_code_email.setClickable(true);
        }
    }

    Runnable runnable_sms = new Runnable() {
        @Override
        public void run() {
            recLen_sms--;
            if (recLen_sms > 0) {
                get_code_sms.setClickable(false);
                get_code_sms.setText(recLen_sms + resend_str);
                handler.postDelayed(this, 1000);
            } else {
                get_code_sms.setClickable(true);
                get_code_sms.setText(R.string.regist_get_code);
            }
        }
    };

    Runnable runnable_email = new Runnable() {
        @Override
        public void run() {
            recLen_email--;
            if (recLen_email > 0) {
                get_code_email.setClickable(false);
                get_code_email.setText(recLen_email + resend_str);
                handler.postDelayed(this, 1000);
            } else {
                get_code_email.setClickable(true);
                get_code_email.setText(R.string.regist_get_code);
            }
        }

    };

    private void phoneRegister() {
        LoginManager.getInstance()
                .registerUser(0, phone_str, MD5.md5(password_str), code_str, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        Bundle b = new Bundle();
                        b.putString("name", phone_str);
                        b.putString("phone", phone_str);
                        b.putInt("type", 1);
                        Message msg = handler.obtainMessage();
                        msg.what = 8;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onNoNetwork() {
                        Message msg = handler.obtainMessage();
                        msg.what = -5;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onErr(Object object) {
                        Message msg = handler.obtainMessage();
                        msg.what = -3;
                        StringBuilder sb = new StringBuilder();
                        switch ((int)object) {
                            case -2:
                                sb.append(getString(R.string.regist_failed));//"-2 信息有误，数据库保存失败"
                                break;
                            case -1:
                                sb.append(getString(R.string.regist_verify_code_wrong));
                                break;
                        }
                        msg.obj = sb.toString();
                        handler.sendMessage(msg);
                    }
                });
    }

    private void emailRegister() {
        LoginManager.getInstance()
                .registerUser(1, email_str, MD5.md5(password_str), code_str, new IListener() {
                    @Override
                    public void onSuccess(Object obj) {
                        Bundle b = new Bundle();
                        b.putString("name", email_str);
                        b.putString("email", email_str);
                        b.putInt("type", 2);
                        Message msg = handler.obtainMessage();
                        msg.what = 8;
                        handler.sendMessage(msg);
                        AppTools.toIntent(RegistActivity.this, PersonalInfoActivity.class);
                    }

                    @Override
                    public void onNoNetwork() {
                        Message msg = handler.obtainMessage();
                        msg.what = -5;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onErr(Object object) {
                        Message msg = handler.obtainMessage();
                        msg.what = -3;
                        StringBuilder sb = new StringBuilder();
                        switch ((int)object) {
                            case -2:
                                sb.append(getString(R.string.regist_failed));//"-2 信息有误，数据库保存失败"
                                break;
                            case -1:
                                sb.append(getString(R.string.regist_verify_code_wrong));
                                break;
                        }
                        msg.obj = sb.toString();
                        handler.sendMessage(msg);
                    }
                });
    }

    private void toggleTab(boolean phone) {
        this.phone = phone;
        //register_phone_title.setBackgroundResource(phone ? R.drawable.register_line : R.drawable.transparent);
        //register_email_title.setBackgroundResource(!phone ? R.drawable.register_line : R.drawable.transparent);

        int p = phone ? 0 : 1;
        AnimationUtil.moveTo(ivCursor, 200, cursorLastX, cursorWidth * p, 0, 0);
        cursorLastX = cursorWidth * p;

        phone_drawable.setVisibility(phone ? View.VISIBLE : View.GONE);
        phone_et.setVisibility(phone ? View.VISIBLE : View.GONE);
        email_drawable.setVisibility(!phone ? View.VISIBLE : View.GONE);
        email_et.setVisibility(!phone ? View.VISIBLE : View.GONE);
        get_code_sms.setVisibility(phone ? View.VISIBLE : View.GONE);
        get_code_email.setVisibility(!phone ? View.VISIBLE : View.GONE);
        code_et.setHint(phone ? R.string.regist_phone_captcha_hint : R.string.regist_email_captcha_hint);

        if(phone) {
            phone_et.requestFocus();
        } else {
            email_et.requestFocus();
        }
    }

    private void togglePwdVisible() {
        int type = password_et.getInputType();
        if(type == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            password_et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            password_et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        password_et.setSelection(password_et.getText().length());
        password_et.setKeyListener(keyListener);
    }

    BaseKeyListener keyListener = new NumberKeyListener() {
        @Override
        protected char[] getAcceptedChars() {
            return StringUtil.passwordDigits();
        }

        @Override
        public int getInputType() {
            return password_et.getInputType();
        }
    };
}
