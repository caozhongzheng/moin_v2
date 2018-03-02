package com.moinapp.wuliao.modules.regularupdate.processor;

import java.util.Map;

import android.text.TextUtils;

import com.moinapp.wuliao.commons.moduleframework.IUpdateActionHandler;
import com.moinapp.wuliao.modules.regularupdate.model.UpdateAction;
import com.moinapp.wuliao.utils.UriQueryUtils;

public class UpdateSearchConfigProcessor implements IUpdateActionProcessor {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	UpdateSearchConfigProcessor() {
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
		String f = keyValues.get("featureId");
		if (!TextUtils.isDigitsOnly(f)) {
			return;
		}

		int featureId = Integer.parseInt(f);
		IUpdateActionHandler handler = handlers.get(featureId);
		if (handler != null) {
			handler.updateSearchConfig(keyValues);
		}
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
