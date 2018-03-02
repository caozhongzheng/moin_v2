package com.moinapp.wuliao.commons.image;

import android.graphics.Bitmap;

import com.moinapp.wuliao.commons.net.Listener;

/**
 * 获取图片监听
 * @time 2013-12-5 下午5:28:56
 */
public interface LoadIconListener extends Listener {
	public void onLoadComplete(Bitmap result);
}
