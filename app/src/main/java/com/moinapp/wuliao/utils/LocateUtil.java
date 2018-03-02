package com.moinapp.wuliao.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

public class LocateUtil {
    private static ILogger MyLog = LoggerFactory.getLogger("LocateUtil");
    private MyGdLocationLitener mGdListener;
    private LocateListener mCallBack;
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Context mContext;
    private LocationManagerProxy mAMapLocationManager;

    private static final int MSG_TIME_OUT = 1;
    private static final int MSG_LOCATE_BY_GD = 2;

    private static final long TIME_OUT = 20 * DateUtils.SECOND_IN_MILLIS;

    private final Object lock = new Object();
    
    private boolean hasResult; 

    public interface LocateListener {

        public void onLocateFinish(AMapLocation aMapLocation);

        public void onLocateFailed();
    }

    public LocateUtil(Context context, LocateListener listener) {
        mContext = context;
        mCallBack = listener;
    }

    public void startLocation() {
        MyLog.i("LocateUtil startLocation");

        mHandlerThread = new HandlerThread("LocateHandlerThread");
        mHandlerThread.start();
        mHandler = new MyHandler(mHandlerThread.getLooper());
        mHandler.sendEmptyMessageDelayed(MSG_TIME_OUT, TIME_OUT);

        mHandler.sendEmptyMessage(MSG_LOCATE_BY_GD);
    }

    private class MyHandler extends Handler {

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try{
            	switch (msg.what) {
                case MSG_LOCATE_BY_GD:
                    mAMapLocationManager = LocationManagerProxy.getInstance(mContext);
                    mGdListener = new MyGdLocationLitener();
                    mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 5*1000 ,10 , mGdListener);
                    break;
                case MSG_TIME_OUT:
                	synchronized(lock){
                		if (hasResult){
                			return;
                		}
                		hasResult = true;
                	}
                	destory();
                	mCallBack.onLocateFailed();
                    break;
				}
            } catch(Exception e){
            	MyLog.e(e);
            }
        }
    }

    private class MyGdLocationLitener implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation == null){
                aMapLocation = new AMapLocation("AMapLocation");
                aMapLocation.setLatitude(0);
                aMapLocation.setLongitude(0);
            }
        	MyLog.i("location succ! lat=" + aMapLocation.getLatitude() + " lon=" + aMapLocation.getLongitude());
        	MyLog.i("location succ! province=" + aMapLocation.getProvince() + " city=" + aMapLocation.getCity() + " District=" + aMapLocation.getDistrict());
        	synchronized(lock){
        		if (hasResult){
        			return;
        		}
        		hasResult = true;
        	}        
        	destory();
            mCallBack.onLocateFinish(aMapLocation);
        }

        @Override
        public void onLocationChanged(Location location) {MyLog.i("LocateUtil onLocationChanged");}

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {MyLog.i("LocateUtil onStatusChanged");}

        @Override
        public void onProviderEnabled(String s) {MyLog.i("LocateUtil onProviderEnabled");}

        @Override
        public void onProviderDisabled(String s) {MyLog.i("LocateUtil onProviderDisabled");}
    }

    private void destory(){
    	if (mAMapLocationManager != null) {  
            mAMapLocationManager.removeUpdates(mGdListener);  
            mAMapLocationManager.destory();  
            mAMapLocationManager = null; 
        } 
    	mHandler.removeMessages(MSG_TIME_OUT);
    	if (mHandlerThread != null){
    		mHandlerThread.quit();
    		mHandlerThread = null;
    	}
    }
}
