package com.moinapp.wuliao.modules.login.network;

import com.moinapp.wuliao.commons.net.AbsService;

import java.util.Map;

/**
 * Created by liujiancheng on 15/5/4.
 */
public class GetUserLoginService extends AbsService{
    public void UserLoginService(String url, Map data, Object tag) {
        getExecutor().submit(new UserLoginProtocal(url, data, tag));
    }

    public void UserGetEmailCodeService(String url, Map data, Object tag) {
        getExecutor().submit(new UserGetEmailCodeProtocal(url, data, tag));
    }

    public void UserGetSmsCodeService(String url, Map data, Object tag) {
        getExecutor().submit(new UserGetSmsCodeProtocal(url, data, tag));
    }

    public void UserCheckEmailService(String url, Map data, Object tag) {
        getExecutor().submit(new UserCheckEmailProtocal(url, data, tag));
    }

    public void UserCheckPhoneService(String url, Map data, Object tag) {
        getExecutor().submit(new UserCheckPhoneProtocal(url, data, tag));
    }

    public void UserLogoutService(String url, Map data, Object tag) {
        getExecutor().submit(new UserLogoutProtocal(url, data, tag));
    }

    public void UserRegisterService(String url, Map data, Object tag) {
        getExecutor().submit(new UserRegisterProtocal(url, data, tag));
    }

    public void UserRetrievePasswordService(String url, Map data, Object tag) {
        getExecutor().submit(new UserRetrievePasswordProtocal(url, data, tag));
    }
}
