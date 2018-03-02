package com.moinapp.wuliao.modules.login.network;

import com.moinapp.wuliao.commons.net.AbsService;

import java.util.Map;

/**
 * Created by liujiancheng on 15/5/4.
 */
public class GetUserUpdateService extends AbsService {
    public void UserUpdateService(String url, Map data, Object tag) {
        getExecutor().submit(new UserUpdateProtocal(url, data, tag));
    }

    public void UserUpdatePasswordService(String url, Map data, Object tag) {
        getExecutor().submit(new UserUpdatePasswordProtocal(url, data, tag));
    }

    public void UserUploadImageService(String url, String filePath, Object tag) {
        getExecutor().submit(new UserUploadImageProtocal(url, filePath, tag));
    }

    public void UserGetInfoervice(String url, Object tag) {
        getExecutor().submit(new UserGetInfoProtocal(url, tag));
    }
}
