package com.moinapp.wuliao.modules.mine.listener;

import com.moinapp.wuliao.commons.net.NetworkingListener;
import com.moinapp.wuliao.modules.mine.model.EmojiCosPlay;

import java.util.List;

/**
 * Created by liujiancheng on 15/6/23.
 */
public interface CosplayListener extends NetworkingListener {
    public void createCosplaySucc(int result, String id);
    public void getCosplaySucc(List<EmojiCosPlay> cosPlayList);
    public void delCosplaySucc(int result);
}
