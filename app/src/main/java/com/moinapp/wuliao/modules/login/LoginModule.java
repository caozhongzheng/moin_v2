package com.moinapp.wuliao.modules.login;

/**
 * Created by liujiancheng on 15/5/5.
 */

import com.moinapp.wuliao.commons.db.IDataTable;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moduleframework.AbsManager;
import com.moinapp.wuliao.commons.moduleframework.AbsModule;
import com.moinapp.wuliao.commons.moduleframework.IFeature;

import java.util.List;

public class LoginModule extends AbsModule {

    // ===========================================================
    // Constants
    // ===========================================================
    public static final String MODULE_NAME = "Login";
    // ===========================================================
    // Fields
    // ===========================================================
    private List<IDataTable> mTables;
    private List<IFeature> features;
    private static ILogger MyLog = LoggerFactory.getLogger(LoginModule.MODULE_NAME);
    // ===========================================================
    // Constructors
    // ===========================================================
    public LoginModule() {
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public List<IFeature> getFeatures() {
        return features;
    }

    @Override
    public List<IDataTable> getTables() {
        return mTables;
    }

    @Override
    protected void onEnabled(boolean enabled) {
        MyLog.i("onEnabled="+enabled);
        AbsManager mgr = LoginManager.getInstance();
        if (enabled) {
            mgr.init();
        } else {
            mgr.onDisable();
        }
    }
    public void init() {
        LoginManager.getInstance().init();
    }
    @Override
    public boolean canEnabled() {
        return true;
    }
    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
