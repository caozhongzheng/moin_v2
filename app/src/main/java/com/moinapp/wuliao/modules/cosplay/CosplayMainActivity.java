package com.moinapp.wuliao.modules.cosplay;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.modules.cosplay.listener.CosplayInitEditListener;
import com.moinapp.wuliao.modules.cosplay.model.CosplayResource;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.DisplayUtil;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.io.File;

public class CosplayMainActivity extends BaseActivity implements View.OnClickListener {


    private static final ILogger MyLog = LoggerFactory.getLogger("CosplayMainActivity");

    public static final int CAMERA_PHOTO_WITH_DATA = 1;// 拍照
    public static final int GALLERY_PHOTO_WITH_DATA = 2;// 图库
    public static final int CROP_CAMERA = 3;//裁减


    ImageLoader mImageLoader = null;

    private String capturePath;
    private String cropPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        setContentView(R.layout.cosplay_layout);


        initTopBar(getString(R.string.cosplay_editPhoto));
        initLeftBtn(true, R.drawable.back_gray);
        initRightBtn(true, getString(R.string.cosplay_photoAlbum));

        findViews();

        MyLog.i(CosplayRuntimeData.getInstance().runtimeJson);
        if(CosplayRuntimeData.getInstance().runtimeJson != null)
        {
            AppTools.toIntent(CosplayMainActivity.this, CosplayEditorActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        else
        {

        }
    }

    private void findViews() {

        //ImageView takePhoto = (ImageView)findViewById(R.string.pho );

    }


    public  void  onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.cosplay_cameraButton:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                capturePath = BitmapUtil.getCosplayImagePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(capturePath)));
                startActivityForResult(intent, CAMERA_PHOTO_WITH_DATA);
                break;
        }
    }

    protected void leftBtnHandle() {
        super.leftBtnHandle();
    }


    protected void rightBtnHandle() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 640);
        intent.putExtra("outputY", 640);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, BitmapUtil.getCosplayCropUri());
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra("circleCrop", true);
        startActivityForResult(intent, GALLERY_PHOTO_WITH_DATA);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode)
            {
                case CAMERA_PHOTO_WITH_DATA:

                    if (!AppTools.existsSDCARD()) {
                        MyLog.v("SD card is not avaiable/writeable right now.");
                        return;
                    }

                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(Uri.fromFile(new File(capturePath)), "image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 640);
                    intent.putExtra("outputY", 640);
                    intent.putExtra("scale", true);
                    intent.putExtra("return-data", false);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, BitmapUtil.getCosplayCropUri());
                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    intent.putExtra("noFaceDetection", true); // no face detection
                    intent.putExtra("circleCrop", true);
                    startActivityForResult(intent, CROP_CAMERA);

                    break;
                case GALLERY_PHOTO_WITH_DATA:
                    cropPath = BitmapUtil.getCosplayCropPath();
                    CosplayRuntimeData.getInstance().backGroundBitmap =   com.moinapp.wuliao.modules.wowo.imageloader.ImageLoader.getInstance().decodeSampledBitmapFromResource(
                            cropPath, DisplayUtil.getDisplayWidth(CosplayMainActivity.this), DisplayUtil.dip2px(CosplayMainActivity.this, 300));

                    CosplayManager.getInstance().saveCosplayBackGround();

                    inEditor();



                    break;
                case CROP_CAMERA:
                    cropPath = BitmapUtil.getCosplayCropPath();
                    CosplayRuntimeData.getInstance().backGroundBitmap = com.moinapp.wuliao.modules.wowo.imageloader.ImageLoader.getInstance().decodeSampledBitmapFromResource(
                            cropPath, DisplayUtil.getDisplayWidth(CosplayMainActivity.this), DisplayUtil.dip2px(CosplayMainActivity.this, 300)); //BitmapFactory.decodeFile(capturePath);

                    //MyLog.i("tWidht:" + CosplayRuntimeData.getInstance().backGroundBitmap.getWidth());

                    inEditor();
                    break;
            }

        }
    }



    private void inEditor()
    {
        CosplayManager.getInstance().setCosplayInitEditListener(new CosplayInitEditListener() {
            @Override
            public void initCosplay(CosplayResource cosplayRes) {

                CosplayRuntimeData.getInstance().initResource = cosplayRes;
                AppTools.toIntent(CosplayMainActivity.this, CosplayEditorActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        });

        CosplayManager.getInstance().getips();
    }


}
