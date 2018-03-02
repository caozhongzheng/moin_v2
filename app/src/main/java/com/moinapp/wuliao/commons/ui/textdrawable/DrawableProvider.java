package com.moinapp.wuliao.commons.ui.textdrawable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.TypedValue;

import com.moinapp.wuliao.commons.ApplicationContext;

/**
 * Created by moying on 15/6/25.
 */
public class DrawableProvider {
    public static final int NO_NAVIGATION = -1;
    public static final int SAMPLE_RECT = 1;
    public static final int SAMPLE_ROUND_RECT = 2;
    public static final int SAMPLE_ROUND = 3;
    public static final int SAMPLE_RECT_BORDER = 4;
    public static final int SAMPLE_ROUND_RECT_BORDER = 5;
    public static final int SAMPLE_ROUND_BORDER = 6;
    public static final int SAMPLE_MULTIPLE_LETTERS = 7;
    public static final int SAMPLE_FONT = 8;
    public static final int SAMPLE_SIZE = 9;
    public static final int SAMPLE_ANIMATION = 10;
    public static final int SAMPLE_MISC = 11;

    private static DrawableProvider mInstance;
    private final Context mContext;

    public static synchronized DrawableProvider getInstance() {
        if(mInstance == null) {
            mInstance = new DrawableProvider();
        }

        return mInstance;
    }

    public DrawableProvider() {
        mContext = ApplicationContext.getContext();
    }

    public DrawableProvider(Context context) {
        mContext = context;
    }

    public TextDrawable getRect(String text, int color) {
        return TextDrawable.builder()
                .buildRect(text, color);
    }

    public TextDrawable getRound(String text, int color) {
        return TextDrawable.builder()
                .buildRound(text, mContext.getResources().getColor(color));
    }

    public TextDrawable getRoundRect(String text, int color) {
        return TextDrawable.builder()
                .buildRoundRect(text, mContext.getResources().getColor(color), toPx(10));
    }

    public TextDrawable getRectWithBorder(String text, int color) {
        return TextDrawable.builder()
                .beginConfig()
                .withBorder(toPx(2))
                .endConfig()
                .buildRect(text, mContext.getResources().getColor(color));
    }

    public TextDrawable getRoundWithBorder(String text, int color) {
        return TextDrawable.builder()
                .beginConfig()
                .withBorder(toPx(2))
                .endConfig()
                .buildRound(text, mContext.getResources().getColor(color));
    }

    public TextDrawable getRoundRectWithBorder(String text, int color) {
        return TextDrawable.builder()
                .beginConfig()
                .withBorder(toPx(2))
                .endConfig()
                .buildRoundRect(text, mContext.getResources().getColor(color), toPx(10));
    }

    public TextDrawable getRectWithMultiLetter(String text, int color) {
        return TextDrawable.builder()
                .beginConfig()
                .fontSize(toPx(20))
                .toUpperCase()
                .endConfig()
                .buildRect(text, mContext.getResources().getColor(color));
    }

    public TextDrawable getRoundWithCustomFont(String text) {
//        String text = "Bold";
        return TextDrawable.builder()
                .beginConfig()
                .useFont(Typeface.DEFAULT)
                .fontSize(toPx(15))
                .textColor(0xfff58559)
                .bold()
                .endConfig()
                .buildRect(text, Color.DKGRAY /*toPx(5)*/);
    }

    public Drawable getRectWithCustomSize(String leftText, String rightText, int leftColor, int rightColor) {
//        String leftText = "I";
//        String rightText = "J";

        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .width(toPx(29))
                .withBorder(toPx(2))
                .endConfig()
                .rect();

        TextDrawable left = builder
                .build(leftText, mContext.getResources().getColor(leftColor));

        TextDrawable right = builder
                .build(rightText, mContext.getResources().getColor(rightColor));

        Drawable[] layerList = {
                new InsetDrawable(left, 0, 0, toPx(31), 0),
                new InsetDrawable(right, toPx(31), 0, 0, 0)
        };
        return new LayerDrawable(layerList);
    }

    public Drawable getRectWithAnimation(int color) {
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .rect();

        AnimationDrawable animationDrawable = new AnimationDrawable();
        for (int i = 10; i > 0; i--) {
            TextDrawable frame = builder.build(String.valueOf(i), mContext.getResources().getColor(color));
            animationDrawable.addFrame(frame, 1200);
        }
        animationDrawable.setOneShot(false);
        animationDrawable.start();

        return animationDrawable;
    }

    public int toPx(int dp) {
        Resources resources = mContext.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    public DataItem getDataItem(String s, int color, int type) {
        Drawable drawable = null;
        switch (type) {
            case DrawableProvider.SAMPLE_RECT:
                drawable = mInstance.getRect(s, color);
                break;
            case DrawableProvider.SAMPLE_ROUND_RECT:
                drawable = mInstance.getRoundRect(s, color);
                break;
            case DrawableProvider.SAMPLE_ROUND:
                drawable = mInstance.getRound(s, color);
                break;
            case DrawableProvider.SAMPLE_RECT_BORDER:
                drawable = mInstance.getRectWithBorder(s, color);
                break;
            case DrawableProvider.SAMPLE_ROUND_RECT_BORDER:
                drawable = mInstance.getRoundRectWithBorder(s, color);
                break;
            case DrawableProvider.SAMPLE_ROUND_BORDER:
                drawable = mInstance.getRoundWithBorder(s, color);
                break;
            case DrawableProvider.SAMPLE_MULTIPLE_LETTERS:
                drawable = mInstance.getRectWithMultiLetter(s, color);
                type = NO_NAVIGATION;
                break;
            case DrawableProvider.SAMPLE_FONT:
                drawable = mInstance.getRoundWithCustomFont(s);
                type = NO_NAVIGATION;
                break;
            case DrawableProvider.SAMPLE_SIZE:
                drawable = mInstance.getRectWithCustomSize(s,s,color,color);
                type = NO_NAVIGATION;
                break;
            case DrawableProvider.SAMPLE_ANIMATION:
                drawable = mInstance.getRectWithAnimation(color);
                type = NO_NAVIGATION;
                break;
            case DrawableProvider.SAMPLE_MISC:
                drawable =  mInstance.getRect("\u03c0", color);
                type = NO_NAVIGATION;
                break;
        }
        return new DataItem(s, drawable, type);
    }
}
