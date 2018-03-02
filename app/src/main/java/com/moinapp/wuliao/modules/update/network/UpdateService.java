package com.moinapp.wuliao.modules.update.network;

import com.moinapp.wuliao.commons.net.AbsService;
import com.moinapp.wuliao.commons.net.UpdateListener;

import java.util.Map;

public class UpdateService extends AbsService{

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    public void checkUpdate(String url, Map data, UpdateListener listener){
        getExecutor().submit(new UpdateProtocol(url, data, listener));
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
