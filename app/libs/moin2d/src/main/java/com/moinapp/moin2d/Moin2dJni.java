package com.moinapp.moin2d;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;

/**
 * Created by wufan on 15/7/15.
 */
public class Moin2dJni {

    static {


        System.loadLibrary("moin2d");
        //隐式调用//
        // System.loadLibrary("jnigraphics");
    }

    public  static Context currentContext = null;

    public  static String getString()
    {
        return "";//stringFromJni();
    }

    //static public native String stringFromJni();



    public static void init(final int pWidth, final int pHeight)
    {
        nativeInit(pWidth,pHeight,currentContext.getAssets());
    }

    private static native void nativeInit(final int pWidth, final int pHeight,AssetManager assetManager);



    public static native void nativeDraw();




    public static native void touchBegin(final float x0, final float y0,final float x0last, final float y0last,
                                         final float x1, final float y1,final float x1Last, final float y1Last);
    public static native void touchEnd(final float x0, final float y0,final float x0last, final float y0last,
                                       final float x1, final float y1,final float x1Last, final float y1Last);
    public static native void touchMove(final float x0, final float y0,final float x0last, final float y0last,
                                        final float x1, final float y1,final float x1Last, final float y1Last);



    public static  Object getBitmapByAsset(String path)
    {

        if(currentContext == null)
        {
            return null;
        }

        try {

            AssetManager assetManager = currentContext.getAssets();
            InputStream is= assetManager.open(path);
            Bitmap bitmap= BitmapFactory.decodeStream(is);
            is.close();

            return bitmap;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {

        }
        return null;

    }

    public static  Object getBitmapByData(byte[] bytes)
    {

        if(currentContext == null)
        {
            return null;
        }

        try {

            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            return bitmap;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {

        }
        return null;

    }


    public  static  native void loadCurrentCosplayGif();


    //添加一个type//
    public static native void addSprite(String type,String tag,String zipFileString,float initScale);


    public static native void deleteSprite(String type);



//    public static native void


}
