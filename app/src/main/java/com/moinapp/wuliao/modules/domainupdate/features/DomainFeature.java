package com.moinapp.wuliao.modules.domainupdate.features;

import com.moinapp.wuliao.commons.moduleframework.AbsFeature;
import com.moinapp.wuliao.commons.moduleframework.AbsUpdateActionHandler;
import com.moinapp.wuliao.commons.moduleframework.IUpdateActionHandler;
import com.moinapp.wuliao.modules.domainupdate.DomainManager;

import java.util.Map;

public class DomainFeature extends AbsFeature{

    // ===========================================================
    // Constants
    // ===========================================================

    private final int FEATURE_ID = 1119;

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
    public int getFeatureId() {
        return FEATURE_ID;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public IUpdateActionHandler getHandler() {
        return new UpdateActionHandler();
    }

    @Override
    public String getVersionTag() {
        return DomainManager.getInstance().getVersion();
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    private class UpdateActionHandler extends AbsUpdateActionHandler {

        @Override
        public void updateDomain(Map<String, String> keyValues) {
            DomainManager.getInstance().updateDomain(keyValues);
        }
    }
}
