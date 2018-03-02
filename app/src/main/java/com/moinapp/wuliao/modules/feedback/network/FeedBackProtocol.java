package com.moinapp.wuliao.modules.feedback.network;

import android.util.Log;

import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocol;
import com.moinapp.wuliao.modules.feedback.FeedbackModule;

public class FeedBackProtocol extends AbsProtocol {

	private static final ILogger NqLog = LoggerFactory.getLogger(FeedbackModule.MODULE_NAME);
	// ===========================================================
	// Fields
	// ===========================================================
//	private final TFeedback feedback;
	private final Object tag;

	// ===========================================================
	// Constructors
	// ===========================================================
	public FeedBackProtocol(String contact, String content, Object tag) {
		this.tag = tag;
//		feedback = new TFeedback();
//		feedback.setContact(contact);
//		feedback.setContent(content);
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected int getProtocolId() {
		return 0xfeed;
	}

	@Override
	public void process() {
		try {
//			TLauncherService.Iface client = TLauncherServiceClientFactory
//					.getClient(getThriftProtocol());
//			client.uploadFeedback(getUserInfo(), feedback);
			EventBus.getDefault().post(
					new FeedbackUploadedEvent().setTag(tag).setSuccess(true));
		} catch (Throwable e) {
			NqLog.e(Log.getStackTraceString(e)); onError();
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
		EventBus.getDefault().post(
				new FeedbackUploadedEvent().setTag(tag).setSuccess(false));
	}
	/**
	 * 用户反馈上传完毕的事件
	 * 
	 */
	public static class FeedbackUploadedEvent {
		/**
		 * 上传结果
		 */
		private boolean success;
		private Object tag;

		public boolean isSuccess() {
			return success;
		}

		public FeedbackUploadedEvent setSuccess(boolean success) {
			this.success = success;
			return this;
		}

		public Object getTag() {
			return tag;
		}

		public FeedbackUploadedEvent setTag(Object tag) {
			this.tag = tag;
			return this;
		}

	}

	
}
