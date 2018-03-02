package com.moinapp.wuliao.modules.cosplay;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

public class CosplayScrollviewH extends HorizontalScrollView {


    private static final ILogger MyLog = LoggerFactory.getLogger("CosplayEditorActivity");
    private CosplayScrollViewListener scrollViewListener = null;

    public CosplayScrollviewH(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public CosplayScrollviewH(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CosplayScrollviewH(Context context) {
        super(context);

    }


    public void setScrollViewListener(CosplayScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        MyLog.i("scrollviewX:"+x+",Y:"+y);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    public interface CosplayScrollViewListener {
          void onScrollChanged(HorizontalScrollView scrollView, int x, int y, int oldx, int oldy);
    }

}
