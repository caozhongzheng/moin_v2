package com.moinapp.wuliao.modules.mine.listener;

import com.moinapp.wuliao.commons.net.NetworkingListener;
import com.moinapp.wuliao.modules.ipresource.model.IPResource;

import java.util.List;

/**
 * Created by liujiancheng on 15/6/23.
 */
public interface FavoriateIPListener extends NetworkingListener {
    public void getFavoriateIPSucc(List<IPResource> ips);
    public void delFavoriateIPSucc(int result);
}
