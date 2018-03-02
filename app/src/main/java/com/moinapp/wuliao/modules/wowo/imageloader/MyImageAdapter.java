package com.moinapp.wuliao.modules.wowo.imageloader;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.modules.wowo.WowoContants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moying on 15/6/11.
 */
public class MyImageAdapter extends CommonAdapter<String> {
    /**
     * 用户选择的图片，存储为图片的完整路径
     */
    public static ArrayList<String> mSelectedImage = new ArrayList<String>();

    static int count;

    /**
     * 文件夹路径
     */
    private String mDirPath;
    private Handler mHandler;

    public MyImageAdapter(Context context, List<String> mDatas, int itemLayoutId,
                     String dirPath, Handler handler) {
        super(context, mDatas, itemLayoutId);
        this.mDirPath = dirPath;
        this.mHandler = handler;
    }

    @Override
    public void convert(final ViewHolder helper, final String item) {
        //设置no_pic
        helper.setImageResource(R.id.id_item_image, R.drawable.pictures_no);
        //设置no_selected
        helper.setImageResource(R.id.id_item_select, R.drawable.picture_unselected);
        //设置图片
        helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);

        final ImageView mImageView = helper.getView(R.id.id_item_image);
        final ImageView mSelect = helper.getView(R.id.id_item_select);

        mImageView.setColorFilter(null);
        /**
         * 已经选择过的图片，显示出选择过的效果
         */
        if (mSelectedImage.contains(mDirPath + "/" + item)) {
            mSelect.setImageResource(R.drawable.pictures_selected);
            mImageView.setColorFilter(Color.parseColor("#77000000"));
        }

        mImageView.setOnClickListener(new OnClickListener() {
            //选择，则将图片变暗，反之则反之
            @Override
            public void onClick(View v) {

                // 已经选择过该图片
                if (mSelectedImage.contains(mDirPath + "/" + item)) {
                    mSelectedImage.remove(mDirPath + "/" + item);
                    count--;
                    mSelect.setImageResource(R.drawable.picture_unselected);
                    mImageView.setColorFilter(null);
                    if (textcallback != null)
                        textcallback.onListen(count);
                } else
                // 未选择该图片
                {
                    if(count >= WowoContants.WO_POST_MAX_SIZE) {
                        Message message = Message.obtain(mHandler, 0x119);
                        message.sendToTarget();
                    } else {
                        mSelectedImage.add(mDirPath + "/" + item);
                        count++;
                        mSelect.setImageResource(R.drawable.pictures_selected);
                        mImageView.setColorFilter(Color.parseColor("#77000000"));
                        if (textcallback != null)
                            textcallback.onListen(count);
                    }
                }

            }
        });

    }


    public static ArrayList<String> getSelectedImage() {
        return mSelectedImage;
    }

    public static void setSelectedImage(ArrayList<String> mSelectedImage) {
        MyImageAdapter.mSelectedImage = mSelectedImage;
        count = mSelectedImage.size();
    }

    private TextCallback textcallback = null;
    public static interface TextCallback {
        public void onListen(int count);
    }

    public void setTextCallback(TextCallback listener) {
        textcallback = listener;
    }

}
