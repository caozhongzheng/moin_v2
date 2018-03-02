package com.moinapp.wuliao.modules.login;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moduleframework.AbsManager;
import com.moinapp.wuliao.commons.net.IListener;
import com.moinapp.wuliao.commons.net.Listener;
import com.moinapp.wuliao.modules.login.model.BaseLoginResult;
import com.moinapp.wuliao.modules.login.model.UserInfo;
import com.moinapp.wuliao.modules.login.network.GetUserLoginService;
import com.moinapp.wuliao.modules.login.network.GetUserUpdateService;
import com.moinapp.wuliao.modules.login.network.UserCheckEmailProtocal;
import com.moinapp.wuliao.modules.login.network.UserCheckPhoneProtocal;
import com.moinapp.wuliao.modules.login.network.UserGetEmailCodeProtocal;
import com.moinapp.wuliao.modules.login.network.UserGetInfoProtocal;
import com.moinapp.wuliao.modules.login.network.UserGetSmsCodeProtocal;
import com.moinapp.wuliao.modules.login.network.UserLoginProtocal;
import com.moinapp.wuliao.modules.login.network.UserLogoutProtocal;
import com.moinapp.wuliao.modules.login.network.UserRegisterProtocal;
import com.moinapp.wuliao.modules.login.network.UserRetrievePasswordProtocal;
import com.moinapp.wuliao.modules.login.network.UserUpdatePasswordProtocal;
import com.moinapp.wuliao.modules.login.network.UserUpdateProtocal;
import com.moinapp.wuliao.modules.login.network.UserUploadImageProtocal;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liujiancheng on 15/5/4.
 */
public class LoginManager extends AbsManager
{
    // ===========================================================
    // Fields
    // ===========================================================
    private static ILogger MyLog = LoggerFactory.getLogger(LoginModule.MODULE_NAME);
    private static LoginManager mInstance;
    private Context mContext;
    private Gson mGson = new Gson();

    // ===========================================================
    // Constructors
    // ===========================================================
    private LoginManager() {
        mContext = ApplicationContext.getContext();
    }

    public static synchronized LoginManager getInstance() {
        if (mInstance == null) {
            mInstance = new LoginManager();
        }

        return mInstance;
    }

    // ===========================================================
    // Methods
    // ===========================================================
    @Override
    public void init() {
        MyLog.i("init is coming.....");
        EventBus.getDefault().register(this);
    }

    /**
     * 注册用户的接口方法
     * @param type： 0表示电话注册 1表示邮箱注册
     * @param name: 电话或邮箱二选一
     * @param md5: 密码的md5
     * @param smscode: 短信码
     */
    public void registerUser(int type, String name, String md5, String smscode,Listener listener) {
        Map data = LoginUtils.registerInfo2Map(type, name, md5, smscode);
        new GetUserLoginService().UserRegisterService(LoginConstants.LOGIN_REGISTER_URL, data, listener);
    }

    /**
     * 魔映自有用户的登录接口
     * @param name： 手机号邮箱二选一
     * @param md5：  密码的md5
     */
    public void userOurLogin(String name, String md5, Listener listener) {
        Map data = LoginUtils.ourLogin2Map(name, md5);
        new GetUserLoginService().UserLoginService(LoginConstants.LOGIN_URL, data, listener);
    }

    /**
     * 第三方用户的登录接口
     * @param token： 第三方登录的token
     * @param token：第三方登录的openid（微信）
     */
    public void userThirdLogin(String platform, Object token, Listener listener) {
        Map data = LoginUtils.thirdLogin2Map(platform, token);
        new GetUserLoginService().UserLoginService(LoginConstants.LOGIN_URL, data, listener);
    }

    /**
     * 修改用户信息的接口
     * @param info: 用户更新的信息
     */
    public void userUpdate(UserInfo info, Listener listener) {
        Map data = LoginUtils.userInfo2Map(info) ;
        new GetUserUpdateService().UserUpdateService(LoginConstants.LOGIN_UPDATE_URL + ClientInfo.getUID(), data, listener);
    }

    /**
     * 获取短信验证码的接口
     * @param phone: 用户的电话号码
     *             @param listener callback
     */
    public void getSmsCode(String phone, Listener listener) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(LoginConstants.PHONE, phone);
        new GetUserLoginService().UserGetSmsCodeService(LoginConstants.LOGIN_GET_SMSCODE_URL, data, listener);
    }

    /**
     * 获取邮箱验证码的接口
     * @param email: 用户的邮箱地址
     * @param listener callback
     */
    public void getEmailCode(String email, Listener listener) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(LoginConstants.EMAIL, email);
        new GetUserLoginService().UserGetEmailCodeService(LoginConstants.LOGIN_GET_EMAILCODE_URL, data, listener);
    }

    /**
     * 获取电话号码是否注册的接口
     * @param phone: 用户的电话号码
     * @param listener callback
     */
    public void userCheckPhone(String phone, Listener listener) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(LoginConstants.PHONE, phone);
        new GetUserLoginService().UserCheckPhoneService(LoginConstants.LOGIN_CHECK_PHONE_URL, data, listener);
    }

    /**
     * 获取邮箱唯一性的接口
     * @param email: 用户的邮箱地址
     * @param listener callback
     */
    public void userCheckmail(String email, Listener listener) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(LoginConstants.EMAIL, email);
        new GetUserLoginService().UserCheckEmailService(LoginConstants.LOGIN_CHECK_EMAIL_URL, data, listener);
    }

    /**
     * 通过电话找回密码请求的接口
     * @param phone: 用户的电话号码
     * @param smscode: 短信验证码
     */
    public void retrievePasswordByPhone(String phone, String smscode, Listener listener) {
        Map data = LoginUtils.phoneRetievePassword2Map(phone, smscode);
        new GetUserLoginService().UserRetrievePasswordService(LoginConstants.LOGIN_RETRIEVE_PASSWORD_URL, data, listener);
    }

    /**
     * 通过邮箱找回密码请求的接口
     * @param email: 用户的邮箱
     * @param emailcode: 邮箱验证码
     */
    public void retrievePasswordByEmail(String email, String emailcode, Listener listener) {
        Map data = LoginUtils.emailRetievePassword2Map(email, emailcode);
        new GetUserLoginService().UserRetrievePasswordService(LoginConstants.LOGIN_RETRIEVE_PASSWORD_URL, data, listener);
    }

    /**
     * 用户退出登录请求的接口
     * @param password: 新密码的md5
     * @param listener: callback
     */
    public void userUpdatePassword(String password,Listener listener) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(LoginConstants.PASSWORD, password);
        new GetUserUpdateService().UserUpdatePasswordService(LoginConstants.LOGIN_UPDATE_PASSWORD_URL, data, listener);
    }

    /**
     * 用户退出登录请求的接口
     */
    public void userLogout(Listener listener) {
        new GetUserLoginService().UserLogoutService(LoginConstants.LOGOUT_URL, null, listener);
    }

    /**
     * 用户上传图像的接口
     */
    public void userUploadImage(String filePath, Listener listener) {
        new GetUserUpdateService().UserUploadImageService(LoginConstants.LOGIN_UPLOAD_URL, filePath, listener);
    }

    /**
     * 获取用户信息的接口
     */
    public void getUserInfo(Listener listener) {
//        new GetUserUpdateService().UserGetInfoervice(LoginConstants.LOGIN_BASE_URL + ClientInfo.getUID(), listener);
        new GetUserUpdateService().UserGetInfoervice(LoginConstants.LOGIN_GET_INFO_URL, listener);
    }

    //==============================接受各个服务器接口协议的回调======================================//
    public void onEvent(UserRegisterProtocal.GetRegisterSuccessEvent event) {
        String response = event.getResponse();
        MyLog.i("User register received register succeed info: response =" + response.toString());
        processResultWithUid(response, event.getTag());
    }

    public void onEvent(UserLoginProtocal.GetLoginSuccessEvent event) {
        String response = event.getResponse();
        MyLog.i("User login received login succeed info: response =" + response);
        processResultWithUid(response, event.getTag());
    }

    public void onEvent(UserUpdateProtocal.GetUpdateSuccessEvent event) {
        String response = event.getResponse();
        MyLog.i("User update received update succeed info: response =" + response);
        processUserUpdate(response, event.getTag());
    }

    public void onEvent(UserGetSmsCodeProtocal.GetSmsCodeSuccessEvent event) {
        String response = event.getResponse();
        MyLog.i("Get sms code received succeed info: response =" + response);
        processResult(response, event.getTag());
    }

    public void onEvent(UserGetEmailCodeProtocal.GetEmailCodeSuccessEvent event) {
        String response = event.getResponse();
        MyLog.i("Get email code received succeed info: response =" + response);
        processResult(response, event.getTag());
    }

    public void onEvent(UserCheckPhoneProtocal.GetCheckPhoneSuccessEvent event) {
        String response = event.getResponse();
        MyLog.i("Check phone received succeed info: response =" + response);
        processResult(response, event.getTag());
    }

    public void onEvent(UserCheckEmailProtocal.GetCheckEmailSuccessEvent event) {
        String response = event.getResponse();
        MyLog.i("Check email received succeed info: response =" + response);
        processResult(response, event.getTag());
    }

    public void onEvent(UserRetrievePasswordProtocal.RetrievePasswordSuccessEvent event) {
        String response = event.getResponse();
        MyLog.i("Find password received succeed info: response =" + response);
        processResultWithUid(response, event.getTag());
    }

    public void onEvent(UserUpdatePasswordProtocal.GetUpdatePasswordSuccessEvent event) {
        String response = event.getResponse();
        MyLog.i("update password received succeed info: response =" + response);
        processResult(response, event.getTag());
    }

    public void onEvent(UserLogoutProtocal.GetLogoutSuccessEvent event) {
        String response = event.getResponse();
        //退出登录候清除本地uid和passport
        ClientInfo.setUID(null);
        ClientInfo.setPassport(null);

        MyLog.i("log out received succeed info: response =" + response);
        processResult(response, event.getTag());
    }

    public void onEvent(UserUploadImageProtocal.GetUploadImageSuccessEvent event) {
        String response = event.getResponse();
        MyLog.i("User update received update succeed info: response =" + response);
        processUploadImageResult(response, event.getTag());
    }

    public void onEvent(UserGetInfoProtocal.GetUserInfoSuccessEvent event) {
        String response = event.getResponse();
        MyLog.i("get user info received succeed info: response =" + response);
        processGetUserInfoResult(response, event.getTag());
    }

    private BaseLoginResult getResultFromResponse(String response) {
        return mGson.fromJson(response, BaseLoginResult.class);
    }

    private BaseLoginResult processResult(String response, Object listener) {
        BaseLoginResult result = getResultFromResponse(response);
        if (result != null) {
            MyLog.i("result=" + result.getResult());
            if(result.getResult() > 0) {
                ((IListener) listener).onSuccess(result.getResult());
            } else if (result.getResult() == 0) {
                ((IListener) listener).onErr(result.getError());
            }

        }
        return result;
    }

    private void processResultWithUid(String response, Object listener) {
        BaseLoginResult result = processResult(response, listener);
        if (result != null) {
            //处理注册返回信息
            if(result.getResult() > 0) {
                //TDDO 正查情况下服务器应该返回不为空的uid和passport，但是发现有时候会为空，所以加上了这行判断
                if (!TextUtils.isEmpty(result.getUid())) {
                    ClientInfo.setUID(result.getUid());
                    MyLog.i("write uid=" + result.getUid());
                }
                if (!TextUtils.isEmpty(result.getPassport())) {
                    ClientInfo.setPassport(result.getPassport());
                    MyLog.i("write write passport=" + result.getPassport());
                }
            }
        }
    }

    private void processUserUpdate(String response, Object listener) {
        UserUpdateProtocal.UserUpdateResult result = mGson.fromJson(response, UserUpdateProtocal.UserUpdateResult.class);
        if (result != null) {
            MyLog.i("user update result=" + result.getResult());
            if(result.getResult() > 0) {
                ((IListener) listener).onSuccess(result.getUser());
            } else if (result.getResult() == 0) {
                ((IListener) listener).onErr(result.getError());
            }

        }
    }

    private void processUploadImageResult(String response, Object listener) {
        UserUploadImageProtocal.UploadImageResult result = mGson.fromJson(response, UserUploadImageProtocal.UploadImageResult.class);
        if (result != null) {
            MyLog.i("user update result=" + result.getResult());
            if(result.getResult() > 0) {
                ((IListener) listener).onSuccess(result.getUrl());
            } else if (result.getResult() == 0) {
                ((IListener) listener).onErr(result.getError());
            }

        }
    }

    private void processGetUserInfoResult(String response, Object listener) {
        UserGetInfoProtocal.GetUserInfoResponse res = mGson.fromJson(response, UserGetInfoProtocal.GetUserInfoResponse.class);

        if (res != null) {
            try {
                MyLog.i("userinfo:"+res.getUser().getNickname());
            } catch (Exception e) {
                e.printStackTrace();
            }
            ((IListener) listener).onSuccess(res.getUser());
        } else {
            ((IListener) listener).onErr(-1);
        }
    }
}
