package com.moinapp.wuliao.modules.domainupdate;

import com.moinapp.wuliao.commons.db.IDataTable;
import com.moinapp.wuliao.commons.moduleframework.AbsModule;
import com.moinapp.wuliao.commons.moduleframework.IFeature;
import com.moinapp.wuliao.modules.domainupdate.features.DomainFeature;
import com.moinapp.wuliao.modules.domainupdate.table.DomainTable;

import java.util.ArrayList;
import java.util.List;

public class DomainModule extends AbsModule{

    // ===========================================================
    // Constants
    // ===========================================================

    public static final String MODULE_NAME = "Domain";

    // ===========================================================
    // Fields
    // ===========================================================

    private List<IDataTable> mTables = new ArrayList<IDataTable>();
    private List<IFeature> mFeatures = new ArrayList<IFeature>();

    // ===========================================================
    // Constructors
    // ===========================================================

    public DomainModule(){
        mTables.add(new DomainTable());
        mFeatures.add(new DomainFeature());
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    protected void onEnabled(boolean enabled) {
    	DomainManager mgr = DomainManager.getInstance();
		if (enabled) {
			mgr.init();
		} else {
			mgr.onDisable();
		}
    }

    @Override
    public void init() {
    	DomainManager.getInstance().init();
    }

    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Override
    public boolean canEnabled() {
        return true;
    }

    @Override
    public List<IFeature> getFeatures() {
        return mFeatures;
    }

    @Override
    public List<IDataTable> getTables() {
        return mTables;
    }

    @Override
    public void onAppFirstInit(boolean enabled) {
        DomainManager.getInstance().firstInit();
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
