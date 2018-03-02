package com.moinapp.wuliao.modules.mine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.modules.ipresource.IpDetailEmjGridAdapter;
import com.moinapp.wuliao.modules.ipresource.model.EmojiInfo;
import com.moinapp.wuliao.modules.ipresource.model.EmojiResource;

import java.util.List;

/**
 * Created by liujiancheng on 15/7/1.
 */
public class MyEmojiDetailActivity extends BaseActivity {
    public static final String EMOJI = "emoji_resource";

    private EmojiResource mEmojiResource;
    public GridView myGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_emoji_detail);


        //得到ip的i的id参数
        Intent intent = getIntent();
        if (intent != null) {
            mEmojiResource =(EmojiResource)getIntent().getSerializableExtra(EMOJI);
            if (mEmojiResource == null) {
                finish();//参数错误退出
            } else {
                initTopBar(mEmojiResource.getName());
                initLeftBtn(true, R.drawable.back_gray);
            }
        } else {
            finish();
        }

        findviews();
    }

    private void findviews() {
        myGridView = (GridView) findViewById(R.id.emoji_grid);

        List< EmojiInfo > list = mEmojiResource.getEmojis();
        IpDetailEmjGridAdapter grdiAdapter = new IpDetailEmjGridAdapter(this, list);
        myGridView.setAdapter(grdiAdapter);
//        UiUtils.fixGridViewHeight(myGridView, getResources().getDisplayMetrics().widthPixels);
    }
}
