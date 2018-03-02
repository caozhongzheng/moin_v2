package com.moinapp.wuliao.commons.net;

import android.content.Context;

import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.concurrent.Priority;

/**
 * 抽象协议基类
 *
 */
public abstract class AbsProtocol implements Runnable, Priority {
	private Object mTag;
	public Context mContext;

	public AbsProtocol(){
		mContext = ApplicationContext.getContext();
	}

	@Override
	public int getPriority() {
		return Priority.PRIORITY_CRITICAL;
	}

	protected abstract int getProtocolId();

	public Object getTag() {
		return mTag;
	}

	public void setTag(Object mTag) {
		this.mTag = mTag;
	}

	public static final Object ACTIVE_LOCK = new Object();

	@Override
	public final void run() {
		try {
			beforeCall();
			// if no connection established, return
//			if (mTransport == null) {
//                onError();
//                return;
//			}
			process();
		} finally {
			afterCall();
		}
	}

	protected abstract void process();

	protected abstract void beforeCall();

	protected abstract void afterCall();

	protected abstract void onError();
}
