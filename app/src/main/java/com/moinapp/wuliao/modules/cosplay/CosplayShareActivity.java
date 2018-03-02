package com.moinapp.wuliao.modules.cosplay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.modules.cosplay.listener.CosplayInitEditListener;
import com.moinapp.wuliao.modules.cosplay.model.CosplayResource;
import com.moinapp.wuliao.utils.AppTools;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.InputStream;

public class CosplayShareActivity extends BaseActivity implements View.OnClickListener {


    private static final ILogger MyLog = LoggerFactory.getLogger("CosplayShareActivity");
    protected ImageView []mColorSelect = new ImageView[6];


    protected TextView displayTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cosplay_share);


        initTopBar(getString(R.string.cosplay_shareTitle));
        initLeftBtn(true, R.drawable.back_gray);
        initRightBtn(true, getString(R.string.cosplay_shareSave));


        findViews();


    }

    private void findViews()
    {
        mColorSelect[0] = (ImageView)findViewById(R.id.redButton);
        mColorSelect[1] = (ImageView)findViewById(R.id.yellowButton);
        mColorSelect[2] = (ImageView)findViewById(R.id.greenButton);
        mColorSelect[3] = (ImageView)findViewById(R.id.blueButton);
        mColorSelect[4] = (ImageView)findViewById(R.id.blackButton);
        mColorSelect[5] = (ImageView)findViewById(R.id.whiteButton);

        displayTextView = (TextView)findViewById(R.id.displayText);



        removeEditViewGroup();
        ViewGroup tGroup =  (ViewGroup)findViewById(R.id.activityCosplayShare);

        tGroup.addView(CosplayRuntimeData.getInstance().editViewGroup, 0);


    }

    private  void  removeEditViewGroup()
    {

        ViewGroup tGroup = (ViewGroup)CosplayRuntimeData.getInstance().editViewGroup.getParent();
        if(tGroup != null)
        {
            tGroup.removeView(CosplayRuntimeData.getInstance().editViewGroup);
        }
    }



    public  void  onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.redButton:
                displayTextView.setTextColor(0xFFFc3175);
                break;

            case R.id.yellowButton:
                displayTextView.setTextColor(0xFFFD9B27);
                break;

            case R.id.greenButton:
                displayTextView.setTextColor(0xFF518EE8);
                break;
            case R.id.blueButton:
                displayTextView.setTextColor(0xFF0E56D3);
                break;
            case R.id.blackButton:
                displayTextView.setTextColor(0xFF000000);
                break;
            case R.id.whiteButton:
                displayTextView.setTextColor(0xFFFFFFFF);
                break;

        }
    }

    //拖动//
    private View.OnTouchListener onTouch = new View.OnTouchListener() {
        int startX;
        int startY;

        public  boolean onTouch(View v, MotionEvent event)
        {
            switch( event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    this.startX = (int)event.getRawX();
                    this.startY = (int)event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int newX = (int)event.getRawX();
                    int newY = (int)event.getRawY();

                    int dx = newX - this.startX;
                    int dy = newY - this.startY;
            }
            return true;
        }
    };

    protected  void  leftBtnHandle()
    {
        removeEditViewGroup();
        super.leftBtnHandle();
    }


    protected void rightBtnHandle() {


        //分享//
        InputStream is = CosplayShareActivity.this.getResources().openRawResource(R.raw.zipper);
        CosplayManager.getInstance().exportCosplay(1,1,1,CosplayRuntimeData.getInstance().currentRes.getIpid(),is);
        CosplayRuntimeData.getInstance().editViewGroup = null;

        //save()
    }


}
