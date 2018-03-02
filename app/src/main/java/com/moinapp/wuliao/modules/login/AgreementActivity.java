package com.moinapp.wuliao.modules.login;

import android.os.Bundle;
import android.view.View;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ui.BaseActivity;

/**
 * Created by moying on 15/5/6.
 */
public class AgreementActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.agreement_activity_layout);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

}
