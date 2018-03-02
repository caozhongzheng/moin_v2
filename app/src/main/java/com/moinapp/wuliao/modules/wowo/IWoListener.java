package com.moinapp.wuliao.modules.wowo;


import com.moinapp.wuliao.commons.net.NetworkingListener;
import com.moinapp.wuliao.modules.wowo.model.WowoInfo;

import java.util.List;

/**
 * Created by liujiancheng on 15/6/8.
 */
public interface IWoListener extends NetworkingListener {
    public void onGetWowoSucc(List<WowoInfo> wowoInfoList);
    public void onSubscribeResult(int result);
}
