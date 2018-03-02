package com.moinapp.wuliao.modules.ipresource;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moinsocialcenter.MoinSocialConstants;
import com.moinapp.wuliao.commons.moinsocialcenter.MoinSocialFactory;
import com.moinapp.wuliao.commons.moinsocialcenter.UmengSocialCenter;
import com.moinapp.wuliao.commons.preference.CommonsPreference;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.commons.ui.photoview.PhotoView;
import com.moinapp.wuliao.commons.ui.photoview.PhotoViewAttacher;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by moying on 15/6/2.
 * 查看剧照的ViewPager
 */
public class StillsViewPagerActivity extends BaseActivity {
    private static final ILogger MyLog = LoggerFactory.getLogger("still");
    private static final String ISLOCKED_ARG = "isLocked";
    public static final String KEY_CURRENT = "current";
    public static final String KEY_CLIPLIST = "cliplist";

    private ViewPager mViewPager;
    private TextView pos_tv;
    private LinearLayout lDownload, lSdahre;
    private static ImageLoader imageLoader;
    private ArrayList<String> mStills;
    String current;
    int pos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stills_view_pager);

        imageLoader = ImageLoader.getInstance();
        if(!imageLoader.isInited()) {
            imageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }

        findViewById(R.id.root).setPadding(0,0,0, CommonsPreference.getInstance().getVirtualKeyboardHeight());

        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        pos_tv = (TextView) findViewById(R.id.position);

        current = getIntent().getStringExtra(KEY_CURRENT);
        mStills = getIntent().getStringArrayListExtra(KEY_CLIPLIST);
        if(mStills != null) {
            for (int i = 0; i < mStills.size(); i++) {
                if(mStills.get(i).equalsIgnoreCase(current)) {
                    pos = i;
                }
            }
        }
        pos_tv.setText((pos + 1) + "/" + mStills.size());

        mViewPager.setAdapter(new SamplePagerAdapter(mStills));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pos = position;
                pos_tv.setText((pos + 1) + "/" + mStills.size());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(pos);

        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            ((HackyViewPager) mViewPager).setLocked(isLocked);
        }

        lDownload = (LinearLayout) findViewById(R.id.l_download);
        lSdahre = (LinearLayout) findViewById(R.id.l_share);

        lDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MyLog.i("download pos: " + pos);
                    MyLog.i("download mStills.get(pos): " + mStills.get(pos));
                    Bitmap img = BitmapUtil.getBitmapByUrl(StillsViewPagerActivity.this, mStills.get(pos));
                    if(img == null)
                        img = ImageLoader.getInstance().loadImageSync(mStills.get(pos), BitmapUtil.getImageLoaderOption());
                    MyLog.i("download img: " + img);

                    String path = BitmapUtil.saveBitmapToSDCardString(StillsViewPagerActivity.this, img, mStills.get(pos), 100);
                    MyLog.i("download path: " + path);

                    //发送系统广播让相册可以扫描到
                    Uri data = Uri.parse("file://" + path);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));

                    if(!TextUtils.isEmpty(path)) {
                        ToastUtils.toast(StillsViewPagerActivity.this, String.format(getResources().getString(R.string.ip_download_ok), BitmapUtil.BITMAP_DOWNLOAD));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    MyLog.e(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    MyLog.e(e);
                }

            }
        });

        // 图片的分享
        lSdahre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = null;
                try {
                    Bitmap img = BitmapUtil.getBitmapByUrl(StillsViewPagerActivity.this, mStills.get(pos));
                    if(img == null)
                        img = ImageLoader.getInstance().loadImageSync(mStills.get(pos), BitmapUtil.getImageLoaderOption());
                    MyLog.i("download img: " + img);

                    path = BitmapUtil.saveBitmapToSDCardString(StillsViewPagerActivity.this, img, mStills.get(pos), 100);
                    MyLog.i("download path: " + path);
                } catch (IOException e) {
                    e.printStackTrace();
                    MyLog.e(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    MyLog.e(e);
                }
                UmengSocialCenter shareCenter = new UmengSocialCenter(StillsViewPagerActivity.this);
                shareCenter.setShareContent("", "", "", path);
                shareCenter.openShare(StillsViewPagerActivity.this);
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    class SamplePagerAdapter extends PagerAdapter {

        private ArrayList<String> sDrawables;

        public SamplePagerAdapter(ArrayList<String> mStills) {
            sDrawables = mStills;
        }

        @Override
        public int getCount() {
            return sDrawables.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());

            try {
                imageLoader.displayImage(sDrawables.get(position), photoView, BitmapUtil.getImageLoaderOption());
            } catch (OutOfMemoryError e) {
                MyLog.e(e);
            } catch (Exception e) {
                MyLog.e(e);
            }

            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    finish();
                }
            });
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.viewpager_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        menuLockItem = menu.findItem(R.id.menu_lock);
//        toggleLockBtnTitle();
//        menuLockItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                toggleViewPagerScrolling();
//                toggleLockBtnTitle();
//                return true;
//            }
//        });
//
//        return super.onPrepareOptionsMenu(menu);
//    }

    private void toggleViewPagerScrolling() {
        if (isViewPagerActive()) {
            ((HackyViewPager) mViewPager).toggleLock();
        }
    }

//    private void toggleLockBtnTitle() {
//        boolean isLocked = false;
//        if (isViewPagerActive()) {
//            isLocked = ((HackyViewPager) mViewPager).isLocked();
//        }
//        String title = (isLocked) ? getString(R.string.menu_unlock) : getString(R.string.menu_lock);
//        if (menuLockItem != null) {
//            menuLockItem.setTitle(title);
//        }
//    }

    private boolean isViewPagerActive() {
        return (mViewPager != null && mViewPager instanceof HackyViewPager);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (isViewPagerActive()) {
            outState.putBoolean(ISLOCKED_ARG, ((HackyViewPager) mViewPager).isLocked());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        UMSocialService mController = UMServiceFactory.getUMSocialService(MoinSocialConstants.NAME_UMENG);
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
