package com.moinapp.wuliao.modules.cosplay;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;

//import com.moinapp.moin2d.IGameLogic;
//import com.moinapp.moin2d.Moin2dJni;

/**
 * Created by wufan on 15/7/24.
 */
public class CosplayLogic
//        implements IGameLogic
{


    private GLSurfaceView _surfaceVIew = null;
    public  void onCreate()
    {
//        Bitmap touchFrame = Moin2dJni.getBitmapByAsset("cosplay_bianjikuang.png");
//        Bitmap bg = Moin2dJni.getBitmapByAsset("cosplay_dash.png");
//        Moin2dJni.setImage(touchFrame, CosplayRuntimeData.getInstance().backGroundBitmap);
//        touchFrame.recycle();
//        bg.recycle();
    }

    public  void onGlViewInit(GLSurfaceView surfaceView)
    {
        _surfaceVIew = surfaceView;
    }


  //  Bitmap t1 = Moin2dJni.getBitmapByAsset("t1.png");
    //Moin2dJni.addSprite(t1, "body");
     public void selectImage(final Bitmap image,final String name,final String tag)
     {
         //Bitmap bg = Moin2dJni.getBitmapByAsset("cosplay_dash.png");
//         if(_surfaceVIew != null) {
//             _surfaceVIew.queueEvent(new Runnable() {
//                 @Override
//                 public void run() {
//                     Moin2dJni.addSprite(image, name,tag,1);
//                 }
//             });
//         }

     }


}
