package com.moinapp.wuliao.modules.wowo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.gif.GifUtils;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/**
 * Created by moying on 15/6/10.
 */
public class GifDialog extends Dialog {
    private Context context;
    private ImageView imageView;
    private ImageLoader imageLoader;
    private String imagePath;

    public GifDialog(Context context, int theme, String path) {
        super(context, theme);

        this.context = context;

        if(path.startsWith("file://"))
            this.imagePath = path.substring(7);

        imageLoader = ImageLoader.getInstance();
        if(!imageLoader.isInited()) {
            imageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_gif_layout);

        imageView = (ImageView) findViewById(R.id.iv_gif);
        GifUtils.displayGif(imageView, new File(imagePath));
    }
}
