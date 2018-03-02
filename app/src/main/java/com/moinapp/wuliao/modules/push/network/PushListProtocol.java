/**
 * 
 */
package com.moinapp.wuliao.modules.push.network;

import android.util.Log;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocol;
import com.moinapp.wuliao.commons.system.SystemFacadeFactory;
import com.moinapp.wuliao.modules.push.PushModule;
import com.moinapp.wuliao.modules.push.PushPreference;
import com.moinapp.wuliao.modules.push.model.Push;

import java.util.ArrayList;

public class PushListProtocol extends AbsProtocol {
	private static final ILogger NqLog = LoggerFactory.getLogger(PushModule.MODULE_NAME);

	public PushListProtocol(Object tag) {
		setTag(tag);
	}

	@Override
	protected int getProtocolId() {
		return 0x16;
	}

	@Override
	protected void process() {
		try {
//			TLauncherService.Iface client = TLauncherServiceClientFactory
//					.getClient(getThriftProtocol());
//			TPushResp resp = client.getPushList(getUserInfo());
//			int size;
//			if (resp.pushmsgs != null && (size = resp.pushmsgs.size()) > 0) {
//				PushManager mgr = PushManager.getInstance();
				ArrayList<Push> pushList = new ArrayList<Push>(/*size*/);
//				for (TPush push : resp.pushmsgs) {
//					Push clientPush = mgr.tpushToPush(push);
//					if (clientPush != null) {
//						pushList.add(clientPush);
//					}
//				}
				EventBus.getDefault().post(
						new PushResultEvent(pushList).setTag(getTag()));
//			}
		} catch (Throwable e) {
			NqLog.e(Log.getStackTraceString(e));onError();
		} finally {
			long now = SystemFacadeFactory.getSystem().currentTimeMillis();
			PushPreference.getInstance().setLastGetPushTime(now);
		}
	}

	@Override
	protected void beforeCall() {

	}

	@Override
	protected void afterCall() {

	}

	@Override
	protected void onError() {
		EventBus.getDefault().post(new PushResultEvent().setTag(getTag()));
	}
	
}
