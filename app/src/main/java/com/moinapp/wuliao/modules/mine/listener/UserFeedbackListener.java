package com.moinapp.wuliao.modules.mine.listener;

import com.moinapp.wuliao.commons.net.NetworkingListener;

/**
 * Created by liujiancheng on 15/6/23.
 */
public interface UserFeedbackListener extends NetworkingListener {
    public void userFeedbackSucc(int result);
}
