package com.moinapp.wuliao.commons.moinsocialcenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.ipresource.model.EmojiInfo;
import com.moinapp.wuliao.utils.ToastUtils;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXEmojiObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.media.DoubanShareContent;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.RenrenShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.TencentWbShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMVideo;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.RenrenSsoHandler;
//import com.umeng.socialize.sso.SinaSsoHandler;
import com.moinapp.wuliao.commons.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.utils.BitmapUtils;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by liujiancheng on 15/6/2.
 * 根据项目需要，关于分享的格式主要有三种
 * 一种是IP，详情和表情专辑等整个的分享，需要从底部弹出统一的一个分享面板，这个我们采用友盟的默认分享面板。
 * 这个调用openShare()，支持umeng默认的8个平台
 * 第二种是比如单个表情剧照等分享，是单独定制格式的分享，我们暂时采用一个自己定制的分享控件，以后根据需要再改进
 * 这个调用postShare()，支持自己定制的规定平台
 * 第三种是直接调用分享逻辑，调用postShare(platform)
 */
public class UmengSocialCenter implements ISocial {
    private static final ILogger MyLog = LoggerFactory.getLogger("UmengSocialCenter");
    private static final int MAX_SHARE_TEXT_LENGTH = 140;

    private static UmengSocialCenter mInstance;
    private final UMSocialService mController;
    private Activity mContext;

    public UmengSocialCenter(Activity activity) {
        mContext = activity;
        mController = UMServiceFactory.getUMSocialService(MoinSocialConstants.NAME_UMENG);
        initShareSupport();
    }

    public static synchronized UmengSocialCenter getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new UmengSocialCenter(activity);
        }

        return mInstance;
    }

    /**
     * 第三方授权操作
     * @param platform
     */
    @Override
    public void doAuthorize(int platform, final DoAuthorizeListener listner) {
        SHARE_MEDIA share_media = convertPlatform(platform);
        mController.doOauthVerify(mContext, share_media, new SocializeListeners.UMAuthListener() {

            @Override
            public void onStart(SHARE_MEDIA platform) {
                MyLog.i("授权开始");
                ToastUtils.toast(mContext, "授权开始...");
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                MyLog.i("授权失败");
                ToastUtils.toast(mContext, "授权失败...");
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                MyLog.i("授权完成");
                ToastUtils.toast(mContext, "授权完成...");
                String uid = value.getString("uid");
                if (!TextUtils.isEmpty(uid)) {
                    MyLog.i(value.toString());
                    listner.onAuthorizeSucc();
                } else {
                    ToastUtils.toast(mContext, "授权失败uid is empty...");
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                MyLog.i("授权取消");
                ToastUtils.toast(mContext, "授权取消...");
            }
        });
    }

    /**
     * 获取第三方登陆得到的用户信息
     * @param platform
     * @return
     */
    @Override
    public void getUserInfo(int platform, final GetThirdUserInfoListener listener) {
        SHARE_MEDIA share_media = convertPlatform(platform);
        mController.getPlatformInfo(mContext, share_media, new SocializeListeners.UMDataListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, Map<String, Object> info) {
                listener.onGetUserInfoSucc(info);

            }
        });
    }

    /**
     * 设置分享内容
     * @param title： 分享的标题
     * @param content：分享的内容
     * @param targetUrl：分享的链接url
     * @param imageUrl：分享的缩略图
     */

    public void setShareContent(String title, String content, String targetUrl, String imageUrl) {
        setWXShareContent(title, content, targetUrl, imageUrl);
        setWXCircleContent(title, content, targetUrl, imageUrl);
        setQQContent(title, content, targetUrl, imageUrl);
//        setQzoneContent(title, content, targetUrl, imageUrl);
        setSinaContent(title, content, targetUrl, imageUrl);
//        setTecentContent(title, content, targetUrl, imageUrl);
//        setRenRenContent(title, content, targetUrl, imageUrl);
//        setDoubanContent(title, content, targetUrl, imageUrl);
    }

    /**
     * 打开友盟提供的统一分享面板
     */
    @Override
    public void openShare(Activity activity) {
        mController.openShare(activity, false);
    }

    /**
     * TODO:需要自定义分享面板
     */
    @Override
    public void postShare() {

    }


    /**
     * 调用postShare分享。跳转至分享编辑页，然后再分享
     * @platform: 传入的平台id
     */
    @Override
    public void postShare(int platform) {
        SHARE_MEDIA pt = convertPlatform(platform);
        mController.postShare(mContext, pt,
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

    /**
     * 初始化各种分享平台，主要有8种：
     * 微信好友，微信朋友圈，qq好友，qzone，新浪微博，腾讯微博，人人以及豆瓣
     */
    private void initShareSupport() {
        //设置支持的各种分享平台
        mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
        mController.getConfig().setPlatformOrder(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
        //配置各种分享平台参数，主要是配置各个平台的appid secret key等
        configPlatforms();
    }


    /**
     * 配置各个分享平台
     */
    private void configPlatforms() {
        new SinaSsoHandler(mContext).addToSocialSDK();
        // 添加新浪SSO授权
//        mController.getConfig().setSsoHandler(new SinaSsoHandler(mContext));
        // 添加腾讯微博SSO授权
//        mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
//        // 添加人人网SSO授权
//        RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(mContext,
//                MoinSocialConstants.RENREN_APP_ID, MoinSocialConstants.RENREN_APP_KEY,
//                MoinSocialConstants.RENREN_APP_SECRET);
//        mController.getConfig().setSsoHandler(renrenSsoHandler);

        // 添加QQ、QZone平台
        addQQQZonePlatform();

        // 添加微信、微信朋友圈平台
        addWXPlatform();
    }

    /**
     * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
     *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
     *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
     *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
     * @return
     */
    private void addQQQZonePlatform() {
        // 添加QQ支持, 并且设置QQ分享内容的target url
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mContext,
                MoinSocialConstants.QQ_APP_ID, MoinSocialConstants.QQ_APP_KEY);
        qqSsoHandler.addToSocialSDK();

        // 添加QZone平台
//        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mContext,
//                MoinSocialConstants.QQ_APP_ID, MoinSocialConstants.QQ_APP_KEY);
//        qZoneSsoHandler.addToSocialSDK();
    }

    /**
     * @功能描述 : 添加微信平台分享
     * @return
     */
    private void addWXPlatform() {
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(mContext, MoinSocialConstants.WEIXIN_APP_ID,
                MoinSocialConstants.WEIXIN_SECRET);
        wxHandler.addToSocialSDK();
        mController.getConfig().setSsoHandler(wxHandler);

        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(mContext, MoinSocialConstants.WEIXIN_APP_ID,
                MoinSocialConstants.WEIXIN_SECRET);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    /**
     * 设置微信分享内容
     */
    private void setWXShareContent(String title, String content, String targetUrl, String imageUrl) {
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareContent(content);
        weixinContent.setTitle(title);
        weixinContent.setTargetUrl(targetUrl);
        if (imageUrl != null) {
            weixinContent.setShareMedia(new UMImage(mContext, imageUrl));
        }
        mController.setShareMedia(weixinContent);
    }

    /**
     * 设置微信朋友圈分享内容
     */
    private void setWXCircleContent(String title, String content, String targetUrl, String imageUrl) {
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareContent(content);
        circleMedia.setTitle(title);
        if (imageUrl != null) {
            circleMedia.setShareMedia(new UMImage(mContext, imageUrl));
        }
        circleMedia.setTargetUrl(targetUrl);
        mController.setShareMedia(circleMedia);
    }

    /**
     * 设置qq分享内容
     */
    private void setQQContent(String title, String content, String targetUrl, String imageUrl) {
        QQShareContent qqShareContent = new QQShareContent();
        qqShareContent.setShareContent(content);
        qqShareContent.setTitle(title);
        if (imageUrl != null) {
            qqShareContent.setShareMedia(new UMImage(mContext, imageUrl));
        }
        qqShareContent.setTargetUrl(targetUrl);
        mController.setShareMedia(qqShareContent);
    }

    /**
     * 设置qq空间分享内容
     */
    private void setQzoneContent(String title, String content, String targetUrl, String imageUrl) {
        QZoneShareContent qzone = new QZoneShareContent();
        qzone.setShareContent(content);
        qzone.setTargetUrl(targetUrl);
        qzone.setTitle(title);
        qzone.setShareMedia(new UMImage(mContext, imageUrl));
        mController.setShareMedia(qzone);
    }

    /**
     * 设置人人分享内容
     */
    private void setRenRenContent(String title, String content, String targetUrl, String imageUrl) {
        RenrenShareContent renrenShareContent = new RenrenShareContent();
        renrenShareContent.setShareContent(content);
        UMImage image = new UMImage(mContext, imageUrl);
        image.setTitle(title);
        image.setThumb(imageUrl);
        renrenShareContent.setShareImage(image);
        renrenShareContent.setAppWebSite(targetUrl);
        mController.setShareMedia(renrenShareContent);
    }

    /**
     * 设置新浪微博分享内容
     */
    private void setSinaContent(String title, String content, String targetUrl, String imageUrl) {
        SinaShareContent sinaShareContent = new SinaShareContent();
        String text = title + " " + content;
        int url_length = targetUrl == null ? 0: targetUrl.length();
        int max_length = (MAX_SHARE_TEXT_LENGTH - url_length) - 2;
        if (text.length() > max_length) {
            text = text.substring(0, max_length);
        }
        sinaShareContent.setShareContent(text + " " + targetUrl);
        if (imageUrl != null) {
            sinaShareContent.setShareImage(new UMImage(mContext, imageUrl));
        }
        mController.setShareMedia(sinaShareContent);
    }

    /**
     * 设置腾讯微博分享内容
     */
    private void setTecentContent(String title, String content, String targetUrl, String imageUrl) {
        TencentWbShareContent tencentWbShareContent = new TencentWbShareContent();
        tencentWbShareContent.setShareContent(content);
        tencentWbShareContent.setTitle(title);
        tencentWbShareContent.setShareImage(new UMImage(mContext, imageUrl));
        tencentWbShareContent.setAppWebSite(targetUrl);
        mController.setShareMedia(tencentWbShareContent);
    }

    /**
     * 设置豆瓣分享内容
     */
    private void setDoubanContent(String title, String content, String targetUrl, String imageUrl) {
        DoubanShareContent doubanContent = new DoubanShareContent();
        doubanContent.setShareContent(content);
        doubanContent.setTitle(title);
        doubanContent.setShareImage(new UMImage(mContext, imageUrl));
        doubanContent.setAppWebSite(targetUrl);
        mController.setShareMedia(doubanContent);
    }

    /**
     * 将外部传入的自定义platform转为友盟的platform
     * @param platform
     * @return
     */
    private SHARE_MEDIA convertPlatform(int platform) {
        SHARE_MEDIA result = SHARE_MEDIA.WEIXIN;
        switch (platform) {
            case MoinSocialConstants.PLATFOMR_WX:
                result = SHARE_MEDIA.WEIXIN;
                break;
            case MoinSocialConstants.PLATFOMR_WX_CIRCLE:
                result = SHARE_MEDIA.WEIXIN_CIRCLE;
                break;
            case MoinSocialConstants.PLATFOMR_QQ:
                result = SHARE_MEDIA.QQ;
                break;
            case MoinSocialConstants.PLATFOMR_QZONE:
                result = SHARE_MEDIA.QZONE;
                break;
            case MoinSocialConstants.PLATFOMR_SINA_WB:
                result = SHARE_MEDIA.SINA;
                break;
            case MoinSocialConstants.PLATFOMR_TECENT_WB:
                result = SHARE_MEDIA.TENCENT;
                break;
            case MoinSocialConstants.PLATFOMR_RENREN:
                result = SHARE_MEDIA.RENREN;
                break;
            case MoinSocialConstants.PLATFOMR_DB:
                result = SHARE_MEDIA.DOUBAN;
                break;
        }
       return result;
    }

    public void shareEmoji(String path, EmojiInfo emoji) {
        //添加微信表情：
        UMWXHandler handler = (UMWXHandler) mController.getConfig().getSsoHandler(SHARE_MEDIA.WEIXIN.getReqCode());
        WXEmojiObject emojiObject = new WXEmojiObject();
        emojiObject.setEmojiPath(path);

        WXMediaMessage wxMediaMessage  = new WXMediaMessage(emojiObject);
        //微信规定缩略图大小不能超过32KB
//        wxMediaMessage.thumbData = BitmapUtils.bitmap2Bytes(BitmapFactory.decodeFile();

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = wxMediaMessage;

        //朋友圈请换成：SendMessageToWX.Req.WXSceneTimeline
        req.scene = SendMessageToWX.Req.WXSceneSession;

        try {
            Field field = UMWXHandler.class.getDeclaredField("mEntity");
            field.setAccessible(true);
            field.set(handler, mController.getEntity());
            handler.mShareContent = "ewrwerwerewr";
//            handler.mShareMedia = new UMImage(mContext, new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.getWXApi().sendReq(req);



    }
}
