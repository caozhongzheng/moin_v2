package com.moinapp.wuliao.modules.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.BaseKeyListener;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.moinapp.wuliao.MoinActivity;
import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.SinaSsoHandler;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.ui.BaseActivity;
import com.moinapp.wuliao.modules.ipresource.EmojiResourceActivity;
import com.moinapp.wuliao.modules.ipresource.IPMoinClipActivity;
import com.moinapp.wuliao.modules.login.model.Location;
import com.moinapp.wuliao.modules.login.model.UserInfo;
import com.moinapp.wuliao.modules.mine.MinePreference;
import com.moinapp.wuliao.utils.AppTools;
import com.moinapp.wuliao.utils.BitmapUtil;
import com.moinapp.wuliao.utils.HttpUtil;
import com.moinapp.wuliao.utils.MD5;
import com.moinapp.wuliao.utils.StringUtil;
import com.moinapp.wuliao.utils.ToastUtils;
import com.moinapp.wuliao.utils.Tools;
import com.moinapp.wuliao.wxapi.WXConstants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.util.Map;

/**
 * Created by moying on 15/5/5.
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

    public static final String KEY_NEED_RETURN = "finish_self";
    public static final String KEY_FROM = "from";
    public static final String KEY_ACTION = "action";
    private ILogger MyLog = LoggerFactory.getLogger(LoginModule.MODULE_NAME);
    private EditText login_username, login_password;
    private ImageView eye;
    private Button login_submit;
    private String username, password;
    private ImageLoader imageLoader;
    private boolean isNeedReturn;
    private int mFrom;
    private int mAction;

    // 友盟整个平台的Controller, 负责管理整个友盟SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService(ClientInfo.getPackageName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_layout);

        MyLog.i("onCreate ClientInfo.getUID()=" + ClientInfo.getUID());
        isNeedReturn = getIntent().getBooleanExtra(KEY_NEED_RETURN, false);
        mFrom = getIntent().getIntExtra(KEY_FROM, 0);
        mAction = getIntent().getIntExtra(KEY_ACTION, 0);

        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(LoginActivity.this, WXConstants.APP_ID, WXConstants.APP_SECRET);
        mController.getConfig().setSsoHandler(wxHandler);

        // 添加QQ平台
        UMQQSsoHandler qqHandler = new UMQQSsoHandler(LoginActivity.this, WXConstants.QQ_APP_ID, WXConstants.QQ_APP_SECRET);
        mController.getConfig().setSsoHandler(qqHandler);

        // 添加新浪微博平台
        mController.getConfig().setSsoHandler(new SinaSsoHandler(LoginActivity.this));

        initView();
        initData();
    }

    private void initView() {
        login_username = (EditText) this.findViewById(R.id.phone_num);
        login_password = (EditText) this.findViewById(R.id.password);
        eye = (ImageView) this.findViewById(R.id.pwd_visible);
        login_submit = (Button) this.findViewById(R.id.login_submit);
    }

    private void initData() {
        imageLoader = ImageLoader.getInstance();
        if(!imageLoader.isInited())
            imageLoader.init(BitmapUtil.getImageLoaderConfiguration());
        login_password.setKeyListener(keyListener);
        login_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                eye.setVisibility(hasFocus ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ToastUtils.toast(LoginActivity.this, R.string.login_username_hint);
                    break;
                case -1:
                    ToastUtils.toast(LoginActivity.this, R.string.regist_password_hint);
                    break;
                case -2:
                    ToastUtils.toast(LoginActivity.this, R.string.login_input_err);
                    break;
                case -3:// 登录失败
                    ToastUtils.toast(LoginActivity.this, msg.obj.toString());
                    break;
                case -4:
                    ToastUtils.toast(LoginActivity.this, R.string.no_network);
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        MyLog.i("LoginActivity onBackPressed");
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.retrive_password:
                if(!Tools.isFastDoubleClick()) {
                    AppTools.toIntent(this, RetrivePasswordActivity.class);
                }
                break;
            case R.id.pwd_visible:
                togglePwdVisible();
                break;
            case R.id.register:
                if(!Tools.isFastDoubleClick()) {
                    AppTools.toIntent(this, RegistActivity.class);
                }
                break;
            case R.id.login_submit:
                if(!Tools.isFastDoubleClick()) {
                    if(StringUtil.isNullOrEmpty(login_username.getText().toString())) {
                        handler.sendEmptyMessage(0);
                    } else if(StringUtil.isNullOrEmpty(login_password.getText().toString())) {
                        handler.sendEmptyMessage(-1);
                    } else if (!checkInput()) {
                        handler.sendEmptyMessage(-2);
                    } else {
                        AppTools.collapseSoftInputMethod(login_password);
                        login();
                    }
                }
                break;
            case R.id.wechat:
                if(!Tools.isFastDoubleClick()) {
                    authorize(SHARE_MEDIA.WEIXIN);
                }
                break;
            case R.id.qq:
                if(!Tools.isFastDoubleClick()) {
                    authorize(SHARE_MEDIA.QQ);
                }
                break;
            case R.id.weibo:
                if(!Tools.isFastDoubleClick()) {
                    authorize(SHARE_MEDIA.SINA);
                }
                break;
        }
    }

    /**
     * 授权。如果授权成功，则获取用户信息</br>
     */
    private void authorize(final SHARE_MEDIA platform) {
        MyLog.i("login platform=" + platform.name());
        mController.doOauthVerify(LoginActivity.this, platform, new SocializeListeners.UMAuthListener() {

            @Override
            public void onStart(SHARE_MEDIA platform) {
                MyLog.i("授权开始"+platform);
                ToastUtils.toast(LoginActivity.this, "授权开始...");
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                MyLog.i("授权失败");
                ToastUtils.toast(LoginActivity.this, "授权失败...");
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                MyLog.i("授权完成"+value);
                ToastUtils.toast(LoginActivity.this, "授权完成...");
                String uid = value.getString("uid");
                if (!TextUtils.isEmpty(uid)) {
                    MyLog.i(value.toString());
                    loginThird(platform, value);
                } else {
                    ToastUtils.toast(LoginActivity.this, "授权失败uid is empty...");
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                MyLog.i("授权取消");
                ToastUtils.toast(LoginActivity.this, "授权取消...");
            }
        });
    }

    private void login() {
        if(StringUtil.isEmail(username) || StringUtil.isCellphone(username)) {
            LoginManager.getInstance().userOurLogin(username, MD5.md5(password), new IListener() {
                @Override
                public void onSuccess(Object obj) {
                    if(isNeedReturn) {
                        setLoginResult();
                    }
//                        finish();
                    else
                        AppTools.toIntent(LoginActivity.this, MoinActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }

                @Override
                public void onNoNetwork() {
                    Message msg = handler.obtainMessage();
                    msg.what = -4;
                    handler.sendMessage(msg);
                }

                @Override
                public void onErr(Object object) {
                    MyLog.i("login failed: " + object.toString());
                    Message msg = handler.obtainMessage();
                    msg.what = -3;
                    msg.obj = getErrorInfo(object);
                    handler.sendMessage(msg);
                }
            });
        }
    }

    private void loginThird(final SHARE_MEDIA platform, Bundle value) {
        LoginManager.getInstance().userThirdLogin(platform.toString(), value, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                int code = (int) obj;
                if (code == 3) {//3 使用第三方帐号第一次登录，需要注册用户个人信息
                    getUserInfo(platform);
                } else {
                    if(isNeedReturn)
                        finish();
                    else
                        AppTools.toIntent(LoginActivity.this, MoinActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP);
                }
            }

            @Override
            public void onNoNetwork() {
                Message msg = handler.obtainMessage();
                msg.what = -4;
                handler.sendMessage(msg);
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("login third failed: " + object.toString());
                Message msg = handler.obtainMessage();
                msg.what = -3;
                msg.obj = getErrorInfo(object);
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 获取授权平台的用户信息</br>
     */
    private void getUserInfo(final SHARE_MEDIA platform) {
        mController.getPlatformInfo(this, platform, new SocializeListeners.UMDataListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, Map<String, Object> info) {
//                String showText = "";
//                if (status == StatusCode.ST_CODE_SUCCESSED) {
//                    showText = "用户名：" + info.get("screen_name").toString();
//                    Log.d("#########", "##########" + info.toString());
//                } else {
//                    showText = "获取用户信息失败";
//                }
                if (status == 200 && info != null) {
//                    ToastUtils.toast(LoginActivity.this, info.toString());
                    MyLog.i(info.toString());
                    MyLog.i("platform:" + platform + ", " + (platform == SHARE_MEDIA.QQ));
                    if (platform == SHARE_MEDIA.WEIXIN) {
                        MyLog.i("WX OK:");
                        onAuthByWeiXin(info);
                    } else if (platform == SHARE_MEDIA.QQ) {
                        MyLog.i("QQ OK:");
                        onAuthByQQ(info);
                    } else if (platform == SHARE_MEDIA.SINA) {
                        MyLog.i("SINA OK:");
                        onAuthBySina(info);
                    }


                } else {
                    AppTools.toIntent(LoginActivity.this, PersonalInfoActivity.class);
                }

            }
        });
    }

    private void onAuthBySina(Map<String, Object> info) {
        // goto PersonalInfoActivity
        Bundle bundle = new Bundle();
        bundle.putString(PersonalInfoActivity.KEY_SEX, info.get("gender").toString());
        bundle.putString(PersonalInfoActivity.KEY_NICKNAME, info.get("screen_name").toString());
        bundle.putString(PersonalInfoActivity.KEY_HEADIMGURL, info.get("profile_image_url").toString());
        bundle.putString(PersonalInfoActivity.KEY_PROVINCE, info.get("location").toString());
        bundle.putString(PersonalInfoActivity.KEY_PLATFORM, SHARE_MEDIA.SINA.toString());
        AppTools.toIntent(LoginActivity.this, bundle, PersonalInfoActivity.class);

        // update user info
        UserInfo us = new UserInfo();
        us.setSex(StringUtil.getGender(LoginActivity.this, info.get("gender").toString(), false));
        //TODO nickname max length
        us.setNickname(info.get("screen_name").toString());
        Location location = new Location();
        location.setProvince(info.get("location").toString());
        us.setLocation(location);

        udpateUserInfo(us);
        uploadAvatar(info.get("profile_image_url").toString());
    }

    private void onAuthByWeiXin(Map<String, Object> info) {
        // goto PersonalInfoActivity
        Bundle bundle = new Bundle();
        bundle.putString(PersonalInfoActivity.KEY_SEX, info.get("sex").toString());
        bundle.putString(PersonalInfoActivity.KEY_NICKNAME, info.get("nickname").toString());
        bundle.putString(PersonalInfoActivity.KEY_HEADIMGURL, info.get("headimgurl").toString());
        bundle.putString(PersonalInfoActivity.KEY_COUNTRY, info.get("country").toString());
        bundle.putString(PersonalInfoActivity.KEY_PROVINCE, info.get("province").toString());
        bundle.putString(PersonalInfoActivity.KEY_CITY, info.get("city").toString());
        bundle.putString(PersonalInfoActivity.KEY_PLATFORM, SHARE_MEDIA.WEIXIN.toString());
        AppTools.toIntent(LoginActivity.this, bundle, PersonalInfoActivity.class);

        // update user info
        UserInfo us = new UserInfo();
        us.setSex(StringUtil.getGender(LoginActivity.this, info.get("sex").toString(), false));
        //TODO nickname max length
        us.setNickname(info.get("nickname").toString());
        Location location = new Location();
        location.setCountry(info.get("country").toString());
        location.setProvince(info.get("province").toString());
        location.setCity(info.get("city").toString());
        us.setLocation(location);

        udpateUserInfo(us);
        uploadAvatar(info.get("headimgurl").toString());
    }

    private void onAuthByQQ(Map<String, Object> info) {
        // goto PersonalInfoActivity
        Bundle bundle = new Bundle();
        bundle.putString(PersonalInfoActivity.KEY_SEX, info.get("gender").toString());
        bundle.putString(PersonalInfoActivity.KEY_NICKNAME, info.get("screen_name").toString());
        bundle.putString(PersonalInfoActivity.KEY_HEADIMGURL, info.get("profile_image_url").toString());
        bundle.putString(PersonalInfoActivity.KEY_PROVINCE, info.get("province").toString());
        bundle.putString(PersonalInfoActivity.KEY_CITY, info.get("city").toString());
        bundle.putString(PersonalInfoActivity.KEY_PLATFORM, SHARE_MEDIA.QQ.toString());
        AppTools.toIntent(LoginActivity.this, bundle, PersonalInfoActivity.class);

        // update user info
        UserInfo us = new UserInfo();
        us.setSex(StringUtil.getGender(LoginActivity.this, info.get("gender").toString(), false));
        //TODO nickname max length
        us.setNickname(info.get("screen_name").toString());
        Location location = new Location();
        location.setProvince(info.get("province").toString());
        location.setCity(info.get("city").toString());
        us.setLocation(location);

        udpateUserInfo(us);
        uploadAvatar(info.get("profile_image_url").toString());
    }

    private void udpateUserInfo(UserInfo us) {
        MinePreference.getInstance().setNickname(us.getNickname());
        LoginManager.getInstance().userUpdate(us, new IListener() {
            @Override
            public void onSuccess(Object obj) {
                MyLog.i("update userinfo OK:" + ((UserInfo) obj).toString());
            }

            @Override
            public void onNoNetwork() {
                MyLog.i("update userinfo NG:" + getString(R.string.no_network));
            }

            @Override
            public void onErr(Object object) {
                MyLog.i("update userinfo NG:" + object.toString());
            }
        });
    }

    private void uploadAvatar(String headimgurl) {
        if (!StringUtil.isNullOrEmpty(headimgurl)) {
            // 向SD卡中写入图片缓存,然后由handler发消息去刷新界面的头像
            // TODO icon_temp的名字需要考虑下，用getUid()_avatar替代试试
            try {
                final String uri = headimgurl;
                MyLog.i("loadSync avatar1 url:" + uri);
                imageLoader.loadImage(uri, BitmapUtil.getImageLoaderOption(), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {
                                    MyLog.i("loadSync onLoadingStarted avatar1 url:" + uri);
                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        MyLog.i("loadSync onLoadingFailed avatar1 url:" + uri);
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        MyLog.i("loadSync onLoadingComplete avatar1 url:" + uri);
                        Bitmap avatar = imageLoader.loadImageSync(uri, BitmapUtil.getImageLoaderOption());
                        if (avatar == null) {
                            MyLog.i("loadSync avatar2 url:" + uri);
                            avatar = ImageLoader.getInstance().loadImageSync(uri, BitmapUtil.getImageLoaderOption());
                        }
                        if (avatar == null) {
                            MyLog.i("download1 avatar url:" + uri);
                            boolean b = HttpUtil.download(uri, BitmapUtil.getAvatarImagePath());
                            if (!b) {
                                MyLog.i("download2 avatar url:" + uri);
                                b = HttpUtil.download(uri, BitmapUtil.getAvatarImagePath());
                            }
                        } else {
                            BitmapUtil.saveUserAvatar(LoginActivity.this, bitmap);
                        }
                        MyLog.i("upload avatar url:" + uri);
                        MyLog.i("upload avatar avatar:" + avatar);
//                            String path = BitmapUtil.saveBitmapToSDCardString(LoginActivity.this, avatar, "avatar_wx", 100);
                        MyLog.i("upload avatar path:" + BitmapUtil.getAvatarImagePath());
                        LoginManager.getInstance().userUploadImage(BitmapUtil.getAvatarImagePath(), new IListener() {
                            @Override
                            public void onSuccess(Object obj) {
                                MyLog.i("upload avatar OK:" + obj.toString());
                            }

                            @Override
                            public void onNoNetwork() {
                                MyLog.i("upload avatar NG:" + getString(R.string.no_network));
                            }

                            @Override
                            public void onErr(Object object) {
                                MyLog.i("upload avatar NG:" + object.toString());
                            }
                        });
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {
//                                    MyLog.i("loadSync onLoadingCancelled avatar1 url:" + uri);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                MyLog.e(e);
            }
        }
    }

    private String getErrorInfo(Object object) {
        // error (出错信息)，-1 用户名密码错误 -2 第三方登录失败，无法创建用户 -3 提交信息有误，数据库保存失败
        StringBuilder sb = new StringBuilder();
        switch ((int) object) {
            case -1:
                sb.append(getString(R.string.login_input_err));
                break;
            case -2:
            case -3:
                sb.append(getString(R.string.login_failed));
                break;
        }
        return sb.toString();
    }

    private boolean checkInput() {
        username = login_username.getText().toString().replaceAll(" ", "");
        password = login_password.getText().toString().replaceAll(" ", "");
        if(StringUtil.isNullOrEmpty(username) || StringUtil.isNullOrEmpty(password)) {
            return false;
        }
        if(password.length() < 6) {
            return false;
        }
        if(!StringUtil.isCellphone(username) && !StringUtil.isEmail(username)) {
            return false;
        }
        return true;
    }

    private void togglePwdVisible() {
        int type = login_password.getInputType();
        if(type == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            login_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eye.setImageResource(R.drawable.login_eye_on);
        } else {
            login_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eye.setImageResource(R.drawable.login_eye);
        }
        login_password.setSelection(login_password.getText().length());
        login_password.setKeyListener(keyListener);
    }

    BaseKeyListener keyListener = new NumberKeyListener() {
        @Override
        protected char[] getAcceptedChars() {
            return StringUtil.passwordDigits();
        }

        @Override
        public int getInputType() {
            return login_password.getInputType();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        MyLog.i("onActivityResult " + ssoHandler);
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        MyLog.i("onResume ClientInfo.getUID()=" + ClientInfo.getUID());
//        if(!TextUtils.isEmpty(ClientInfo.getUID())) {
//            AppTools.toIntent(LoginActivity.this, MoinActivity.class);
//        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MyLog.i("onRestart ClientInfo.getUID()=" + ClientInfo.getUID());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLog.i("onPause ClientInfo.getUID()=" + ClientInfo.getUID());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i("onDestroy ClientInfo.getUID()=" + ClientInfo.getUID());
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyLog.i("onStart ClientInfo.getUID()=" + ClientInfo.getUID());
    }

    private void setLoginResult() {
        MyLog.i("setLoginResult.......mFrom="+mFrom);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);// 线程暂停1秒，单位毫秒
                    switch (mFrom) {
                        case LoginConstants.IP_DETAIL:
                            EventBus.getDefault().post(new LoginSuccJump.LoginSuccessEvent());
                            EventBus.getDefault().post(new LoginSuccJump.JumpIPDetailEvent());
                            break;
                        case LoginConstants.EMOJI_RESOURCE:
                            EventBus.getDefault().post(new LoginSuccJump.LoginSuccessEvent());
                            EventBus.getDefault().post(new LoginSuccJump.JumpEmojiResourceEvent(mAction));
                            break;
//                        case LoginConstants.WOWO_RULE:
//                            EventBus.getDefault().post(new LoginSuccJump.JumpWoRuleEvent());
//                            break;
                        case LoginConstants.WO_POST_LIST_NEWPOST:
                            EventBus.getDefault().post(new LoginSuccJump.LoginSuccessEvent());
                            EventBus.getDefault().post(new LoginSuccJump.JumpWoPostNewPostEvent());
                            break;
//                        case LoginConstants.POST_DETAIL_REPLY:
//                            EventBus.getDefault().post(new LoginSuccJump.JumpPostReplyEvent());
//                            break;
                        case LoginConstants.FRAGMENT_WOWO:
                        case LoginConstants.WO_POST_LIST_ATTENTION:
                        case LoginConstants.WOWO_RULE:
                        case LoginConstants.POST_DETAIL_COMMENT:
                        case LoginConstants.POST_DETAIL_REPLY:
                        case LoginConstants.MINE_EMOJI:
                        case LoginConstants.MINE_IP:
                        case LoginConstants.MINE_WOWO:
                        case LoginConstants.MINE_SETTINGS:
                        case LoginConstants.MINE_AVATAR:
                        case LoginConstants.FRAGMENT_MINE:
                            EventBus.getDefault().post(new LoginSuccJump.LoginSuccessEvent());
                            break;
                    }
                    LoginActivity.this.finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
