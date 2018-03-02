package com.moinapp.wuliao.modules.cosplay;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.modules.cosplay.listener.CosplayInitEditListener;
import com.moinapp.wuliao.modules.cosplay.model.CosplayResource;
import com.moinapp.wuliao.modules.cosplay.model.CosplayThemeInfo;
import com.moinapp.wuliao.modules.cosplay.model.CosplayThemes;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CosplayRuntimeData {

    private static final ILogger MyLog = LoggerFactory.getLogger("CosplayRuntimeData");
    private static CosplayRuntimeData mInstance;

    //创建时候的Resourec//
    public CosplayResource initResource = null;

    public CosplayResource currentRes = null;
    private String _ipid = null;

    public String currentItemKey = null;
    public List<CosplayThemeInfo> currentListInfo = null;
    public CosplayThemeInfo currentHemesInfo = null;



    public Bitmap backGroundBitmap = null;
    // ===========================================================
    // Constructors
    // ===========================================================

    public View editViewGroup = null;

    public String runtimeJson = null;

    private CosplayRuntimeData() {

    }

    public static synchronized CosplayRuntimeData getInstance() {
        if (mInstance == null) {
            mInstance = new CosplayRuntimeData();
        }

        return mInstance;
    }


    public void setIpid(String ipid)
    {
        _ipid = ipid;
    }
    public String getIpid()
    {
        return _ipid;
    }


    //返回是否设置成功，设置成功则刷新界面//
    public  boolean setCurrentCosplay(CosplayResource currentRes) {
        if (currentRes == null) {
            return false;
        }

        if (this.currentRes == currentRes) {
            return false;
        }

        Map<String,List<CosplayThemeInfo>> tMap = currentRes.getThemes().getAllExistInfo();

        if(tMap.size()== 0) {
            return false;
        }

        Iterator iter = tMap.entrySet().iterator();

        if(iter.hasNext())
        {
            Map.Entry<String,List<CosplayThemeInfo>> entry = (Map.Entry<String,List<CosplayThemeInfo>>) iter.next();

            this.currentRes = currentRes;
        }
        else {
            return false;
        }

        return true;

    }

    public boolean setCurrentCosplayThemesList(String key)
    {
        currentItemKey = key;
        List<CosplayThemeInfo> list = currentRes.getThemes().getAllExistInfo().get(key);

        if(list == null)
        {
            return  false;
        }
        if(currentListInfo == list)
        {
            return  false;
        }
        this.currentListInfo = list;

        return true;


    }



    public  boolean setCurrentCosplayThemeInfo(CosplayThemeInfo currentThemeInfo) {
        if(currentThemeInfo == null)
        {
            return false;
        }

        if(this.currentHemesInfo  == currentThemeInfo)
        {
            return false;
        }

        this.currentHemesInfo = currentThemeInfo;

        return  true;


    }




}
