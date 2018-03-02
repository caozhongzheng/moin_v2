package com.moinapp.wuliao.modules.wowo.network;

import com.moinapp.wuliao.commons.net.AbsService;

import java.util.Map;

/**
 * Created by liujiancheng on 15/6/8.
 */
public class WowoNetService extends AbsService {
    public void getMyWoListService(String url, Object tag) {
        getExecutor().submit(new GetMyWoListProtocal(url, tag));
    }

    public void getSuggestWoListService(String url, Map data, Object tag) {
        getExecutor().submit(new GetSuggestWoListProtocal(url, data, tag));
    }

    public void subScribeWoService(String url, Map data, Object tag) {
        getExecutor().submit(new SubscribeWoProtocal(url, data, tag));
    }

    public void getSuggestPostService(String url, Map data, Object tag) {
        getExecutor().submit(new GetSuggestPostProtocal(url, data, tag));
    }

    public void getWoPostService(String url, Map data, int filter, Object tag) {
        getExecutor().submit(new GetWoPostProtocal(url, data, filter,tag));
    }

    public void getIPPostService(String url, Map data, Object tag) {
        getExecutor().submit(new GetIPPostProtocal(url, data, tag));
    }

    public void newPostService(String url, Map data, Object tag) {
        getExecutor().submit(new NewPostProtocal(url, data, tag));
    }

    public void UploadFileService(String url, String filePath, Object tag) {
        getExecutor().submit(new UploadImageProtocal(url, filePath, tag));
    }

    public void ReplyPostService(String url, Map data, Object tag) {
        getExecutor().submit(new ReplyPostProtocal(url, data, tag));
    }

    public void getPostDetailService(String url, Map data, Object tag) {
        getExecutor().submit(new GetPostDetailProtocal(url, data, tag));
    }

    public void getWoTagService(String url, Map data, Object tag) {
        getExecutor().submit(new GetWoTagProtocal(url, data, tag));
    }
}