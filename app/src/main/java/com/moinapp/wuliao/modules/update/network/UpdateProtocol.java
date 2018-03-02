package com.moinapp.wuliao.modules.update.network;

import android.os.Bundle;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.https.AbsHttpOperation;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.net.HttpPostProtocal;
import com.moinapp.wuliao.commons.net.NetConstants;
import com.moinapp.wuliao.commons.net.UpdateListener;
import com.moinapp.wuliao.modules.mine.MineModule;
import com.moinapp.wuliao.modules.mine.model.BaseResponse;
import com.moinapp.wuliao.commons.https.HttpObserver;
import com.moinapp.wuliao.modules.update.model.App;

import java.util.Map;

public class UpdateProtocol extends HttpPostProtocal implements HttpObserver {
	private static ILogger MyLog = LoggerFactory.getLogger(MineModule.MODULE_NAME);

	// ===========================================================
	// Constructors
	// ===========================================================
	public UpdateProtocol(String url, Map data, Object tag) {
		super(url, data);
		setTag(tag);
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	protected com.moinapp.wuliao.commons.https.HttpObserver getCallback() {
		return this;
	}

	@Override
	public void onHttpResult(int resultCode, String respone) {
		MyLog.i("UpdateProtocol.onHttpResult()... resultCode=" + resultCode + ", response=" + respone);
		if (resultCode == AbsHttpOperation.ERROR_NONE) {
			EventBus.getDefault().post(new UpdateSuccessEvent(respone, getTag()));
		} else {
			Object object = getTag();
			if (object != null) {
				((UpdateListener) object).onErr(resultCode);
			}
		}
	}

	// ===========================================================
	// inner class
	// ===========================================================
	public static class UpdateSuccessEvent extends AbsProtocolEvent {
		private String mResponse;

		public UpdateSuccessEvent(String resp, Object tag) {
			setTag(tag);
			mResponse = resp;
		}

		public String getResponse() {
			return mResponse;
		}
	}

	/**
	 * 服务器返回的版本更新的response
	 */
	public static class UpdateResponse extends BaseResponse {
		private App app;

		public App getApp() {
			return app;
		}

		public void setApp(App app) {
			this.app = app;
		}
	}

	// ===========================================================
	// interface
	// ===========================================================
	@Override
	protected void beforeCall() {

	}

	@Override
	protected void afterCall() {

	}

	@Override
	protected void onError() {

	}

}

