package com.moinapp.moin2d;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Environment;

import java.io.InputStream;

/**
 * Created by wufan on 15/7/24.
 */
public class CosplayLogic implements IGameLogic {

    public  void onCreate()
    {

//        Bitmap t1 = Moin2dJni.getBitmapByAsset("t1.png");
        String shuchu = Environment.getExternalStorageDirectory().getAbsolutePath();
         Moin2dJni.addSprite("body","tag",shuchu+"/testZip.zip",1);

    }

    public void onGlViewInit(GLSurfaceView surfaceView)
    {

    }
}
