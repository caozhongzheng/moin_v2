package com.moinapp.wuliao.modules.wowo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.gif.GifUtils;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.preference.CommonsPreference;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.modules.ipresource.HackyViewPager;
import com.moinapp.wuliao.modules.ipresource.IPResourceConstants;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.HttpUtil;
import com.moinapp.wuliao.wxapi.WXConstants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXEmojiObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.utils.BitmapUtils;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by moying on 15/6/17.
 */
public class EmojisViewPagerActivity extends BaseActivity {
    private static final ILogger MyLog = LoggerFactory.getLogger("emojis");
    private static final String ISLOCKED_ARG = "isLocked";
    public static final String KEY_CID = "cid";
    public static final String KEY_CURRENT_URL = "current";
    public static final String KEY_CID_LIST = "cidlist";
    public static final String KEY_EMJURL_LIST = "emojilist";
    public static final String KEY_EMJPATHLIST = "emojiPathlist";

    private ViewPager mViewPager;
    private TextView pos_tv;
    private ArrayList<Integer> mCids;
    private ArrayList<String> mEmojis;
    private ArrayList<String> mEmojiPaths;
    private LinearLayout mShareWX;
    private LinearLayout mShareQQ;
    private Context mContext;
    private SamplePagerAdapter adapter;
    int cid, pos;
    private ImageLoader mImageLoader;

    // 友盟整个平台的Controller, 负责管理整个友盟SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share", RequestType.SOCIAL);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emoji_view_pager);

        //初始化图片缓存
        mImageLoader = ImageLoader.getInstance();
        if(!mImageLoader.isInited()) {
            mImageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        }

        // 添加微信 qq平台
        UMWXHandler wxHandler = new UMWXHandler(this, WXConstants.APP_ID, WXConstants.APP_SECRET);
        wxHandler.addToSocialSDK();
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,WXConstants.QQ_APP_ID, WXConstants.QQ_APP_SECRET);
        qqSsoHandler.addToSocialSDK();
        mContext = this;

        findViewById(R.id.root).setPadding(0, 0, 0, CommonsPreference.getInstance().getVirtualKeyboardHeight());

        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        pos_tv = (TextView) findViewById(R.id.position);

        cid = getIntent().getIntExtra(KEY_CID, 0);
        MyLog.i("你点击了第" + cid + "楼");
        mCids = getIntent().getIntegerArrayListExtra(KEY_CID_LIST);
        mEmojis = getIntent().getStringArrayListExtra(KEY_EMJURL_LIST);
        mEmojiPaths = getIntent().getStringArrayListExtra(KEY_EMJPATHLIST);

        if(mCids != null && mEmojis != null && mEmojiPaths != null) {
            for (int i = 0; i < mCids.size(); i++) {
                if(mCids.get(i) == cid) {
                    pos = i;
                }
            }
        }
        if(pos >= mCids.size()) {
            pos = mCids.size()-1;
        }
        pos_tv.setText((pos + 1) + "/" + mEmojis.size());

        mViewPager.setAdapter(adapter = new SamplePagerAdapter(mEmojis, mEmojiPaths));
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pos = position;
                pos_tv.setText((pos + 1) + "/" + mEmojis.size());
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


        findViewById(R.id.share_wx).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emjPath = adapter.getsPaths().get(mViewPager.getCurrentItem());
                UMWXHandler handler = (UMWXHandler) mController.getConfig().getSsoHandler(SHARE_MEDIA.WEIXIN.getReqCode());
                WXEmojiObject emojiObject = new WXEmojiObject();
                emojiObject.emojiPath = emjPath;

                // 用WXTextObject对象初始化一个WXMediaMessage对象
                WXMediaMessage msg = new WXMediaMessage(emojiObject);
                msg.title = "";
                msg.description = "";
                if (new File(emjPath).exists())
                    MyLog.i("mEmojiThumb=" + emjPath + " exist!!!!");
                // 先从imageloader的缓存读
                Bitmap bitmap = null;
                //从表情文件下载的本读路径读
                String thumb = emjPath.substring(0,emjPath.lastIndexOf(".")) + IPResourceConstants.JPG_EXTENSION;
                MyLog.i("thumb =" + thumb);
                File thumbFIle = new File(thumb);
                if (thumbFIle.exists()) {
                    bitmap = BitmapFactory.decodeFile(thumb);
                }
                if (bitmap == null) {
                    MyLog.i("bitmap = null");
                    // 都获取不到的话用默认的图标
                    bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_img);
                }
                //开始压缩质量，从60开始压，这个是经验值测试得出，不保险，最终需要服务器上传的表情缩略图
                //要小于32k来解决
                if (BitmapUtils.bitmap2Bytes(bitmap).length > 32*1024) {
                    bitmap = BitmapUtil.compressImage(bitmap, 60, 32);
                    //压缩后仍让大于32k，用默认缩略图
                    if (BitmapUtils.bitmap2Bytes(bitmap).length > 32*1024) {
                        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_img);
                    }
                }

                msg.thumbData = BitmapUtils.bitmap2Bytes(bitmap);
                MyLog.i("msg.thumbData.size=" + msg.thumbData.length);

                // 构造一个Req
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = String.valueOf(System.currentTimeMillis()); // transaction字段用于唯一标识一个请求
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneSession;
                handler.getWXApi().sendReq(req);
            }
        });

        findViewById(R.id.share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emjPath = adapter.getsPaths().get(mViewPager.getCurrentItem());
                QQShareContent qqShareContent = new QQShareContent();
                qqShareContent.setTitle("");
                qqShareContent.setShareMedia(new UMImage(mContext, emjPath));
                mController.setShareMedia(qqShareContent);
                mController.postShare(mContext, SHARE_MEDIA.QQ,
                        new SocializeListeners.SnsPostListener() {
                            @Override
                            public void onStart() {
                                Toast.makeText(mContext, "开始分享.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
                                if (eCode == 200) {
                                    Toast.makeText(mContext, "分享成功.", Toast.LENGTH_SHORT).show();
                                } else {
                                    String eMsg = "";
                                    if (eCode == -101) {
                                        eMsg = "没有授权";
                                    }
                                    Toast.makeText(mContext, "分享失败[" + eCode + "] " +
                                            eMsg, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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

        private ArrayList<String> sUrls;

        private ArrayList<String> sPaths;

        public ArrayList<String> getsPaths() {
            return sPaths;
        }

        public SamplePagerAdapter(ArrayList<String> mEmojis, ArrayList<String> mEmojiPaths) {
            sUrls = mEmojis;
            sPaths = mEmojiPaths;
        }

        @Override
        public int getCount() {
            return sUrls.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            ImageView photoView = new ImageView(container.getContext());

            boolean down = HttpUtil.download(sUrls.get(position), sPaths.get(position));
            if (down) {
                GifUtils.displayGif(photoView, new File(sPaths.get(position)));
            }

            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EmojisViewPagerActivity.this.finish();
                }
            });
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            MyLog.i("destroy item: position=" + position);
            GifUtils.recycleGif((ImageView) object);
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

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
}
