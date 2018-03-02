package com.moinapp.wuliao.modules.ipresource;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.gif.GifAnimationDrawable;
import com.moinapp.wuliao.commons.gif.GifUtils;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.modules.ipresource.model.EmojiInfo;
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

/**
 * Created by liujiancheng on 15/5/29.
 */
public class EmojiShowActivity extends BaseActivity implements View.OnClickListener {
    private static final ILogger MyLog = LoggerFactory.getLogger("EmojiShowActivity");
    public static  final String EMOJI_OBJECT = "emoji_object";
    public static final String EMOJI_PATH = "emoji_path";

    GifAnimationDrawable mGif;
    Context mContext;
    ImageView mEmojiImage;
    ImageView mCancelView;
    ImageView mShare2WX;
    ImageView mShare2WXFriends;

    EmojiInfo mEmoji;
    String mEmojiThumb;
    String mEmojiPath;
    private ImageLoader imageLoader;
    // 友盟整个平台的Controller, 负责管理整个友盟SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share", RequestType.SOCIAL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.emoji_gif_dialog);

        //设置屏幕宽度
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth() * 1;
        getWindow().setAttributes(p);

        imageLoader = ImageLoader.getInstance();
        if(!imageLoader.isInited())
            imageLoader.init(BitmapUtil.getImageLoaderConfiguration());

        mEmoji = getEmoji();
        if (TextUtils.isEmpty(mEmojiPath) && mEmoji == null) {
            finish();
        }
        if (mEmoji != null) {
            mEmojiThumb = EmojiUtils.getThumbPath(mEmoji);
            mEmojiPath = EmojiUtils.getEmjPath(mEmoji);
        }

        findViews();
        setupClickListener();
        displayEmoji();

        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(this, WXConstants.APP_ID, WXConstants.APP_SECRET);
        wxHandler.addToSocialSDK();

        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,WXConstants.QQ_APP_ID, WXConstants.QQ_APP_SECRET);
        qqSsoHandler.addToSocialSDK();
    }

    private void findViews() {
        mEmojiImage = (ImageView) findViewById(R.id.dialog_imageview);
        mCancelView = (ImageView) findViewById(R.id.share_dialog_cancel);
        mShare2WX = (ImageView) findViewById(R.id.gif_weixin);
        mShare2WXFriends = (ImageView) findViewById(R.id.gif_qq);
    }

    private void setupClickListener() {
        mCancelView.setOnClickListener(this);
        mShare2WX.setOnClickListener(this);
        mShare2WXFriends.setOnClickListener(this);
    }

    private void displayEmoji() {
        final File file = new File(mEmojiPath);
        if (file.exists()) {
            MyLog.i("ljc...file exist");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            GifUtils.displayGif(mEmojiImage, file);
                        }
                    });
                }
            }).start();

        } else {
            //download gif first
            if (mEmoji != null) {
                downloadEmoji(mEmoji.getPicture().getUri(), mEmojiPath, new EmojiDownloadListener() {
                    @Override
                    public void onEmojiDownloadSucc(File gif) {
                        MyLog.i("onEmojiDownloadSucc: download gif file:" + gif.getAbsolutePath());
                        final File f = gif;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GifUtils.displayGif(mEmojiImage, f);
                            }
                        });
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.share_dialog_cancel:
                finish();
                break;
            case R.id.gif_weixin:
                //TODO
                doShare(IPResourceConstants.PLATFORM_WEIXIN);
                break;
            case R.id.gif_qq:
                //TODO
                doShare(IPResourceConstants.PLATFORM_QQ);
                break;
        }
    }

    private void doShare(int platform) {
        switch (platform) {
            case IPResourceConstants.PLATFORM_WEIXIN:
                UMWXHandler handler = (UMWXHandler) mController.getConfig().getSsoHandler(SHARE_MEDIA.WEIXIN.getReqCode());
                WXEmojiObject emojiObject = new WXEmojiObject();
                emojiObject.emojiPath = mEmojiPath;


                // 用WXTextObject对象初始化一个WXMediaMessage对象
                WXMediaMessage msg = new WXMediaMessage(emojiObject);
                msg.title = "";
                msg.description = "";

                // 先从imageloader的缓存读
                Bitmap bitmap = null;
                if (mEmoji != null) {
                    bitmap = imageLoader.loadImageSync(mEmoji.getIcon().getUri());
                }
                if (bitmap == null) {
                    //再从表情文件下载的本读路径读
                    String thumb = mEmojiPath.substring(mEmojiPath.lastIndexOf(".")) + IPResourceConstants.JPG_EXTENSION;
                    MyLog.i("thumb =" + thumb);
                    File thumbFIle = new File(thumb);
                    if (thumbFIle.exists()) {
                        bitmap = BitmapFactory.decodeFile(thumb);
                    }
                    if (bitmap == null) {
                        MyLog.i("bitmap = null");
                        // 都获取不到的话用默认的图标
                        bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.default_img);
                    }
                }
                //开始压缩质量，从60开始压，这个是经验值测试得出，不保险，最终需要服务器上传的表情缩略图
                //要小于32k来解决
                if (BitmapUtils.bitmap2Bytes(bitmap).length > 32*1024) {
                    bitmap = BitmapUtil.compressImage(bitmap, 60, 32);
                    //压缩后仍让大于32k，用默认缩略图
                    if (BitmapUtils.bitmap2Bytes(bitmap).length > 32*1024) {
                        bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.default_img);
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
                break;

            case IPResourceConstants.PLATFORM_QQ:
                QQShareContent qqShareContent = new QQShareContent();
                qqShareContent.setTitle("");
                qqShareContent.setShareMedia(new UMImage(mContext, mEmojiPath));
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
                break;
        }
    }

    private EmojiInfo getEmoji() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }

        mEmojiPath = intent.getStringExtra(EMOJI_PATH);
        return (EmojiInfo)intent.getSerializableExtra(EMOJI_OBJECT);
    }

    private void downloadEmoji(final String url, final String path, final EmojiDownloadListener listener) {
        MyLog.i("downloadEmoji:url="+url+",path=" + path);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //download thumb first
                HttpUtil.download(mEmoji.getIcon().getUri(), mEmojiThumb);
                if (HttpUtil.download(url, path)) {
                    listener.onEmojiDownloadSucc(new File(path));
                }

            }
        }).start();
    }

    private interface EmojiDownloadListener {
        void onEmojiDownloadSucc(File gifFile);
    }

}
