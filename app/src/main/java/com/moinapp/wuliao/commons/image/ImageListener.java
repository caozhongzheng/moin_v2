package com.moinapp.wuliao.commons.image;

import android.graphics.drawable.BitmapDrawable;

import com.moinapp.wuliao.commons.net.Listener;

public interface ImageListener extends Listener{
	
	public void getImageSucc(String url, BitmapDrawable drawable);
}
