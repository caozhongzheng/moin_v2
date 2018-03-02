package com.moinapp.wuliao.modules.wowo;

import com.moinapp.wuliao.commons.db.IDataTable;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moduleframework.AbsManager;
import com.moinapp.wuliao.commons.moduleframework.AbsModule;
import com.moinapp.wuliao.commons.moduleframework.IFeature;
import com.moinapp.wuliao.modules.ipresource.IPResourceManager;
import com.moinapp.wuliao.modules.ipresource.tables.IPListCacheTable;
import com.moinapp.wuliao.modules.wowo.tables.WoListCacheTable;
import com.moinapp.wuliao.modules.wowo.tables.WoSuggestPostTable;

import java.util.ArrayList;
import java.util.List;

public class WowoModule extends AbsModule {

    // ===========================================================
    // Constants
    // ===========================================================
    public static final String MODULE_NAME = "wowo";
    // ===========================================================
    // Fields
    // ===========================================================
    private List<IDataTable> mTables;
    private List<IFeature> features;
    private static ILogger MyLog = LoggerFactory.getLogger(WowoModule.MODULE_NAME);
    // ===========================================================
    // Constructors
    // ===========================================================
    public WowoModule() {
        mTables = new ArrayList<IDataTable>();
        mTables.add(new WoSuggestPostTable());
        mTables.add(new WoListCacheTable());
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
        MyLog.i("onEnabled=" + enabled);
        AbsManager mgr = IPResourceManager.getInstance();
        if (enabled) {
            mgr.init();
        } else {
            mgr.onDisable();
        }
    }

    public void init() {
        IPResourceManager.getInstance().init();
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
