package com.moinapp.wuliao.modules.cosplay.listener;

import com.moinapp.wuliao.commons.net.NetworkingListener;
import com.moinapp.wuliao.modules.cosplay.model.CosplayResource;

import java.util.List;

/**
 * Created by liujiancheng on 15/6/30.
 */
public interface GetCosplayResListener extends NetworkingListener {
    public void getCosplaySucc(List<CosplayResource> cosplayResourceList);
}
