package com.moinapp.wuliao.modules.cosplay;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

//import com.moinapp.moin2d.MoinGlView;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.modules.cosplay.listener.CosplayInitEditListener;
import com.moinapp.wuliao.modules.cosplay.model.CosplayResource;
import com.moinapp.wuliao.modules.cosplay.model.CosplayThemeInfo;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//ndk部分//

public class CosplayEditorActivity extends BaseActivity implements CosplayScrollviewH.CosplayScrollViewListener{

    private static final ILogger MyLog = LoggerFactory.getLogger("CosplayEditorActivity");

    LinearLayout mScrollTypeRelative = null;
    LinearLayout mScrollItemRelative = null;
    ViewGroup mainView = null;
    ImageView mDakaXiu = null;

    //itemIcon//
    ImageView mTagEdit = null;

    ////
    View mCurrentTypeView = null;

    ImageView mCosplayArrow = null;


    ImageLoader mImageLoader = null;

    Map<String,List<CosplayThemeInfo>> mCosplayThemeInfoMap = null;

    List<ItemViewHolder> mItemList = new LinkedList<ItemViewHolder>();

//    MoinGlView moinGlView;

    ViewGroup backGroundView = null;
    List<ImageView> mtempEditImageList = new LinkedList<ImageView>();

    CosplayLogic mlogic = null;



    //    private List<CosplayThemeInfo> head;
//    private List<CosplayThemeInfo> body;
//    private List<CosplayThemeInfo> hand;
//    private List<CosplayThemeInfo> arm;
//    private List<CosplayThemeInfo> foot;
//    private List<CosplayThemeInfo> leg;
//    private List<CosplayThemeInfo> feature;
//    private List<CosplayThemeInfo> tool;
//    private List<CosplayThemeInfo> other;
//    private List<CosplayThemeInfo> other1;
//    private List<CosplayThemeInfo> other2;
//    private List<CosplayThemeInfo> other3;

    private Map<String,Integer> mTypeIconMap = new HashMap<String,Integer>(){{
        put("head", R.drawable.cosplay_hair);
        put("body", R.drawable.cosplay_cloth);
        put("hand", R.drawable.cosplay_hand);
        put("arm", R.drawable.cosplay_hand);
        put("foot", R.drawable.cosplay_hand);
        put("leg", R.drawable.cosplay_hand);
        put("feature", R.drawable.cosplay_fx);
        put("tool", R.drawable.cosplay_fx);
        put("other", R.drawable.cosplay_fx);
        put("other1", R.drawable.cosplay_fx);
        put("other2", R.drawable.cosplay_fx);
        put("other3", R.drawable.cosplay_fx);

    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(BitmapUtil.getImageLoaderConfiguration());

        setContentView(R.layout.activity_cosplay_editor);


        CosplayManager.getInstance().setCosplayInitEditListener(new CosplayInitEditListener() {
            @Override
            public void initCosplay(CosplayResource cosplayRes) {
                //    MyLog.i("initCosplay");
                initEditType(cosplayRes);
            }
        });



        initTopBar(getString(R.string.cosplay_expressionTitle));
        initLeftBtn(true, R.drawable.back_gray);
        initRightBtn(true, getString(R.string.cosplay_next));

        findViews();

        if(CosplayRuntimeData.getInstance().initResource != null)
        {
            initEditType(CosplayRuntimeData.getInstance().initResource);
            CosplayRuntimeData.getInstance().initResource = null;
        }
    }


    protected void rightBtnHandle() {

        CosplayRuntimeData.getInstance().editViewGroup = backGroundView;
        AppTools.toIntent(this, CosplayShareActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    void findViews()
    {
        mainView =   (ViewGroup)findViewById(R.id.activityCosplayEditor);

        backGroundView = (ViewGroup)findViewById(R.id.glView);


        mlogic = new CosplayLogic();
//        moinGlView = new MoinGlView(this,mlogic);
        //temp//
        if(CosplayRuntimeData.getInstance().backGroundBitmap != null)
        {
//            backGroundView.addView(moinGlView);




        }


        /********/

        CosplayScrollviewH editScroView = (CosplayScrollviewH)findViewById(R.id.cosplay_editScrollView);

        editScroView.setScrollViewListener(this);

        //HorizontalScrollView tScrollView = (HorizontalScrollView)findViewById(R.id.cosplay_editScrollView);
        mScrollTypeRelative = (LinearLayout) findViewById(R.id.scrollrelative);

        mDakaXiu = (ImageView)mScrollTypeRelative.findViewById(R.id.cosplayDakaXiu);
        mDakaXiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CosplayManager.getInstance().getips();


            }
        });

        mTagEdit = (ImageView)findViewById(R.id.tagEditText);
        mScrollItemRelative = (LinearLayout)findViewById(R.id.scrollcellItemrelative);

        mCosplayArrow = (ImageView)findViewById(R.id.cosplay_arrow);
        mTagEdit.setVisibility(View.INVISIBLE);




    }




    //创建
    private void initEditType(CosplayResource cosplayRes) {


        boolean isSet =  CosplayRuntimeData.getInstance().setCurrentCosplay(cosplayRes);
        if(isSet  == true)
        {
            MyLog.i(cosplayRes.getIconLocalPath());
            mImageLoader.displayImage(cosplayRes.getIcon().getUri(), mTagEdit, BitmapUtil.getImageLoaderOption());
            mTagEdit.setVisibility(View.VISIBLE);

            CosplayRuntimeData.getInstance().setIpid(cosplayRes.getIpid());

            int nCount = mScrollTypeRelative.getChildCount();
            mScrollTypeRelative.removeViews(1, mScrollTypeRelative.getChildCount() - 1);
            mScrollItemRelative.removeAllViews();

            mCosplayThemeInfoMap =  cosplayRes.getThemes().getAllExistInfo();

            String firstKey = null;
            //for(int i= 0;i<4;i++)
            {
                for (Map.Entry<String, List<CosplayThemeInfo>> entry : mCosplayThemeInfoMap.entrySet()) {

                    final String tkey = entry.getKey();
                    // System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());

                    LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View layout = inflater.inflate(R.layout.cosplay_typecell_layout, null);

                    if (firstKey == null) {
                        firstKey = tkey;
                        //默认是第一个View//
                        mCurrentTypeView = layout;
                    }

                    ImageView tImageView = (ImageView) layout.findViewById(R.id.cosplay_type_cell_image);
                    tImageView.setImageDrawable(this.getResources().getDrawable(mTypeIconMap.get(entry.getKey())));
                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mCurrentTypeView = layout;
                            refreshItems(tkey);
                            refreshArrowPos();
                           // refreshArrowPos();
                            //点击View//

                        }
                    });

                    TextView textView = (TextView) layout.findViewById(R.id.cosplay_type_cell_text);
                    textView.setText(entry.getKey());

                    mScrollTypeRelative.addView(layout);
                }
            }

//            MyLog.i("firstRefreshItems:"+firstKey);
            refreshItems(firstKey);
            refreshArrowPos();

            //View变化后调用//
            mainView.post(new Runnable() {
                @Override
                public void run() {
                    refreshArrowPos();
                }
            });



        }




    }


    public   void onScrollChanged(HorizontalScrollView scrollView, int x, int y, int oldx, int oldy)
    {
        refreshArrowPos();
    }

    private void refreshArrowPos()
    {
        if(mCurrentTypeView != null)
        {

            int[] location = new  int[2] ;
            mCurrentTypeView.getLocationInWindow(location);


            //float x = mCurrentTypeView.getX()+ tView.getX();

            mCosplayArrow.setX(location[0] + mCurrentTypeView.getWidth()/2);
            //MyLog.i("location:"+location[0]);

        }
    }



    private void refreshItems(String key)
    {

        boolean isSet =CosplayRuntimeData.getInstance().setCurrentCosplayThemesList(key);
        if(isSet)
        {
            mScrollItemRelative.removeAllViews();

            mItemList.clear();

            ItemViewHolder firstHolder = null;


                for (CosplayThemeInfo info : CosplayRuntimeData.getInstance().currentListInfo) {
                    LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.cosplay_itemcell_layout, null);

                    ItemViewHolder holder = new ItemViewHolder();
                    if (firstHolder == null) {
                        firstHolder = holder;
                    }
                    holder.info = info;
                    holder.itemImage = (ImageView) layout.findViewById(R.id.cosplay_itemImage);
                    holder.selectImage = (ImageView) layout.findViewById(R.id.cosplay_itemSelect);


                    mItemList.add(holder);

                    holder.itemImage.setTag(holder);
                    holder.itemImage.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {

                                                                ItemViewHolder itemHolder = (ItemViewHolder) view.getTag();
                                                                selectItemViewHolder(itemHolder);
                                                            }
                                                        }
                    );

                    if (info.getIcon().getUri() != null) {
                        mImageLoader.displayImage(info.getIcon().getUri(), holder.itemImage, BitmapUtil.getImageLoaderOption());
                    }
                    mScrollItemRelative.addView(layout);

                }

            selectItemViewHolder(firstHolder);
        }
    }
    //当前选中的item//

    private void selectItemViewHolder(ItemViewHolder viewHolder)
    {
        boolean isSelect = CosplayRuntimeData.getInstance().setCurrentCosplayThemeInfo(viewHolder.info);
        if(isSelect)
        {
            for(ItemViewHolder tviewHolder: mItemList)
            {
                tviewHolder.selectImage.setVisibility(View.INVISIBLE);
            }
            if(viewHolder.selectImage != null)
            {
                viewHolder.selectImage.setVisibility(View.VISIBLE);
            }
            if(CosplayRuntimeData.getInstance().currentItemKey == null)
            {
                return;
            }

            for(ImageView itemView:mtempEditImageList)
            {

                final String tag = (String)itemView.getTag();
                if(CosplayRuntimeData.getInstance().currentItemKey.equals(tag))
                {
                    mImageLoader.displayImage(viewHolder.info.getIcon().getUri(), itemView, BitmapUtil.getImageLoaderOption(), new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String s, View view, final  Bitmap bitmap) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mlogic.selectImage(bitmap,"name", CosplayRuntimeData.getInstance().currentItemKey);
                                }
                            });

                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });


                    return;
                }
            }

            ImageView itemView = new ImageView(this);
            itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            itemView.setTag(CosplayRuntimeData.getInstance().currentItemKey);


            mImageLoader.displayImage(viewHolder.info.getIcon().getUri(), itemView, BitmapUtil.getImageLoaderOption(),

                    new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String s, View view, final  Bitmap bitmap) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mlogic.selectImage(bitmap, CosplayRuntimeData.getInstance().currentItemKey,"name");
                                }
                            });

                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });

            //tView.setX();
            //tView.setY(200);
            mtempEditImageList.add(itemView);
            backGroundView.addView(itemView);

        }
    }


    protected void onResume() {
        super.onResume();
        if(CosplayRuntimeData.getInstance().editViewGroup != null)
        {
            if(CosplayRuntimeData.getInstance().editViewGroup.getParent() == null)
            {
                mainView.addView(CosplayRuntimeData.getInstance().editViewGroup);
            }
        }
    }


    class ItemViewHolder
    {
        View layout;
        ImageView itemImage = null;
        ImageView selectImage = null;
        CosplayThemeInfo info = null;

    }


}