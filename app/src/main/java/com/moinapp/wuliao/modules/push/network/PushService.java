package com.moinapp.wuliao.modules.push.network;

import com.moinapp.wuliao.commons.net.AbsService;

/**
 * Push服务类，将外部请求提交到线程池执行
 */
public class PushService extends AbsService {
	public void getPush(Object tag) {
		getExecutor().submit(new PushListProtocol(tag));
	}

}
