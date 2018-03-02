package com.moinapp.wuliao.modules.update;

import com.moinapp.wuliao.commons.db.IDataTable;
import com.moinapp.wuliao.commons.moduleframework.AbsManager;
import com.moinapp.wuliao.commons.moduleframework.AbsModule;
import com.moinapp.wuliao.commons.moduleframework.IFeature;
import com.moinapp.wuliao.commons.system.SystemFacadeFactory;

import java.util.List;

public class UpdateModule extends AbsModule {

	// ===========================================================
	// Constants
	// ===========================================================

	public static final String MODULE_NAME = "Update";

	// ===========================================================
	// Fields
	// ===========================================================
	private UpdatePreference mPreference;
	// ===========================================================
	// Constructors
	// ===========================================================
	public UpdateModule(){
		mPreference = UpdatePreference.getInstance();
	}
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onEnabled(boolean enabled) {
		AbsManager mgr = UpdateManager.getInstance();
		if (enabled) {
			mgr.init();
		} else {
			mgr.onDisable();
		}
	}
	public void init() {
		UpdateManager.getInstance().init();
	}
    @Override
    public boolean canEnabled() {
        return true;
    }

	@Override
	public String getName() {
		return MODULE_NAME;
	}

	@Override
	public List<IFeature> getFeatures() {
		return null;
	}

	@Override
	public List<IDataTable> getTables() {
		return null;
	}

	@Override
	public void onAppFirstInit(boolean enabled) {
		long now = SystemFacadeFactory.getSystem().currentTimeMillis();
		mPreference.setLastCheckUpdate(now);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
