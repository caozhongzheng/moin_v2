/**
 * 
 */
package com.moinapp.wuliao.modules.regularupdate;

import android.content.Context;
import android.text.format.DateUtils;

import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.system.SystemFacadeFactory;
import com.moinapp.wuliao.utils.CommonMethod;
import com.moinapp.wuliao.utils.NetworkUtils;

public class RegularUpdatePolicy {

	public static boolean isExceedFrequency() {
		Context ctx = ApplicationContext.getContext();
		
		if (CommonMethod.hasActiveNetwork(ctx)) {
			long now = SystemFacadeFactory.getSystem().currentTimeMillis();
			RegularUpatePreference pref = RegularUpatePreference.getInstance();
			long lastRegularUpdateTime = pref.getLastRegularUpdateTime();
			long freqWifi = pref.getRegularUpdateFreqWifiInMinutes() * DateUtils.MINUTE_IN_MILLIS;
			long freq3g = pref.getRegularUpdateFreq3gInMinutes() * DateUtils.MINUTE_IN_MILLIS;
			long freq = 0L;

			if (NetworkUtils.isWifi(ctx)) {
				freq = freqWifi > 0 ? freqWifi : 2 * DateUtils.DAY_IN_MILLIS;
			} else {
				freq = freq3g > 0 ? freq3g : 7 * DateUtils.DAY_IN_MILLIS;
			}
			
			if (Math.abs(now - lastRegularUpdateTime) >= freq) {
				return true;
			}
		}
		return false;
	}
}
