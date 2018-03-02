package com.moinapp.wuliao.modules.mine.listener;

import com.moinapp.wuliao.commons.net.NetworkingListener;
import com.moinapp.wuliao.modules.ipresource.model.EmojiResource;

import java.util.List;

/**
 * Created by liujiancheng on 15/6/23.
 */
public interface MyEmojiListener extends NetworkingListener {
    public void getMyEmojiSucc(List<EmojiResource> emojis);
    public void delEmojiSucc(int result);
}
