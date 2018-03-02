/**
 * 
 */
package com.moinapp.wuliao.commons.net;

public class ProtocolFinishEvent {
	private boolean success;

	public boolean isSuccess() {
		return success;
	}

	public ProtocolFinishEvent setSuccess(boolean success) {
		this.success = success; return this;
	}

}
