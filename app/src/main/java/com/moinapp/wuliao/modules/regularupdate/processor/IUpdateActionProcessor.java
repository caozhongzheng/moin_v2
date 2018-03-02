package com.moinapp.wuliao.modules.regularupdate.processor;

import com.moinapp.wuliao.commons.moduleframework.IUpdateActionHandler;
import com.moinapp.wuliao.modules.regularupdate.model.UpdateAction;

import java.util.Map;

public interface IUpdateActionProcessor {

	public void process(Map<Integer, IUpdateActionHandler> handlers, UpdateAction action);

}
