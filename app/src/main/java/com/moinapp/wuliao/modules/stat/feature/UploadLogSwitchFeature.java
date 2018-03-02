package com.moinapp.wuliao.modules.stat.feature;

import com.moinapp.wuliao.commons.moduleframework.AbsSwitchFeature;
import com.moinapp.wuliao.commons.moduleframework.IModule;
import com.moinapp.wuliao.commons.moduleframework.ModuleContainer;
import com.moinapp.wuliao.modules.stat.StatModule;
import com.moinapp.wuliao.modules.stat.StatPreference;

public class UploadLogSwitchFeature extends AbsSwitchFeature {
	
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int FEATURE = 125;
	
	// ===========================================================
	// Fields
	// ===========================================================
	private StatPreference mPreference;
	
	// ===========================================================
	// Constructor
	// ===========================================================
	public UploadLogSwitchFeature() {
		mPreference = StatPreference.getInstance();
	};
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public int getFeatureId() {
		return FEATURE;
	}

	@Override
	protected IModule getModule() {
		return ModuleContainer.getInstance().getModuleByName(StatModule.MODULE_NAME);
	}

	@Override
	public boolean isEnabled() {
		return mPreference.isUploadLogEnabled();
	}
	
	@Override
	public void enableFeature() {
		mPreference.setUploadLogEnabled(true);
	}
	
	@Override
	public void disableFeature() {
		mPreference.setUploadLogEnabled(false);		
	}
}
