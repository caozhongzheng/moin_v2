package com.moinapp.wuliao.modules.banner;

import android.content.Context;

import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.utils.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

public class CacheHelper {
	private static final ILogger MyLog = LoggerFactory.getLogger(BannerModule.MODULE_NAME);
	
	private String cache_data = "/data/data/";
	private String cache_path = "";
	private String cache_banner = ""; 
	private Context mContext;
	private static CacheHelper instance;
	
	private CacheHelper(Context context){
		mContext = context;
		cache_path = cache_data + context.getPackageName() + "/cache/banner";
		cache_banner = cache_path + "/banner_";
	}
	
	public synchronized static CacheHelper getInstance(Context context){
		if (instance == null) {
			instance = new CacheHelper(context);
		}
		return instance;
	}
	
	/**
	 * 缓存内容汇总
	 * @param banners
	 * @param uid
	 */
	public void saveBannerListDataObjCache(ArrayList<Banner> banners, int uid) {
		ObjectOutputStream out = null;
		try {
			MyLog.d("saveBannerListDataObjCache uid:" + uid);
			if (banners == null || banners.size() == 0) 
				return;
			String cachePath = cache_banner + uid + ".obj";
			File file = new File(cache_path);
			if (!file.exists()) {
				file.mkdirs();
			}
			out = new ObjectOutputStream(new FileOutputStream(cachePath));
			out.writeObject(banners);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			MyLog.e("saveBannerListDataObjCache uid:" + uid + ", FileNotFoundException err:" + e);
		} catch (Exception e) {
			e.printStackTrace();
			MyLog.e("saveBannerListDataObjCache uid:" + uid + ", err:" + e);
		} finally {
			FileUtil.closeStream(out);
		}
	}
	
	/**
	 * 获取内容汇总缓存
	 * @param uid
	 */
	public ArrayList<Banner> getBannerListFromCache(int uid) {
		ObjectInputStream in = null;
		ArrayList<Banner> banners = new ArrayList<Banner>();
		try {
			String cachePath = cache_banner + uid + ".obj";
			File file = new File(cachePath);
			if (file.exists() && file.length() > 0) {
				in = new ObjectInputStream(new FileInputStream(cachePath));
				banners = (ArrayList<Banner>) in.readObject();
			}
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			FileUtil.closeStream(in);
		}
		MyLog.d("getBannerListFromCache uid:" + uid + ", " + banners);
		return banners;
	}
}
