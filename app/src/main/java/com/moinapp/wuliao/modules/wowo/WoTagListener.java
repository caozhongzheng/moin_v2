package com.moinapp.wuliao.modules.wowo;

import com.moinapp.wuliao.commons.net.NetworkingListener;

import java.util.List;

/**
 * Created by liujiancheng on 15/6/25.
 */
public interface WoTagListener extends NetworkingListener {
    public void onGetWoTagSucc(List<String> tags);
}
