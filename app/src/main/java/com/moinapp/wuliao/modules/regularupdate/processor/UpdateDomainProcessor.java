package com.moinapp.wuliao.modules.regularupdate.processor;

import android.text.TextUtils;
import com.moinapp.wuliao.commons.moduleframework.IUpdateActionHandler;
import com.moinapp.wuliao.modules.regularupdate.model.UpdateAction;
import com.moinapp.wuliao.utils.UriQueryUtils;

import java.util.Map;

public class UpdateDomainProcessor implements IUpdateActionProcessor{

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

    @Override
    public void process(Map<Integer, IUpdateActionHandler> handlers, UpdateAction action) {
        Map<String, String> keyValues = UriQueryUtils.parse(action.getParameters());
        String f = keyValues.get("featureId");
        if (f==null||!TextUtils.isDigitsOnly(f)) {
            return;
        }

        int featureId = Integer.parseInt(f);
        IUpdateActionHandler handler = handlers.get(featureId);
        if (handler != null) {
            handler.updateDomain(keyValues);
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
