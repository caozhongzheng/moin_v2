package com.moinapp.wuliao.commons.ui.menu;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
final class ViewHelper {

    private ViewHelper() {
    }

    public static int getLeft(View v) {
        if (MenuDrawer.USE_TRANSLATIONS) {
            return (int) (v.getLeft() + v.getTranslationX());
        }

        return v.getLeft();
    }

    public static int getTop(View v) {
        if (MenuDrawer.USE_TRANSLATIONS) {
            return (int) (v.getTop() + v.getTranslationY());
        }

        return v.getTop();
    }

    public static int getRight(View v) {
        if (MenuDrawer.USE_TRANSLATIONS) {
            return (int) (v.getRight() + v.getTranslationX());
        }

        return v.getRight();
    }

    public static int getBottom(View v) {
        if (MenuDrawer.USE_TRANSLATIONS) {
            return (int) (v.getBottom() + v.getTranslationY());
        }

        return v.getBottom();
    }
}