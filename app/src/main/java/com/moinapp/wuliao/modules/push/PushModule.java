package com.moinapp.wuliao.modules.push;

import com.moinapp.wuliao.commons.db.IDataTable;
import com.moinapp.wuliao.commons.moduleframework.AbsManager;
import com.moinapp.wuliao.commons.moduleframework.AbsModule;
import com.moinapp.wuliao.commons.moduleframework.IFeature;
import com.moinapp.wuliao.modules.push.features.PushFeature;
import com.moinapp.wuliao.modules.push.table.PushCacheTable;

import java.util.ArrayList;
import java.util.List;

public class PushModule extends AbsModule {

	// ===========================================================
	// Constants
	// ===========================================================
	public static final String MODULE_NAME = "Push";
	// ===========================================================
	// Fields
	// ===========================================================
	private List<IDataTable> mTables;
	private List<IFeature> features;
    private PushPreference mPreference;

	// ===========================================================
	// Constructors
	// ===========================================================
	public PushModule() {
		mTables = new ArrayList<IDataTable>();
		mTables.add(new PushCacheTable());

		features = new ArrayList<IFeature>();
		features.add(new PushFeature());
		
		mPreference = PushPreference.getInstance();
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
	    mPreference.setPushEnable(enabled);
		AbsManager mgr = PushManager.getInstance();
		if (enabled) {
			mgr.init();
		} else {
			mgr.onDisable();
		}
	}
	public void init() {
		PushManager.getInstance().init();
	}
    @Override
    public boolean canEnabled() {
        return mPreference.isPushEnable();
    }
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
