package com.moinapp.wuliao.modules.login;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.ui.FragmentSkip;
import com.moinapp.wuliao.utils.AnimationUtil;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.moinapp.wuliao.utils.Tools;

/**
 * Created by moying on 15/5/6.
 */
public class RetrievePassword1_Fragment extends Fragment {

    private ILogger MyLog = LoggerFactory.getLogger(LoginModule.MODULE_NAME);
    private Activity activity;
    private FragmentSkip callback;
    private EditText phone_et, email_et, code_et;

    private int cursorWidth;
    private int cursorLastX;

    private ImageView phone_drawable, email_drawable, ivCursor;
    private TextView get_code_sms, get_code_email, submit_tv;
    private LinearLayout retrive_phone_title, retrive_email_title;
    private boolean byphone = true;
    private int recLen_sms = 60, recLen_email = 60;

    private String phone_str, email_str, code_str, resend_str;
    private final int  NULL = 0, ERROR = -1, CORRECT = 1;
    private int phone_state = NULL, email_state = NULL, code_state = NULL;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    stopCountDown();
                    ToastUtils.toast(getActivity(), R.string.retrieve_phone_unregister);
                    phone_et.requestFocus();
                    break;
                case 1:
                    getSmsCode();
                    code_et.requestFocus();
                    break;
                case -2:
                    stopCountDown();
                    ToastUtils.toast(getActivity(), R.string.retrieve_email_unregister);
                    email_et.requestFocus();
                    break;
                case 2:
                    getEmailCode();
                    code_et.requestFocus();
                    break;
                case -3:// verify failed
                    stopCountDown();
                    ToastUtils.toast(activity, msg.obj.toString());
                    break;
                case -4:
                    stopCountDown();
                    ToastUtils.toast(getActivity(), R.string.regist_get_verify_code_fail);
                    break;
                case -5:
                    stopCountDown();
                    ToastUtils.toast(getActivity(), R.string.no_network);
                    code_et.requestFocus();
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.retrive_phone1_layout, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = getActivity();
        callback = (FragmentSkip) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        phone_et = (EditText) activity.findViewById(R.id.phone);
        email_et = (EditText) activity.findViewById(R.id.email);
        code_et = (EditText) activity.findViewById(R.id.code);
        phone_drawable = (ImageView) activity.findViewById(R.id.phone_drawable);
        email_drawable = (ImageView) activity.findViewById(R.id.email_drawable);

        ivCursor = (ImageView) activity.findViewById(R.id.r_cursor).findViewById(R.id.iv_cursor);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;// 获取分辨率宽度
        cursorWidth = (int) (screenWidth / 2 + 0.5f);
        ivCursor.setLayoutParams(new RelativeLayout.LayoutParams(cursorWidth, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

        get_code_sms = (TextView) activity.findViewById(R.id.get_code_sms);
        get_code_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Tools.isFastDoubleClick())
                    checkPhone();
            }
        });
        get_code_email = (TextView) activity.findViewById(R.id.get_code_email);
        get_code_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Tools.isFastDoubleClick())
                    checkEmail();
            }
        });

        resend_str = getString(R.string.regist_resend);

        retrive_phone_title = (LinearLayout) activity.findViewById(R.id.retrive_phone_title);
        retrive_email_title = (LinearLayout) activity.findViewById(R.id.retrive_email_title);
        toggleTab(true);
        retrive_phone_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTab(true);
            }
        });
        retrive_email_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTab(false);
            }
        });

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

        submit_tv = (TextView) activity.findViewById(R.id.retrive_verify_submit);
        submit_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Tools.isFastDoubleClick()) {
                    if(byphone) {
                        phoneRetrive();
                    } else {
                        emailRetrive();
                    }
                }
            }
        });

    }

    private void phoneRetrive() {
        MyLog.i("phoneRetrive phone=" + phone_str + ", code=" + code_str);
        if (phone_state == CORRECT && code_state != NULL) {
            String code = code_et.getText().toString();
            if (!"".equals(code))
                LoginManager.getInstance().retrievePasswordByPhone(phone_str,
                        code_str, new IListener() {
                            @Override
                            public void onSuccess(Object obj) {
                                MyLog.i("phoneRetrive onSuccess=" + phone_str + ", code=" + code_str);
                                callback.skip(1, phone_str, code_str, email_str);
                            }

                            @Override
                            public void onNoNetwork() {
                                Message msg = handler.obtainMessage();
                                msg.what = -5;
                                handler.sendMessage(msg);
                            }

                            @Override
                            public void onErr(Object object) {
                                MyLog.i("phoneRetrive onErr=" + object.toString());
                                Message msg = handler.obtainMessage();
                                msg.what = -3;
                                msg.obj = getErrInfo((int) object);
                                handler.sendMessage(msg);

                                code_et.requestFocus();
                            }
                        });
            else
                ToastUtils.toast(activity, R.string.getsms_code_empty);
        } else {
            ToastUtils.toast(activity, R.string.retrieve_phone_tip);
            phone_et.requestFocus();
        }
    }

    private void emailRetrive() {
        MyLog.i("emailRetrive email=" + email_str + ", code=" + code_str);
        if (email_state == CORRECT && code_state != NULL) {
            String code = code_et.getText().toString();
            if (!"".equals(code))
                LoginManager.getInstance().retrievePasswordByEmail(email_str,
                        code_str, new IListener() {
                            @Override
                            public void onSuccess(Object obj) {
                                MyLog.i("emailRetrive onSuccess=" + phone_str + ", code=" + code_str);
                                callback.skip(1, phone_str, code_str, email_str);
                            }

                            @Override
                            public void onNoNetwork() {
                                Message msg = handler.obtainMessage();
                                msg.what = -5;
                                handler.sendMessage(msg);
                            }

                            @Override
                            public void onErr(Object object) {
                                MyLog.i("emailRetrive onErr=" + object.toString());
                                Message msg = handler.obtainMessage();
                                msg.what = -3;
                                msg.obj = getErrInfo((int) object);
                                handler.sendMessage(msg);

                                code_et.requestFocus();
                            }
                        });
            else
                ToastUtils.toast(activity, R.string.getsms_code_empty);
        } else {
            ToastUtils.toast(activity, R.string.retrieve_email_tip);
            email_et.requestFocus();
        }
    }

    private String getErrInfo(int object) {
        switch (object) {
            case 2:
                return getString(R.string.retrieve_err2);
            case -2:
                return getString(R.string.retrieve_err4);
            case -3:
                return getString(R.string.retrieve_err3);
            case -1:
                return getString(R.string.retrieve_err1);
        }
        return "";
    }

    private void checkPhone() {
        phone_str = phone_et.getText().toString();
        if(StringUtil.isNullOrEmpty(phone_str)) {
            ToastUtils.toast(activity, R.string.regist_phone_null);
        } else if (!StringUtil.isCellphone(phone_str)) {
            ToastUtils.toast(activity, R.string.regist_phone_format_err);
            phone_et.requestFocus();
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
                                        Message msg = handler.obtainMessage();
                                        msg.what = -1;
                                        handler.sendMessage(msg);
                                    }

                                    @Override
                                    public void onNoNetwork() {
                                        Message msg = handler.obtainMessage();
                                        msg.what = -5;
                                        handler.sendMessage(msg);
                                    }

                                    @Override
                                    public void onErr(Object obj) {
                                        /**error（出错信息）-1 手机号码已存在，-2 手机号码格式有误*/
                                        if ((int) obj == -2) { // phone format err
                                            Message msg = handler.obtainMessage();
                                            msg.what = -1;
                                            handler.sendMessage(msg);
                                        } else if ((int) obj == -1) {
                                            Message msg = handler.obtainMessage();
                                            msg.what = 1;
                                            handler.sendMessage(msg);
                                        }
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    private void checkEmail() {
        email_str = email_et.getText().toString();
        if(StringUtil.isNullOrEmpty(email_str)) {
            ToastUtils.toast(activity, R.string.regist_email_null);
        } else if (!StringUtil.isEmail(email_str)) {
            ToastUtils.toast(activity, R.string.regist_email_format_err);
            email_et.requestFocus();
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
                                        Message msg = handler.obtainMessage();
                                        msg.what = -2;
                                        handler.sendMessage(msg);
                                    }

                                    @Override
                                    public void onNoNetwork() {
                                        Message msg = handler.obtainMessage();
                                        msg.what = -5;
                                        handler.sendMessage(msg);
                                    }

                                    @Override
                                    public void onErr(Object obj) {
                                        /**error（出错信息）-1 邮箱已存在，-2 邮箱格式有误*/
                                        if ((int) obj == -2) { // phone format err
                                            Message msg = handler.obtainMessage();
                                            msg.what = -2;
                                            handler.sendMessage(msg);
                                        } else if ((int) obj == -1) {
                                            Message msg = handler.obtainMessage();
                                            msg.what = 2;
                                            handler.sendMessage(msg);
                                        }

                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
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
        if(byphone) {
            handler.removeCallbacks(runnable_sms);
            recLen_sms = 60;
            handler.postDelayed(runnable_sms, 1000);
        } else {
            handler.removeCallbacks(runnable_email);
            recLen_email = 60;
            handler.postDelayed(runnable_email, 1000);
        }
    }

    private void stopCountDown() {
        if(byphone) {
            handler.removeCallbacks(runnable_sms);
            get_code_sms.setText(R.string.regist_get_code);
        } else {
            handler.removeCallbacks(runnable_email);
            get_code_email.setText(R.string.regist_get_code);
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

    private void toggleTab(boolean phone) {
        byphone = phone;
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
}
