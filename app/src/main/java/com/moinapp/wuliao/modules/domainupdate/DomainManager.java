package com.moinapp.wuliao.modules.domainupdate;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.db.DataProvider;
import com.moinapp.wuliao.commons.info.CommonDefine;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moduleframework.AbsManager;
import com.moinapp.wuliao.commons.net.DomainInfo;
import com.moinapp.wuliao.commons.net.DomainProviderFactory;
import com.moinapp.wuliao.commons.net.IDomainProvider;
import com.moinapp.wuliao.modules.domainupdate.table.DomainTable;
import com.moinapp.wuliao.utils.CommonMethod;

import java.util.ArrayList;
import java.util.Map;

public class DomainManager extends AbsManager implements IDomainProvider{

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private static final ILogger NqLog = LoggerFactory.getLogger(DomainModule.MODULE_NAME);

    private static DomainManager mInstance;

    private Context mContext;

    //TODO edit urls
    private static final String[] DOMAIN_SHOPPING = {
    	//type|region|version|seq|domains
    	"0|0|1|0|lau-livecn.nq.com:443&lau-livecn.qlauncher.com:443&lau-livecn.netqin.com:443",
    	"1|0|1|0|plug-livecn.nq.com:443&plug-livecn.qlauncher.com:443&plug-livecn.netqin.com:443",
    	"0|1|1|0|lau-live.nq.com:443&lau-live.qlauncher.com:443&lau-live.netqin.com:443",
    	"1|1|1|0|plug-live.nq.com:443&plug-live.qlauncher.com:443&plug-live.netqin.com:443"
    };

    private static final String[] DOMAIN_VRF = {
    	//type|region|version|seq|domains
    	"0|0|1|0|vrf-live.nq.com:8081&vrf-live.qlauncher.com:8081&vrf-live.netqin.com:8081",
    	"1|0|1|0|vrf-live.nq.com:8083&vrf-live.qlauncher.com:8083&vrf-live.netqin.com:8083",
    	"0|1|1|0|vrf-live.nq.com:8081&vrf-live.qlauncher.com:8081&vrf-live.netqin.com:8081",
    	"1|1|1|0|vrf-live.nq.com:8083&vrf-live.qlauncher.com:8083&vrf-live.netqin.com:8083"
    };

    private static final String[] DOMAIN_TEST = {
    	//type|region|version|seq|domains
    	"0|0|1|0|test-live.nq.com:8081&test-live.qlauncher.com:8081&test-live.netqin.com:8081",
    	"1|0|1|0|test-live.nq.com:8083&test-live.qlauncher.com:8083&test-live.netqin.com:8083",
    	"0|1|1|0|test-live.nq.com:8081&test-live.qlauncher.com:8081&test-live.netqin.com:8081",
    	"1|1|1|0|test-live.nq.com:8083&test-live.qlauncher.com:8083&test-live.netqin.com:8083"
    };

    // ===========================================================
    // Constructors
    // ===========================================================

    private DomainManager(){
        mContext = ApplicationContext.getContext();
    }

    public synchronized static DomainManager getInstance() {
        if(mInstance == null) {
            mInstance = new DomainManager();
        }

        return mInstance;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void init() {
    	DomainProviderFactory.setProvider(this);
    }
    
    @Override
    public void onDisable() {
    	DomainProviderFactory.setProvider(null);
    	super.onDisable();    	
    }

    @Override
    public void switchToNext(int type) {
        NqLog.i("switchToNext type="+type);
        
        int region = CommonMethod.getServerRegion(mContext);
        
        Cursor c = null;
        int seq = -1;
        int count = -1;
        try{
            c = mContext.getContentResolver().query(DomainTable.TABLE_URI,null,
                    DomainTable.DOMAIN_TYPE + " = ? AND " + DomainTable.DOMAIN_REGION + " = ?",
                    new String[]{String.valueOf(type),String.valueOf(region)},null);
            if(c != null && c.moveToNext()){
                seq = c.getInt(c.getColumnIndex(DomainTable.DOMAIN_SEQ));
                String domains = c.getString(c.getColumnIndex(DomainTable.DOMAIN_DOMAINS));
                String[] domainList = domains.split("\\&");
                if (domainList!=null){
                	count = domainList.length;
                }
            }
        } catch(Exception e){
        	NqLog.e(e);
        } finally {
            if(c != null){
                c.close();
            }
        }
        
        if (seq < 0 || count <= 0){
        	return;
        }

        seq = (seq + 1) % count;
        ContentValues value = new ContentValues();
        value.put(DomainTable.DOMAIN_SEQ, seq);
        mContext.getContentResolver().update(DomainTable.TABLE_URI,value,
                DomainTable.DOMAIN_TYPE + " = ? AND " + DomainTable.DOMAIN_REGION +" = ?",
                new String[]{String.valueOf(type), String.valueOf(region)});
    }

    public void updateDomain(Map<String,String> keyValues){
    	if (keyValues == null){
    		return;
    	}
    	
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newDelete(DomainTable.TABLE_URI).build());
        String version = keyValues.get("version");

        ContentValues[] domainsList = new ContentValues[4];
        for(String str : keyValues.keySet()){
            if(TextUtils.isDigitsOnly(str)){
                int id = Integer.valueOf(str);
                int type = id/100;

                if(type >=1 && type <=4){
                    String urlWithPort = keyValues.get(str);
                    ContentValues value = new ContentValues();
					if (domainsList[type-1] != null) {
						urlWithPort = domainsList[type-1]
								.getAsString(DomainTable.DOMAIN_DOMAINS)
								+ "&"
								+ urlWithPort;
					}
                    value.put(DomainTable.DOMAIN_TYPE,type == 1 || type ==3 ? DomainInfo.TYPE_LAUNCHER : DomainInfo.TYPE_WEATHER);
                    value.put(DomainTable.DOMAIN_REGION,type <=2 ? 0 :1);
                    value.put(DomainTable.DOMAIN_VER,version);
                    value.put(DomainTable.DOMAIN_SEQ,0);
                    value.put(DomainTable.DOMAIN_DOMAINS,urlWithPort);
                    domainsList[type-1] = value;
                }
            }
        }
        
        for(int i=0; i<4; i++){
        	if (domainsList[i] != null){
        		ops.add(ContentProviderOperation.newInsert(DomainTable.TABLE_URI).withValues(domainsList[i]).build());
        	}
        }

        try{
        	if (!ops.isEmpty()){
        		mContext.getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY,ops);
        	}
        } catch (Exception e) {
            NqLog.e(e);
        }
    }

    public String getVersion(){
        Cursor c = null;
        String version = "1";
        try{
            c = mContext.getContentResolver().query(DomainTable.TABLE_URI,null, null,null,null);
            if(c != null && c.moveToNext()){
                version = c.getString(c.getColumnIndex(DomainTable.DOMAIN_VER));
            }
        }finally {
            if(c != null){
                c.close();
            }
        }

        return version;
    }

    @Override
    public DomainInfo getDomainInfo(int type) {
        DomainInfo result = null;
        
        Cursor c = null;
        try{
        	int region = CommonMethod.getServerRegion(mContext);
        	c = mContext.getContentResolver().query(DomainTable.TABLE_URI,null,
                    DomainTable.DOMAIN_TYPE + " = ? AND " + DomainTable.DOMAIN_REGION + " = ?",
                    new String[]{String.valueOf(type),String.valueOf(region)},null);
            if(c != null && c.moveToNext()){
            	String domains = c.getString(c.getColumnIndex(DomainTable.DOMAIN_DOMAINS));
            	int seq = c.getInt(c.getColumnIndex(DomainTable.DOMAIN_SEQ));
            	String[] domainList = domains.split("\\&");
            	if (domainList != null && domainList.length > seq){
            		String[] hostAndPort = domainList[seq].split(":");
            		DomainInfo domain = new DomainInfo();
            		domain.type = type;
            		domain.region = region;
            		domain.host = hostAndPort[0];
            		domain.port = Integer.valueOf(hostAndPort[1]);
            		result = domain;
            	}
            }
        } catch (Exception e){
        	NqLog.e(e);
        }
        finally {
            if(c != null){
                c.close();
            }
        }
        
        
        if(result == null){
            firstInit();
            result = new DomainInfo();
            int region = CommonMethod.getServerRegion(mContext);
            result.region = region;
            if (type == DomainInfo.TYPE_LAUNCHER){
            	result.type = DomainInfo.TYPE_LAUNCHER;
            	result.host = CommonMethod.getLauncherUrl(mContext);
            	result.port = CommonDefine.APP_PORT;
            } else if (type == DomainInfo.TYPE_WEATHER) {
//            	result.type = DomainInfo.TYPE_WEATHER;
//            	result.host = CommonMethod.getWeatherUrl(mContext);
//            	result.port = CommonDefine.WEATHER_PORT;
            }
        }

        NqLog.i("getUrlWithPort result="+result+" type="+type);
        return result;
    }

    public void firstInit(){
        ArrayList<ContentProviderOperation> list = new ArrayList<ContentProviderOperation>();
        ContentProviderOperation.Builder b;

        String[] domains = null;
//        switch (CommonDefine.getStyleType()){
//            case CommonDefine.VERSION_STYLE_SHOPPING:
//                domains = DOMAIN_SHOPPING;
//                break;
//            case CommonDefine.VERSION_STYLE_VERIFY:
//                domains = DOMAIN_VRF;
//                break;
//            case CommonDefine.VERSION_STYLE_TEST:
//                domains = DOMAIN_TEST;
//                break;
//        }

        for(String s : domains){
            String[] ss = s.split("\\|");
            ContentValues value = new ContentValues();
            value.put(DomainTable.DOMAIN_TYPE,ss[0]);
            value.put(DomainTable.DOMAIN_REGION,ss[1]);
            value.put(DomainTable.DOMAIN_VER,ss[2]);
            value.put(DomainTable.DOMAIN_SEQ,ss[3]);            
            value.put(DomainTable.DOMAIN_DOMAINS,ss[4]);
            b = ContentProviderOperation.newInsert(DomainTable.TABLE_URI).withValues(value);
            list.add(b.build());
        }

        try {
            mContext.getContentResolver().applyBatch(DataProvider.DATA_AUTHORITY, list);
        } catch (Exception e) {
            NqLog.e(e);
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
