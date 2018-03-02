package com.moinapp.wuliao.modules.cosplay;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.ToastUtils;

/**
 * Created by liujiancheng on 15/7/22.
 */
public class CosplayDragActivity extends BaseActivity implements View.OnTouchListener {
    private static final ILogger MyLog = LoggerFactory.getLogger("CosplayDragActivity");

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    DisplayMetrics dm;
    ImageView imgView;
    View line1, line2;
    Bitmap bitmap;
    float minScaleR;// 最小缩放比例
    static final float MAX_SCALE = 4f;// 最大缩放比例
    static final int NONE = 0;// 初始状态
    static final int DRAG = 1;// 拖动
    static final int ZOOM = 2;// 缩放
    int mode = NONE;
    PointF prev = new PointF();
    PointF mid = new PointF();
    float dist = 1f;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cosplay_drag);

        imgView = (ImageView) findViewById(R.id.imageView);// 获取控件
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        initTopBar("编辑图片");
        initLeftBtn(true, R.drawable.back_gray);
        initRightBtn(true, getString(R.string.cosplay_next));

        //===================image来自?=================================
        //暂时不清楚图片来源，请自行修改
        String path = BitmapUtil.BITMAP_CACHE + "2.jpg";
        MyLog.i("path="+path);
        bitmap = BitmapFactory.decodeFile(path);
        if (bitmap != null)
            MyLog.i("bitmap is not null");
        else
            MyLog.i("bitmap == null!!!");
        //===============================================================

        imgView.setImageBitmap(bitmap);// 填充控件
        imgView.setOnTouchListener(this);// 设置触屏监听
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);// 获取分辨率
        minZoom();
//        center();
        imgView.setImageMatrix(matrix);


    }


    @Override
    protected void leftBtnHandle() {
        finish();
    }

    @Override
    protected void rightBtnHandle() {
        MyLog.i("right button click....");
        View view = CosplayDragActivity.this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        bitmap = view.getDrawingCache();

        if (bitmap != null) {
            String screen = BitmapUtil.BITMAP_CACHE + "screen.jpg";
            int[] location1 = new int[2];
            line1.getLocationOnScreen(location1);
            MyLog.i("location1.x="+location1[0] + "location.y="+location1[1]);

            int[] location2 = new int[2];
            line2.getLocationOnScreen(location2);
            MyLog.i("location2.x=" + location2[0] + "location.y=" + location2[1]);

            MyLog.i("clipped region width=" + getResources().getDisplayMetrics().widthPixels + ",height=" + (location2[1]-location1[1]));
            bitmap = Bitmap.createBitmap(bitmap, location1[0], location1[1], getResources().getDisplayMetrics().widthPixels, location2[1]-location1[1]);
            boolean ret = BitmapUtil.saveBitmap2file(bitmap, screen, Bitmap.CompressFormat.JPEG, 100);
            if (ret) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toast(CosplayDragActivity.this, "保存截屏成功");
                    }
                });
            }
//            bitmap = Bitmap.createBitmap(bitmap, x, y+2*toHeight, width, height);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(bitmap != null){
            if(!bitmap.isRecycled()){
                bitmap.recycle();   //回收图片所占的内存
                bitmap = null;
                System.gc();  //提醒系统及时回收
            }
        }
    }

    /**
     * 触屏监听
     */
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            // 主点按下
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                prev.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            // 副点按下
            case MotionEvent.ACTION_POINTER_DOWN:
                dist = spacing(event);
                // 如果连续两点距离大于10，则判定为多点模式
                if (spacing(event) > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - prev.x, event.getY()
                            - prev.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float tScale = newDist / dist;
                        matrix.postScale(tScale, tScale, mid.x, mid.y);
                    }
                }
                break;
        }
        imgView.setImageMatrix(matrix);
        CheckView();
        return true;
    }
    /**
     * 限制最大最小缩放比例，自动居中
     */
    private void CheckView() {
        float p[] = new float[9];
        matrix.getValues(p);
        if (mode == ZOOM) {
            if (p[0] < minScaleR) {
                matrix.setScale(minScaleR, minScaleR);
            }
            if (p[0] > MAX_SCALE) {
                matrix.set(savedMatrix);
            }
        }
//        center();
    }
    /**
     * 最小缩放比例，最大为100%
     */
    private void minZoom() {
        minScaleR = Math.min(
                (float) dm.widthPixels / (float) bitmap.getWidth(),
                (float) dm.heightPixels / (float) bitmap.getHeight());
        if (minScaleR < 1.0) {
            matrix.postScale(minScaleR, minScaleR);
        }
    }
    private void center() {
        center(true, true);
    }
    /**
     * 横向、纵向居中
     */
    protected void center(boolean horizontal, boolean vertical) {
        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        m.mapRect(rect);
        float height = rect.height();
        float width = rect.width();
        float deltaX = 0, deltaY = 0;
        if (vertical) {
            // 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下放留空则往下移
            int screenHeight = dm.heightPixels;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = imgView.getHeight() - rect.bottom;
            }
        }
        if (horizontal) {
            int screenWidth = dm.widthPixels;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }
    /**
     * 两点的距离
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
    /**
     * 两点的中点
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}