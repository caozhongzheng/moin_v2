package com.moinapp.wuliao.modules.ipresource;

import com.moinapp.wuliao.commons.db.IDataTable;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moduleframework.AbsManager;
import com.moinapp.wuliao.commons.moduleframework.AbsModule;
import com.moinapp.wuliao.commons.moduleframework.IFeature;
import com.moinapp.wuliao.modules.ipresource.tables.BannerListCache;
import com.moinapp.wuliao.modules.ipresource.tables.EmojiListCacheTable;
import com.moinapp.wuliao.modules.ipresource.tables.IPListCacheTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liujiancheng on 15/5/13.
 */
public class IPResourceModule extends AbsModule {

    // ===========================================================
    // Constants
    // ===========================================================
    public static final String MODULE_NAME = "ip";
    // ===========================================================
    // Fields
    // ===========================================================
    private List<IDataTable> mTables;
    private List<IFeature> features;
    private static ILogger MyLog = LoggerFactory.getLogger(IPResourceModule.MODULE_NAME);
    // ===========================================================
    // Constructors
    // ===========================================================
    public IPResourceModule() {
        mTables = new ArrayList<IDataTable>();
        mTables.add(new IPListCacheTable());
        mTables.add(new EmojiListCacheTable());
        mTables.add(new BannerListCache());
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
