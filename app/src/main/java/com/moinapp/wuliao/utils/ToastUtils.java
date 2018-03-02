package com.moinapp.wuliao.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	private static String hint = "";
	private static Toast toast = null;

	public static void toast(Context context, String text) {
		if (hint.equalsIgnoreCase(text)) {
			if (toast != null) {
				toast.cancel();
			}
		}
		hint = text;
		toast = Toast.makeText(context, text,
				Toast.LENGTH_SHORT);
		toast.show();
	}

	public static void toast(Context context, int strID) {
		String text = context.getResources().getString(strID);
		toast(context, text);
	}
}
