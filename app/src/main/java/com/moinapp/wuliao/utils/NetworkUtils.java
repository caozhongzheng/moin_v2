/**
 * 
 */
package com.moinapp.wuliao.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.moinapp.wuliao.commons.system.SystemFacadeFactory;

public class NetworkUtils {
	// 网络类型
	public static final int NETWORK_UNKNOWN = 0;
	public static final int NETWORK_WIFI = 1;
	public static final int NETWORK_MOBILE = 2;

	public static boolean isConnected(Context context) {
		boolean result = false;

		NetworkInfo ni = SystemFacadeFactory.getSystem(context).getActiveNetworkInfo();
		if (ni != null && ni.isConnected()) {
			result = true;
		}

		return result;
	}

	public static boolean isWifi(Context context) {
        Integer networkType = SystemFacadeFactory.getSystem(context).getActiveNetworkType();
        if (networkType != null && networkType.intValue() == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else {
            return false;
        }
	}
}
