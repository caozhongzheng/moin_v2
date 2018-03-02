package com.moinapp.wuliao.commons.net;

import com.moinapp.wuliao.commons.concurrent.PriorityExecutor;

import java.util.concurrent.ExecutorService;

public class AbsService {
	private ExecutorService mExecutor;

	public AbsService() {
		mExecutor = PriorityExecutor.getExecutor();
	}

	protected ExecutorService getExecutor() {
		return mExecutor;
	}

	protected void setExecutor(ExecutorService executor) {
		mExecutor = executor;
	}
}
