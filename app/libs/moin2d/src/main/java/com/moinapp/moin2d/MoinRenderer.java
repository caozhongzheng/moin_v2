package com.moinapp.moin2d;



import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MoinRenderer implements GLSurfaceView.Renderer {
    // ===========================================================
    // Constants
    // ===========================================================

    private final static long NANOSECONDSPERSECOND = 1000000000L;
    private final static long NANOSECONDSPERMICROSECOND = 1000000;

    private static long sAnimationInterval = (long) (1.0 / 60 * MoinRenderer.NANOSECONDSPERSECOND);

    private IGameLogic mlogic = null;
    // ===========================================================
    // Fields
    // ===========================================================

    private long mLastTickInNanoSeconds;
    private int mScreenWidth;
    private int mScreenHeight;
    private Context mContext = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public static void setAnimationInterval(final double pAnimationInterval) {
        MoinRenderer.sAnimationInterval = (long) (pAnimationInterval * MoinRenderer.NANOSECONDSPERSECOND);
    }

    public  void setLogic(IGameLogic logic)
    {
        mlogic = logic;
    }

    public void setScreenWidthAndHeight(final int pSurfaceWidth, final int pSurfaceHeight) {
        this.mScreenWidth = pSurfaceWidth;
        this.mScreenHeight = pSurfaceHeight;
    }

    public void setCurrentContext(Context context)
    {
        mContext = context;
    }


    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void onSurfaceCreated(final GL10 pGL10, final EGLConfig pEGLConfig) {



        Moin2dJni.init(this.mScreenWidth, this.mScreenHeight);
        this.mLastTickInNanoSeconds = System.nanoTime();

        if(mlogic != null)
        {
            mlogic.onCreate();
        }

    }



    @Override
    public void onSurfaceChanged(final GL10 pGL10, final int pWidth, final int pHeight) {
    }

    @Override
    public void onDrawFrame(final GL10 gl) {
		/*
		 * FPS controlling algorithm is not accurate, and it will slow down FPS
		 * on some devices. So comment FPS controlling code.
		 */

		/*
		final long nowInNanoSeconds = System.nanoTime();
		final long interval = nowInNanoSeconds - this.mLastTickInNanoSeconds;
		*/

        // should render a frame when onDrawFrame() is called or there is a
        // "ghost"
        Moin2dJni.nativeDraw();


		/*
		// fps controlling
		if (interval < Cocos2dxRenderer.sAnimationInterval) {
			try {
				// because we render it before, so we should sleep twice time interval
				Thread.sleep((Cocos2dxRenderer.sAnimationInterval - interval) / Cocos2dxRenderer.NANOSECONDSPERMICROSECOND);
			} catch (final Exception e) {
			}
		}

		this.mLastTickInNanoSeconds = nowInNanoSeconds;
		*/
    }

    // ===========================================================
    // Methods
    // ===========================================================

//    private static native void nativeTouchesBegin(final int pID, final float pX, final float pY);
//    private static native void nativeTouchesEnd(final int pID, final float pX, final float pY);
//    private static native void nativeTouchesMove(final int[] pIDs, final float[] pXs, final float[] pYs);
//    private static native void nativeTouchesCancel(final inFramet[] pIDs, final float[] pXs, final float[] pYs);
//    private static native boolean nativeKeyDown(final int pKeyCode);

//    private static native void nativeOnPause();
//    private static native void nativeOnResume();
//
//    public void handleActionDown(final int pID, final float pX, final float pY) {
//        MoinRenderer.nativeTouchesBegin(pID, pX, pY);
//    }
//
//    public void handleActionUp(final int pID, final float pX, final float pY) {
//        MoinRenderer.nativeTouchesEnd(pID, pX, pY);
//    }
//
//    public void handleActionCancel(final int[] pIDs, final float[] pXs, final float[] pYs) {
//        MoinRenderer.nativeTouchesCancel(pIDs, pXs, pYs);
//    }
//
//    public void handleActionMove(final int[] pIDs, final float[] pXs, final float[] pYs) {
//        MoinRenderer.nativeTouchesMove(pIDs, pXs, pYs);
//    }
//
//    public void handleKeyDown(final int pKeyCode) {
//        MoinRenderer.nativeKeyDown(pKeyCode);
//    }
//
//    public void handleOnPause() {
//        MoinRenderer.nativeOnPause();
//    }
//
//    public void handleOnResume() {
//        MoinRenderer.nativeOnResume();
//    }
//
//    private static native void nativeInsertText(final String pText);
//    private static native void nativeDeleteBackward();
//    private static native String nativeGetContentText();
//
//    public void handleInsertText(final String pText) {
//     //   MoinRenderer.nativeInsertText(pText);
//    }
//
//    public void handleDeleteBackward() {
//        //MoinRenderer.nativeDeleteBackward();
//    }
//
//    public String getContentText() {
//        return MoinRenderer.nativeGetContentText();
//    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
