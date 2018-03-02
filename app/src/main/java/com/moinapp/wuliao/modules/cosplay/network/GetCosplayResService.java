package com.moinapp.wuliao.modules.cosplay.network;

import com.moinapp.wuliao.commons.net.AbsService;

import java.util.Map;

public class GetCosplayResService extends AbsService {
    public void GetCosplayService(String url, Map data, Object tag) {
        getExecutor().submit(new GetCosplayResProtocal(url, data, tag));
    }
}