package com.moinapp.wuliao.modules.regularupdate.network;

import com.moinapp.wuliao.commons.net.AbsService;
import com.moinapp.wuliao.commons.net.ServiceLock;

public class RegularUpdateService extends AbsService {
	
	public void regularUpdate(Object tag){
		ServiceLock lock = ServiceLock.getInstance(RegularUpdateProtocol.class.getName());
		if (!lock.available()) {
			return;
		}
		getExecutor().submit(new RegularUpdateProtocol(tag));
	}
}
