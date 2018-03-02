package com.moinapp.wuliao.modules.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.ui.FragmentSkip;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;

/**
 * Created by moying on 15/5/7.
 */
public class RetrievePassword2_Fragment extends Fragment {
    private static ILogger MyLog = LoggerFactory.getLogger(LoginModule.MODULE_NAME);

    private FragmentSkip callback;

    private EditText password1_et, password2_et;
    private String pwd1_str, pwd2_str;

    private final int  NULL = 0, ERROR = -1, CORRECT = 1;
    private int pwd1_state = NULL, pwd2_state = NULL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.retrive_phone2_layout, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (FragmentSkip) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        password1_et = (EditText) getActivity().findViewById(R.id.password1);
        password2_et = (EditText) getActivity().findViewById(R.id.password2);

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
//                    phone_ok.setVisibility(View.INVISIBLE);
                } else if (pwd1_str.length() < 6) {
                    pwd1_state = ERROR;
//                    phone_ok.setVisibility(View.INVISIBLE);
                } else {
                    pwd1_state = CORRECT;
//                    phone_ok.setVisibility(View.VISIBLE);
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
//                    phone_ok.setVisibility(View.INVISIBLE);
                } else if (pwd2_str.length() < 6) {
                    pwd2_state = ERROR;
//                    phone_ok.setVisibility(View.INVISIBLE);
                } else {
                    pwd2_state = CORRECT;
//                    phone_ok.setVisibility(View.VISIBLE);
                }
            }
        });

        getActivity().findViewById(R.id.retrive_sure_submit)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resetPassword();
                    }
                });

    }

    private void resetPassword() {
        if(pwd1_state == CORRECT && pwd2_state == CORRECT) {
            if(pwd1_str.equals(pwd2_str)) {
                callback.skip(2, pwd1_str);
            } else {
                ToastUtils.toast(getActivity(), R.string.retrieve_err5);
            }
        } else {
            ToastUtils.toast(getActivity(), R.string.regist_password_tip);
        }
    }

}
