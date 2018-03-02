package com.moinapp.wuliao.modules.mine.listener;

import com.moinapp.wuliao.commons.net.NetworkingListener;

import java.util.List;

/**
 * Created by liujiancheng on 15/6/23.
 */
public interface UserTypeListener extends NetworkingListener {
    /**
     * 1 常规用户 2 第三方用户微信 3 第三方用户QQ
     * @param type
     */
    public void getUserTypeSucc(int type, List<String> namelist);
}
