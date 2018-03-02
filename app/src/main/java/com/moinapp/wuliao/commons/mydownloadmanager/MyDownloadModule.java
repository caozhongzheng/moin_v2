package com.moinapp.wuliao.commons.mydownloadmanager;

import com.moinapp.wuliao.commons.db.IDataTable;
import com.moinapp.wuliao.commons.moduleframework.AbsModule;
import com.moinapp.wuliao.commons.moduleframework.IFeature;
import com.moinapp.wuliao.commons.mydownloadmanager.table.DownloadTable;

import java.util.ArrayList;
import java.util.List;

public class MyDownloadModule extends AbsModule {

	// ===========================================================
	// Constants
	// ===========================================================

    private static final String MODULE_NAME = "MyDownload";

	// ===========================================================
	// Fields
	// ===========================================================

    private List<IDataTable> mTables;

	// ===========================================================
	// Constructors
	// ===========================================================

    public MyDownloadModule(){
        mTables = new ArrayList<IDataTable>();
        mTables.add(new DownloadTable());
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
		return null;
	}

	@Override
	public List<IDataTable> getTables() {
		return mTables;
	}

    @Override
    public boolean canEnabled() {
        return true;
    }

	@Override
	protected void onEnabled(boolean enabled) {
		// do nothing		
	}
    public void init(){}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
