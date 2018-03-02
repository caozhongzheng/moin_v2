package com.moinapp.wuliao.commons.net;

import android.content.Context;

import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.info.CommonDefine;
import com.moinapp.wuliao.utils.CommonMethod;

public class DefaultDomainProvider implements IDomainProvider{

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public DomainInfo getDomainInfo(int type) {
    	DomainInfo result = new DomainInfo();
    	Context context = ApplicationContext.getContext();
    	
    	int region = CommonMethod.getServerRegion(context);
    	result.region = region;
    	if (type == DomainInfo.TYPE_LAUNCHER){
    		result.type = DomainInfo.TYPE_LAUNCHER;
        	result.host = CommonMethod.getLauncherUrl(context);
        	result.port = CommonDefine.APP_PORT;
//        } else if (type == DomainInfo.TYPE_WEATHER) {
//        	result.type = DomainInfo.TYPE_WEATHER;
//        	result.host = CommonMethod.getWeatherUrl(context);
//        	result.port = CommonDefine.WEATHER_PORT;
        }
        return result;
    }

    @Override
    public void switchToNext(int type) {

    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
