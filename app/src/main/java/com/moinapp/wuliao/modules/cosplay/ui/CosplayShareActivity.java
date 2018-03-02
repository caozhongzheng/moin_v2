package com.moinapp.wuliao.modules.cosplay.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.modules.ipresource.IPResourceConstants;
import com.moinapp.wuliao.wxapi.WXConstants;
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

/**
 * Created by liujiancheng on 15/5/29.
 */
public class CosplayShareActivity extends BaseActivity implements View.OnClickListener {
    private static final ILogger MyLog = LoggerFactory.getLogger("CosplayShareActivity");
    public static  final String COSPLAY_PATH = "cosplay_path";

    Context mContext;
    ImageView mShare2WX;
    ImageView mShare2QQ;

    String mCosplayPath;

    // 友盟整个平台的Controller, 负责管理整个友盟SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService("com.umeng.share", RequestType.SOCIAL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.cosplay_share_dialog);

        //设置屏幕宽度
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();
        android.view.WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth() * 1;
        getWindow().setAttributes(p);

        mCosplayPath = getCosplay();
        if (TextUtils.isEmpty(mCosplayPath)) {
            finish();
        }

        findViews();
        setupClickListener();

        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(this, WXConstants.APP_ID, WXConstants.APP_SECRET);
        wxHandler.addToSocialSDK();

        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,WXConstants.QQ_APP_ID, WXConstants.QQ_APP_SECRET);
        qqSsoHandler.addToSocialSDK();
    }

    private String getCosplay() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }

        return intent.getStringExtra(COSPLAY_PATH);
    }

    private void findViews() {
        mShare2WX = (ImageView) findViewById(R.id.gif_weixin);
        mShare2QQ = (ImageView) findViewById(R.id.gif_qq);
    }

    private void setupClickListener() {
        mShare2WX.setOnClickListener(this);
        mShare2QQ.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
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
                emojiObject.emojiPath = mCosplayPath;

                // 用WXTextObject对象初始化一个WXMediaMessage对象
                WXMediaMessage msg = new WXMediaMessage(emojiObject);
                msg.title = "";
                msg.description = "";
                // 发现微信规定缩略图不能超过32k，实际上有时候20k的也不行，所以统一直接用default icon作为缩略图了，以后要改
//                msg.thumbData = BitmapUtils.bitmap2Bytes(BitmapFactory.decodeFile(mEmojiThumb));
                msg.thumbData = BitmapUtils.bitmap2Bytes(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.default_img));

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
                qqShareContent.setShareMedia(new UMImage(mContext, mCosplayPath));
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

}
