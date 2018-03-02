package com.moinapp.wuliao.commons.concurrent;

import java.util.concurrent.ExecutorService;

public class PriorityExecutor {
	private static final int MAX_POOL_SIZE = 4;
	private static ExecutorService sExecutor;

	public synchronized static ExecutorService getExecutor() {
		if (sExecutor == null) {
			sExecutor = new PriorityThreadPoolExecutor(MAX_POOL_SIZE);
		}

		return sExecutor;
	}
}
