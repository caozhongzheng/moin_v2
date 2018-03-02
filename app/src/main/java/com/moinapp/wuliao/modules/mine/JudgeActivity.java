package com.moinapp.wuliao.modules.mine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.modules.feedback.FeedBackActivity;
import com.moinapp.wuliao.modules.wasai.WasaiContants;
import com.moinapp.wuliao.utils.AppTools;

/**
 * Created by moying on 15/6/26.
 */
public class JudgeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.judge_layout);

        initTopBar(getString(R.string.feedback));
        initLeftBtn(true, R.drawable.back_gray);

        findViewById(R.id.rl_good).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 给应用评分
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(WasaiContants.MOIN_APP_URL));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        findViewById(R.id.rl_bad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppTools.toIntent(JudgeActivity.this, FeedBackActivity.class);
            }
        });
    }
}
