package com.moinapp.wuliao.modules.regularupdate.processor;

import java.util.Map;

import com.moinapp.wuliao.commons.moduleframework.IUpdateActionHandler;
import com.moinapp.wuliao.modules.regularupdate.RegularUpatePreference;
import com.moinapp.wuliao.modules.regularupdate.model.UpdateAction;
import com.moinapp.wuliao.utils.UriQueryUtils;

public class UpdatePreferencesProcessor implements IUpdateActionProcessor {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================
	private RegularUpatePreference mPreference;

	// ===========================================================
	// Constructors
	// ===========================================================
	UpdatePreferencesProcessor() {
		mPreference = RegularUpatePreference.getInstance();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public void process(Map<Integer, IUpdateActionHandler> handlers,
			UpdateAction action) {
		Map<String, String> keyValues = UriQueryUtils.parse(action
				.getParameters());
		String value = keyValues.get("version");
		if (value != null) {
			mPreference.setRegularUpdateVersion(value);
		}

		for (IUpdateActionHandler handler : handlers.values()) {
			if (handler.supportModuleLevelAction()) {
				handler.updatePreferences(keyValues);
			}
		}
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
