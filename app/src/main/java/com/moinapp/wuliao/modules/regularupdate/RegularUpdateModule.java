package com.moinapp.wuliao.modules.regularupdate;

import com.moinapp.wuliao.commons.db.IDataTable;
import com.moinapp.wuliao.commons.moduleframework.AbsManager;
import com.moinapp.wuliao.commons.moduleframework.AbsModule;
import com.moinapp.wuliao.commons.moduleframework.IFeature;
import com.moinapp.wuliao.modules.regularupdate.features.RegularupdateUpdateFeature;

import java.util.ArrayList;
import java.util.List;

public class RegularUpdateModule extends AbsModule {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String MODULE_NAME = "RegularUpdate";
	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================
	private List<IFeature> features;

	public RegularUpdateModule() {
		features = new ArrayList<IFeature>(5);
		features.add(new RegularupdateUpdateFeature());
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
	protected void onEnabled(boolean enabled) {
		AbsManager mgr = RegularUpdateManager.getInstance();
		if (enabled) {
			mgr.init();
		} else {
			// do nothing. won't disable regularUpdate
		}
	}
	public void init() {
		RegularUpdateManager.getInstance().init();
	}
	@Override
	public boolean canEnabled() {
		return true;
	}

	@Override
	public String getLogTag() {
		return MODULE_NAME;
	}

	@Override
	public List<IFeature> getFeatures() {
		return features;
	}

	@Override
	public List<IDataTable> getTables() {
		return null;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
