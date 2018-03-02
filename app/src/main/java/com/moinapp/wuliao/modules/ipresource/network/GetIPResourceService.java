package com.moinapp.wuliao.modules.ipresource.network;

import com.moinapp.wuliao.commons.net.AbsService;

import java.util.Map;

/**
 * Created by liujiancheng on 15/5/12.
 */
public class GetIPResourceService extends AbsService {
    public void GetBannerService(String url, Object tag) {
        getExecutor().submit(new GetBannerProtocal(url, tag));
    }

    public void GetHotTagService(String url, Map data, Object tag) {
        getExecutor().submit(new GetHotTagProtocal(url, data, tag));
    }

    public void SearchTagService(String url, Map data, int type, Object tag) {
        getExecutor().submit(new SearchTagProtocal(url, data, type, tag));
    }

    public void getIPListService(String url, int column, String lastid, Map data, Object tag) {
        getExecutor().submit(new GetIPListProtocal(url, column, lastid, data, tag));
    }

    public void getIPDetailService(String url, Map data, Object tag) {
        getExecutor().submit(new GetIPDetailProtocal(url, data, tag));
    }

    public void getHotIPService(String url, Object tag) {
        getExecutor().submit(new GetHotIPProtocal(url, tag));
    }

    public void getemojiService(String url, Map data, Object tag) {
        getExecutor().submit(new GetEmojiListProtocal(url, data, tag));
    }

    public void downloadEmojiService(String url, Map data, Object tag) {
        getExecutor().submit(new DownloadEmojiProtocal(url, data, tag));
    }

    public void favoriateIPService(String url, Map data, Object tag) {
        getExecutor().submit(new FavoriateIPProtocal(url, data, tag));
    }

    public void likeResourceService(String url, Map data, Object tag) {
        getExecutor().submit(new LikeResourceProtocal(url, data, tag));
    }
}
