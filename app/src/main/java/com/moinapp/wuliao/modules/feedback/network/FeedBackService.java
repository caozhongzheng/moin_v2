/**
 * 
 */
package com.moinapp.wuliao.modules.feedback.network;

import com.moinapp.wuliao.commons.net.AbsService;

public class FeedBackService extends AbsService {
	public void uploadFeedback(String contact, String content, Object tag) {
		getExecutor().submit(new FeedBackProtocol(contact, content, tag));
	}
}
