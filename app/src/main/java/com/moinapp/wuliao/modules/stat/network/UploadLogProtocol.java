package com.moinapp.wuliao.modules.stat.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.concurrent.Priority;
import com.moinapp.wuliao.commons.eventbus.EventBus;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.net.AbsProtocol;
import com.moinapp.wuliao.commons.net.AbsProtocolEvent;
import com.moinapp.wuliao.commons.system.SystemFacadeFactory;
import com.moinapp.wuliao.modules.stat.DefaultLauncherStat;
import com.moinapp.wuliao.modules.stat.StatModule;
import com.moinapp.wuliao.modules.stat.StatPreference;
import com.moinapp.wuliao.modules.stat.table.StoreStatTable;

import java.util.ArrayList;

public class UploadLogProtocol extends AbsProtocol{
	private static final ILogger NqLog = LoggerFactory.getLogger(StatModule.MODULE_NAME);

    @Override
    protected void beforeCall() {

    }

    @Override
    protected void afterCall() {

    }

    private static final int MAX_SZIE = 300; //一次上传最大条数
	
	private ArrayList<String> dataList;

    private Context mContext;
	
	private static final String LOG_TYPES = "store_action_zip";

	public UploadLogProtocol() {
        mContext = ApplicationContext.getContext();
	}

	@Override
	protected void process() {
        NqLog.i("UploadLogProtocol process");
		try {
//            TLauncherService.Iface client = TLauncherServiceClientFactory.getClient(getThriftProtocol());
            boolean needAppend = true;
            while (getStatData()) {
                StringBuilder builder = new StringBuilder();
                if (needAppend){
                	appendDefaultLauncherStat();
                	needAppend = false;
                }
                
                for (String s : dataList) {
                    builder.append(s);
                    builder.append("\n");
                }

                //upload to server
//                TLogUploadRequest req = new TLogUploadRequest();
//                req.logType = LOG_TYPES;
//                req.byteStream = ByteBuffer.wrap(GZipUtils.compress(builder.toString().getBytes()));

//                client.uploadLog(getUserInfo(), req);

                //delete the db;
                mContext.getContentResolver().delete(StoreStatTable.TABLE_URI,
                        StoreStatTable.STORE_STAT_ISUPLOAD + " = 1", null);
            }

            NqLog.i("upload log succ!");
            EventBus.getDefault().post(new UploadLogSuccessEvent());
		}catch(Exception e) {
            e.printStackTrace(); onError();
        }finally {
            StatPreference.getInstance().setLastUploadLog(System.currentTimeMillis());
        }
    }
	@Override
	protected void onError() {
		EventBus.getDefault().post(new UploadLogFailEvent());
	}

	private boolean getStatData() {
		dataList = new ArrayList<String>();
		ArrayList<Long> mIds = new ArrayList<Long>();
		String selection = "limit " + 0 + "," + MAX_SZIE;

        Cursor storeCursor = null;
        try{
            storeCursor = mContext.getContentResolver().query(StoreStatTable.TABLE_URI, null, null, null, "_id ASC " + selection);
            while(storeCursor != null && storeCursor.moveToNext()) {
                String desc = storeCursor.getString(storeCursor.getColumnIndex(StoreStatTable.STORE_STAT_DESC));
                String resourceId = storeCursor.getString(storeCursor.getColumnIndex(StoreStatTable.STORE_STAT_RESOURCEID));
                String scene = storeCursor.getString(storeCursor.getColumnIndex(StoreStatTable.STORE_STAT_SCNE));
                long time = storeCursor.getLong(storeCursor.getColumnIndex(StoreStatTable.STORE_STAT_TIME));
                dataList.add(desc + "|" + resourceId  + "|" + scene + "|" + time);
                mIds.add(storeCursor.getLong(storeCursor.getColumnIndex(StoreStatTable.STORE_STAT_ID)));
            }

            if (mIds.size() > 0) {
	            ContentValues storevalue = new ContentValues();
	            storevalue.put(StoreStatTable.STORE_STAT_ISUPLOAD, 1);
	            mContext.getContentResolver().update(StoreStatTable.TABLE_URI, storevalue, StoreStatTable.STORE_STAT_ID + " BETWEEN ? AND ?",
	                new String[] { String.valueOf(mIds.get(0)),String.valueOf(mIds.get(mIds.size() - 1)) });
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(storeCursor != null){
                storeCursor.close();
            }
        }

        NqLog.i("getStatData dataList.size="+dataList.size());

		return (dataList.size()) > 0;
	}
	
	@Override
    public int getPriority() {
        return Priority.PRIORITY_MINOR;
    }

    @Override
    protected int getProtocolId() {
        return 0x15;
    }
    
    private void appendDefaultLauncherStat(){
    	long now = SystemFacadeFactory.getSystem().currentTimeMillis();
        String packageName = DefaultLauncherStat.getPackageName(mContext);
        int isDefaultLauncher = mContext.getPackageName().equals(packageName) ? 1 : 0;
        String defaultLauncherStat = "3408|null|" + isDefaultLauncher + "_" + packageName + "|" + now;
        dataList.add(defaultLauncherStat);
        NqLog.i("defaultLauncherStat: " + defaultLauncherStat);
    }

    public static class UploadLogSuccessEvent extends AbsProtocolEvent{
    }

    public static class UploadLogFailEvent extends AbsProtocolEvent{

    }

	
}
