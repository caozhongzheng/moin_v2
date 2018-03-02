package com.moinapp.wuliao.modules.wowo;

import android.content.Intent;
import android.os.Bundle;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.commons.ui.photoview.PhotoView;
import com.moinapp.wuliao.modules.wowo.imageloader.ImageLoader;
import com.moinapp.wuliao.utils.StringUtil;

/**
 * Created by moying on 15/6/9.
 */
public class DeleteImageActivity extends BaseActivity {

    public static final String KEY_PATH = "path";
    public static final String KEY_DELETE = "delete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_remove_photo_layout);

        initTopBar(getString(R.string.photo));
        initLeftBtn(true, R.drawable.back_gray);
        initRightBtn(true, getString(R.string.delete));

        PhotoView photoView = (PhotoView) findViewById(R.id.photoview);

        String path = getIntent().getStringExtra(KEY_PATH);
        if(!StringUtil.isNullOrEmpty(path)) {
            ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(path, photoView);
        }
    }

    @Override
    protected void rightBtnHandle() {
        super.rightBtnHandle();
        Intent intent = new Intent();
        intent.putExtra(KEY_DELETE, true);
        setResult(0, intent);
        finish();
    }
}
