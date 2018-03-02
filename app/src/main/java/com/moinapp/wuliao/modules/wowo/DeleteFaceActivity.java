package com.moinapp.wuliao.modules.wowo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.gif.GifUtils;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.utils.StringUtil;

import java.io.File;

/**
 * Created by moying on 15/6/9.
 */
public class DeleteFaceActivity extends BaseActivity {

    public static final String KEY_PATH = "path";
    public static final String KEY_DELETE = "delete";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_remove_face_layout);

        initTopBar(getString(R.string.ip_emoji));
        initLeftBtn(true, R.drawable.back_gray);
        initRightBtn(true, getString(R.string.delete));

        ImageView gifView = (ImageView) findViewById(R.id.gifview);

        String path = getIntent().getStringExtra(KEY_PATH);

        if(!StringUtil.isNullOrEmpty(path)) {
            android.util.Log.i("delf", path);
            if(path.startsWith("file://"))
                path = path.substring(7);
            GifUtils.displayGif(gifView, new File(path));
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
