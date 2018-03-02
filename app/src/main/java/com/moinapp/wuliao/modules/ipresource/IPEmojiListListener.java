package com.moinapp.wuliao.modules.ipresource;

import com.moinapp.wuliao.commons.net.NetworkingListener;
import com.moinapp.wuliao.modules.ipresource.model.EmojiResource;

import java.util.List;

/**
 * Created by liujiancheng on 15/5/28.
 */
public interface IPEmojiListListener extends NetworkingListener {
    public void onGetEmojiListSucc(List<EmojiResource> emojiList, List<EmojiResource> suggest);
}
