package com.moinapp.wuliao.modules.mine;

import android.os.Bundle;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ui.BaseActivity;

/**
 * Created by moying on 15/6/26.
 */
public class AboutMOINActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about_moin_layout);

        initTopBar(getString(R.string.about_moin));
        initLeftBtn(true, R.drawable.back_gray);
    }
}
