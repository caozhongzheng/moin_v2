package com.moinapp.wuliao.utils;

import android.database.Cursor;

import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

public class CursorUtils {
	private static ILogger MyLog = LoggerFactory.getLogger("CursorUtils");
	public static void closeCursor(Cursor cursor) {
		try {
			if (cursor != null) {
				cursor.close();
			}
		} catch (Exception e) {
			MyLog.e(e);
		}
	}
}
