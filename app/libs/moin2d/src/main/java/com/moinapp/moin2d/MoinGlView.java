package com.moinapp.moin2d;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MoinGlView extends GLSurfaceView {

    private static String TAG = "MoinGlView";
    private static final boolean DEBUG = false;

    private static MoinGlView mGlVIew;
    private MoinRenderer mRender = null;

    private float mLastX = -1;
    private float mLastY = -1;
    private IGameLogic mlogic = null;
    public MoinGlView(Context context,IGameLogic gameLogic) {
        super(context);
        Moin2dJni.currentContext = context;
        mlogic = gameLogic;
//        Bitmap touchFrame = Moin2dJni.getBitmapByAsset("cosplay_bianjikuang.png");
//        float a = touchFrame.getWidth();
//        Bitmap Bg = Moin2dJni.getBitmapByAsset("cosplay_dash.png");
//        float b = Bg.getWidth();
//        Moin2dJni.setImage(touchFrame, Bg);

        initView();
    }

    public void initView() {

        this.setEGLContextClientVersion(2);
        this.setFocusableInTouchMode(true);

        /* By default, GLSurfaceView() creates a RGB_565 opaque surface.
         * If we want a translucent one, we should change the surface's
         * format here, using PixelFormat.TRANSLUCENT for GL Surfaces
         * is interpreted as any 32-bit surface with alpha by SurfaceFlinger.
         */

        MoinGlView.mGlVIew = this;
        /* Setup the context factory for 2.0 rendering.
         * See ContextFactory class definition below
         */
        //setEGLContextFactory(new ContextFactory());

        this.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);

        /* Set the renderer responsible for frame rendering */
        mlogic.onGlViewInit(this);
        mRender = new MoinRenderer();
        mRender.setLogic(mlogic);
        setRenderer(mRender);


        this.queueEvent(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    public static MoinGlView getInstance() {
        return mGlVIew;
    }

    @Override
    public void onResume() {
        super.onResume();

        this.queueEvent(new Runnable() {
            @Override
            public void run() {
                //Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleOnResume();
            }
        });
    }

    @Override
    public void onPause() {
        this.queueEvent(new Runnable() {
            @Override
            public void run() {
                //Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleOnPause();
            }
        });

        //super.onPause();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent pMotionEvent) {
        Log.i(TAG, "onTouchEvent....");
        // these data are used in ACTION_MOVE and ACTION_CANCEL
        final int pointerNumber = pMotionEvent.getPointerCount();
        final int[] ids = new int[pointerNumber];
        final float[] xs = new float[pointerNumber];
        final float[] ys = new float[pointerNumber];

        for (int i = 0; i < pointerNumber; i++) {
            ids[i] = pMotionEvent.getPointerId(i);
            xs[i] = pMotionEvent.getX(i);
            ys[i] = pMotionEvent.getY(i);
        }

        switch (pMotionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                final int indexPointerDown = pMotionEvent.getAction() >> MotionEvent.ACTION_POINTER_ID_SHIFT;
                final int idPointerDown = pMotionEvent.getPointerId(indexPointerDown);
                final float xPointerDown = pMotionEvent.getX(indexPointerDown);
                final float yPointerDown = pMotionEvent.getY(indexPointerDown);

                this.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        //Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleActionDown(idPointerDown, xPointerDown, yPointerDown);
                    }
                });
                break;

            case MotionEvent.ACTION_DOWN://按下
                // there are only one finger on the screen
//                Log.i(TAG,"MotionEvent.ACTION_DOWN....");
//                Log.i(TAG, "MotionEvent.ACTION_DOWN:x0=" + xs[0] + "y0=" + ys[0]);
//                if  (pointerNumber > 1) {
//                    Log.i(TAG, "MotionEvent.ACTION_DOWN:x1=" + xs[1] + "y1=" + ys[1]);
//                }
                mLastX = xs[0];
                mLastY = ys[0];
                this.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        //Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleActionDown(idDown, xDown, yDown);
                        Moin2dJni.touchBegin(xs[0], ys[0], pointerNumber > 1 ? xs[1] : -1, pointerNumber > 1 ? ys[1] : -1, -1, -1, -1, -1);
                    }
                });
                break;

            case MotionEvent.ACTION_MOVE://移动
//                Log.i(TAG,"MotionEvent.ACTION_MOVE....");
//                Log.i(TAG, "MotionEvent.ACTION_MOVE:x0=" + xs[0] + "y0=" + ys[0]);
//                if  (pointerNumber > 1) {
//                    Log.i(TAG, "MotionEvent.ACTION_MOVE:x1=" + xs[1] + "y1=" + ys[1]);
//                }
                //Log.i(TAG, "MotionEvent.ACTION_MOVE:mLastX=" + mLastX + "mLastY=" + mLastY);
                if ((Math.abs(xs[0] - mLastX) > 0) || (Math.abs(ys[0] - mLastY) > 0)) {
                    this.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "x0=" + xs[0] + "y0=" + ys[0]+";lastx:"+mLastX+";lasty:"+mLastY);
                            //Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleActionDown(idDown, xDown, yDown);
                            Moin2dJni.touchMove(xs[0], ys[0], mLastX, mLastY, -1, -1, -1, -1);//pointerNumber > 1 ? xs[1] : -1, pointerNumber > 1 ? ys[1] : -1);
                            mLastX = xs[0];
                            mLastY = ys[0];
                        }
                    });
                    Log.i(TAG, "11111x0=" + xs[0] + "y0=" + ys[0] + ";lastx:" + mLastX + ";lasty:" + mLastY);
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                final int indexPointUp = pMotionEvent.getAction() >> MotionEvent.ACTION_POINTER_ID_SHIFT;
                final int idPointerUp = pMotionEvent.getPointerId(indexPointUp);
                final float xPointerUp = pMotionEvent.getX(indexPointUp);
                final float yPointerUp = pMotionEvent.getY(indexPointUp);

                this.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        //Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleActionUp(idPointerUp, xPointerUp, yPointerUp);
                    }
                });
                break;

            case MotionEvent.ACTION_UP://手指松开，按下的动作结束
                // there are only one finger on the screen
                Log.i(TAG,"MotionEvent.ACTION_UP....");
                Log.i(TAG, "MotionEvent.ACTION_UP:x0=" + xs[0] + "y0=" + ys[0]);
                if  (pointerNumber > 1) {
                    Log.i(TAG, "MotionEvent.ACTION_UP:x1=" + xs[1] + "y1=" + ys[1]);
                }
                this.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        //Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleActionDown(idDown, xDown, yDown);
                        // Moin2dJni.touchEnd(xs[0], ys[0], pointerNumber > 1 ? xs[1] : -1, pointerNumber > 1 ? ys[1] : -1);
                    }
                });
                break;

            case MotionEvent.ACTION_CANCEL://手指松开，与ACTION_UP的区别是不用做任何处理，意味着动作的取消
                this.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        //Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleActionCancel(ids, xs, ys);
                    }
                });
                break;
        }

        /*
		if (BuildConfig.DEBUG) {
			Cocos2dxGLSurfaceView.dumpMotionEvent(pMotionEvent);
		}
		*/
        return true;
    }

    /*
     * This function is called before Cocos2dxRenderer.nativeInit(), so the
     * width and height is correct.
     */
    @Override
    protected void onSizeChanged(final int pNewSurfaceWidth, final int pNewSurfaceHeight, final int pOldSurfaceWidth, final int pOldSurfaceHeight) {
        // if(!this.isInEditMode()) {
        this.mRender.setScreenWidthAndHeight(pNewSurfaceWidth,pNewSurfaceHeight);
        //this.mCocos2dxRenderer.setScreenWidthAndHeight(pNewSurfaceWidth, pNewSurfaceHeight);
        //}
    }

    @Override
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pKeyEvent) {
        switch (pKeyCode) {
            case KeyEvent.KEYCODE_BACK:
                return super.onKeyDown(pKeyCode, pKeyEvent);
            case KeyEvent.KEYCODE_MENU:
                this.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        //Cocos2dxGLSurfaceView.this.mCocos2dxRenderer.handleKeyDown(pKeyCode);
                    }
                });
                return true;
            default:
                return super.onKeyDown(pKeyCode, pKeyEvent);
        }
    }




    private static class Renderer implements GLSurfaceView.Renderer {
        public void onDrawFrame(GL10 gl) {
            //GL2JNILib.step();
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //GL2JNILib.init(width, height);
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // Do nothing.
        }
    }
}
