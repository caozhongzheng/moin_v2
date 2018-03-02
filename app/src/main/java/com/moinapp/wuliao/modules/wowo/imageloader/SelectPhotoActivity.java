package com.moinapp.wuliao.modules.wowo.imageloader;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.utils.ToastUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by moying on 15/6/11.
 */
public class SelectPhotoActivity extends BaseActivity implements ListImageDirPopupWindow.OnImageDirSelected {

    private ILogger MyLog = LoggerFactory.getLogger("sp");

    public static final String KEY_SELECTED = "selected";
    public static final String KEY_IS_CHANGED = "ischange";

    private ProgressDialog mProgressDialog;

    /**
     * 存储文件夹中的图片数量
     */
    private int mPicsSize;
    /**
     * 图片数量最多的文件夹
     */
    private File mImgDir;
    /**
     * 所有的图片
     */
    private List<String> mImgs;

    /**
     * 已选择的图片
     */
    private ArrayList<String> mSelectImgs;

    private GridView mGirdView;
    private MyImageAdapter mAdapter;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();

    /**
     * 扫描拿到所有的图片文件夹
     */
    private List<ImageFolder> mImageFloders = new ArrayList<ImageFolder>();

    private RelativeLayout mBottomLy;

    private TextView mChooseDir;
    private TextView mImageCount;
    private TextView mSelectCount;
    int totalCount = 0;
    boolean changed = false;

    private int mScreenHeight;

    private int mPhotoMax;

    private ListImageDirPopupWindow mListImageDirPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);

        initTopBar(getString(R.string.wowo_photos));
        initLeftBtn(true, R.drawable.back_gray);
        initRightBtn(true, getString(R.string.select_ok));

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;

        mPhotoMax = getResources().getInteger(R.integer.post_photo_max_count);

        mSelectImgs = getIntent().getStringArrayListExtra(KEY_SELECTED);
        if(mSelectImgs == null) {
            mSelectImgs = new ArrayList<>();
        }

        initView();
        getImages();
        initEvent();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mGirdView = (GridView) findViewById(R.id.id_gridView);
        mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
        mImageCount = (TextView) findViewById(R.id.id_total_count);
        mSelectCount = (TextView) findViewById(R.id.id_select_count);
        if(mSelectImgs != null && mSelectImgs.size() > 0) {
            mSelectCount.setText(mSelectImgs.size() + "/" + mPhotoMax);
        }
        else {
            mSelectCount.setText("0/" + mPhotoMax);
        }
        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
    }

    private void initEvent() {
        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        mChooseDir.setOnClickListener(chooseDirListener);
        mImageCount.setOnClickListener(chooseDirListener);
//        mSelectCount.setOnClickListener(selectOkListener);
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示进度条
        mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

        new Thread(new Runnable() {
            @Override
            public void run() {

                String firstImage = null;

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = SelectPhotoActivity.this
                        .getContentResolver();

                // 只查询jpeg,webp和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png", "image/webp"},
                        MediaStore.Images.Media.DATE_MODIFIED);

//                Log.e("TAG", mCursor.getCount() + "");
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

//                    Log.e("TAG", path);
                    // 拿到第一张图片的路径
                    if (firstImage == null)
                        firstImage = path;
                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();
                    ImageFolder imageFloder = null;
                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        imageFloder = new ImageFolder();
                        imageFloder.setDir(dirPath);
                        imageFloder.setFirstImagePath(path);
                    }

                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            return filename.toLowerCase().endsWith(".jpg")
                                    || filename.endsWith(".jpeg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".webp");
                        }
                    }).length;
                    totalCount += picSize;

                    imageFloder.setCount(picSize);
                    MyLog.i("扫描完了:"+imageFloder.toString());
                    mImageFloders.add(imageFloder);

                    if (picSize > mPicsSize) {
                        mPicsSize = picSize;
                        mImgDir = parentFile;
                    }
                }
                mCursor.close();

                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;

                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110);

            }
        }).start();

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x119:
                    ToastUtils.toast(SelectPhotoActivity.this, String.format(getResources().getString(R.string.select_photo_warning), mPhotoMax));
                    break;

                case 0x110:
                    mProgressDialog.dismiss();
                    // 为View绑定数据
                    data2View();
                    // 初始化展示文件夹的popupWindw
                    initListDirPopupWindw();
                    break;
                default:
                    break;
            }
        }
    };

    private OnClickListener chooseDirListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mListImageDirPopupWindow
                    .setAnimationStyle(R.style.popwin_anim_style);
            mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

            // 设置背景颜色变暗
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = .3f;
            getWindow().setAttributes(lp);
        }
    };

    private OnClickListener selectOkListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            selectOK();
        }
    };

    private void selectOK() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(KEY_SELECTED, MyImageAdapter.getSelectedImage());
        intent.putExtra(KEY_IS_CHANGED, changed);
        setResult(0, intent);
        finish();
    }

    /**
     * 为View绑定数据
     */
    private void data2View() {
        if (mImgDir == null) {
            ToastUtils.toast(getApplicationContext(), R.string.no_photo);
            return;
        }

        mImgs = Arrays.asList(mImgDir.list());
        setAdapter();
    }


    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw() {
        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
                mImageFloders, LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.imgloader_list_dir, null));

        mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    @Override
    public void selected(ImageFolder floder) {

        mImgDir = new File(floder.getDir());
        mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase().endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg") || filename.endsWith(".webp");
//                        || filename.endsWith(".WEBP") || filename.endsWith(".PNG")
//                        || filename.endsWith(".JEPG") || filename.endsWith(".JPG")
            }
        }));

        setAdapter();

        mChooseDir.setText(floder.getName());
        mListImageDirPopupWindow.dismiss();
    }

    private void setAdapter() {
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mAdapter = new MyImageAdapter(getApplicationContext(), mImgs,
                R.layout.imgloader_grid_item, mImgDir.getAbsolutePath(), mHandler);
        MyImageAdapter.setSelectedImage(mSelectImgs);
        mAdapter.setTextCallback(new MyImageAdapter.TextCallback() {
            @Override
            public void onListen(int count) {
                mSelectCount.setText(count + "/" + mPhotoMax);
//                mSelectCount.setClickable(true);
                changed = true;
            }
        });
        mGirdView.setAdapter(mAdapter);
        mImageCount.setText(totalCount + "张");
    }

    @Override
    protected void rightBtnHandle() {
        super.rightBtnHandle();
        selectOK();
//        clearSelected();
    }

    private void clearSelected() {
        if(mSelectImgs.size() > 0) {
            mSelectImgs.clear();
            changed = true;
            setAdapter();
            mSelectCount.setText("0/" + mPhotoMax);
        }

    }
}
