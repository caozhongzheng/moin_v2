package com.moinapp.wuliao.modules.stat.feature;

import com.moinapp.wuliao.commons.moduleframework.AbsSwitchFeature;
import com.moinapp.wuliao.commons.moduleframework.IModule;
import com.moinapp.wuliao.commons.moduleframework.ModuleContainer;
import com.moinapp.wuliao.modules.stat.StatModule;
import com.moinapp.wuliao.modules.stat.StatPreference;

public class UploadPackageSwitchFeature extends AbsSwitchFeature {
	
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int FEATURE = 126;
	
	// ===========================================================
	// Fields
	// ===========================================================
	private StatPreference mPreference;
	
	// ===========================================================
	// Constructor
	// ===========================================================
	public UploadPackageSwitchFeature() {
		mPreference = StatPreference.getInstance();
	};
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public int getFeatureId() {
		// TODO Auto-generated method stub
		return FEATURE;
	}

	@Override
	protected IModule getModule() {
		// TODO Auto-generated method stub
		return ModuleContainer.getInstance().getModuleByName(StatModule.MODULE_NAME);
	}
	
	@Override
	public boolean isEnabled() {
		return mPreference.isUploadPackageEnabled();
	}

	@Override
	public void enableFeature() {
		mPreference.setUploadPackageEnabled(true);
	}
	
	@Override
	public void disableFeature() {
		mPreference.setUploadPackageEnabled(false);		
	}
}