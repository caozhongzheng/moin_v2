package com.moinapp.wuliao.modules.mine.network;

import com.moinapp.wuliao.commons.net.AbsService;

import java.util.Map;

/**
 * Created by liujiancheng on 15/6/23.
 */
public class MineService extends AbsService {
    public void createCosplayService(String url,Map data, Object tag) {
        getExecutor().submit(new CreateCosplayProtocal(url, data, tag));
    }

    // 联调接口pass，返回了cosplay list，list里无数据
    public void getCosplayService(String url, Object tag) {
        getExecutor().submit(new GetCosplayProtocal(url, tag));
    }

    // 联调pass，返回result ＝1
    public void delCosplayService(String url, Map data, Object tag) {
        getExecutor().submit(new DelCosplayProtocal(url, data, tag));
    }

    // 联调接口pass
    public void getMyEmojiService(String url, Object tag) {
        getExecutor().submit(new GetMyEmojiProtocal(url, tag));
    }

    // 联调接口pass
    public void delMyemojiService(String url, Map data, Object tag) {
        getExecutor().submit(new DelMyEmojiProtocal(url, data, tag));
    }

    // 联调接口pass
    public void getFavoriateIPService(String url, Object tag) {
        getExecutor().submit(new GetFavoriateIPProtocal(url, tag));
    }

    // 联调接口pass
    public void delFavoriateIPService(String url, Map data, Object tag) {
        getExecutor().submit(new DelFavoriateIPProtocal(url, data, tag));
    }

    // 联调接口pass
    public void getMyPostService(String url, Object tag) {
        getExecutor().submit(new GetMyPostProtocal(url, tag));
    }

    public void delMyPostService(String url, Map data, Object tag) {
        getExecutor().submit(new DelMyPostProtocal(url, data, tag));
    }

    // 联调接口pass
    public void getMyReplyService(String url, Object tag) {
        getExecutor().submit(new GetMyReplyProtocal(url, tag));
    }

    // 联调接口pass
    public void delMyReplyService(String url, Map data, Object tag) {
        getExecutor().submit(new DelMyReplyProtocal(url, data, tag));
    }

    // 联调接口pass
    public void getUserTypeService(String url, Object tag) {
        getExecutor().submit(new GetUserTypeProtocal(url, tag));
    }

    // 联调接口pass
    public void userFeedbackService(String url, Map data, Object tag) {
        getExecutor().submit(new UserFeedbackProtocal(url, data, tag));
    }
}
