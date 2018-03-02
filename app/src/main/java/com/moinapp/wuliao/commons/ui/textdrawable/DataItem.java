package com.moinapp.wuliao.commons.ui.textdrawable;

import android.graphics.drawable.Drawable;

/**
 * Created by moying on 15/6/25.
 */
public class DataItem {
    private String label;

    private Drawable drawable;

    private int navigationInfo;

    public DataItem(String label, Drawable drawable, int navigationInfo) {
        this.label = label;
        this.drawable = drawable;
        this.navigationInfo = navigationInfo;
    }

    public String getLabel() {
        return label;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public int getNavigationInfo() {
        return navigationInfo;
    }
}
