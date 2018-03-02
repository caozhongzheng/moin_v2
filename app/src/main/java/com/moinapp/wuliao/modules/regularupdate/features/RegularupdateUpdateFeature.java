package com.moinapp.wuliao.modules.regularupdate.features;

import java.util.Map;

import android.content.Context;

import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.moduleframework.AbsFeature;
import com.moinapp.wuliao.commons.moduleframework.AbsUpdateActionHandler;
import com.moinapp.wuliao.commons.moduleframework.IUpdateActionHandler;
import com.moinapp.wuliao.modules.regularupdate.RegularUpatePreference;

/**
 * 客户端联网频率更新
 * 
 */
public class RegularupdateUpdateFeature extends AbsFeature {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int FEATURE = 109;
	// ===========================================================
	// Fields
	// ===========================================================
	private UpdateActionHandler mHandler;
	private RegularUpatePreference mPreference;

	// ===========================================================
	// Constructors
	// ===========================================================
	public RegularupdateUpdateFeature() {
		mHandler = new UpdateActionHandler();
		Context context = ApplicationContext.getContext();
		mPreference = RegularUpatePreference.getInstance();
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public int getFeatureId() {
		return FEATURE;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public int getStatus() {
		return 0;
	}

	@Override
	public IUpdateActionHandler getHandler() {
		return mHandler;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	@Override
	public String getVersionTag() {
		return mPreference.getRegularUpdateVersion();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	private class UpdateActionHandler extends AbsUpdateActionHandler {
		@Override
		public boolean supportModuleLevelAction() {
			return true;
		}

		@Override
		public void updatePreferences(Map<String, String> map) {
			//这里只处理与定期联网模块本身的更新频率，其余联网频率更新功能分散在各个模块内
			{
				// 定期联网接口，wifi 下联网频率，单位分钟
				String s = map.get("l_regular_update_freq_wifi");
				if (s != null) {
					long value = Long.parseLong(s);
					mPreference.setRegularUpdateFreqWifiInMinutes(value);
				}
			}
			{// 定期联网接口，3G 下联网频率，单位分钟
				String s = map.get("l_regular_update_freq_3g");
				if (s != null) {
					long value = Long.parseLong(s);
					mPreference.setRegularUpdateFreq3gInMinutes(value);
				}
			}
		}

	}
}
