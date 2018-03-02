package com.moinapp.wuliao.modules.ipresource;

import com.moinapp.wuliao.commons.net.NetworkingListener;
import com.moinapp.wuliao.modules.ipresource.model.IPResource;

import java.util.List;

/**
 * Created by moying on 15/5/14.
 */
public interface IPresListListener extends NetworkingListener {
    public void onGetIPListSucc(int column, String lastid, List<IPResource> ipResourceList);
    public void onGetHotIPSucc(IPResource ipResource);
}
