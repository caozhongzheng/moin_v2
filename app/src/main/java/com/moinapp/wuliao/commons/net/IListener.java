package com.moinapp.wuliao.commons.net;

/**
 * Created by moying on 15/5/8.
 */
public interface IListener extends Listener {
    void onSuccess(Object obj);
    void onNoNetwork();
}
