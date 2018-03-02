package com.moinapp.wuliao.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;

/**
 * 根据资源的名字获取其ID值
 */
public class MResource {
	private static ILogger MyLog = LoggerFactory.getLogger("MResource");
	/**
	 * 根据资源的名字获取字符串
	 * @param context
	 * @param name
	 * @return
	 */
	public static String getString(Context context, String name) {
		String result = null;
		int id = getIdByName(context, "string", name);
		try{
		result = context.getString(id);
		} catch(Exception e){
			MyLog.e(e);
		}
		
		return result;
	}
	
	/**
	 * 根据资源的名字获取字符串,并且接收格式化参数
	 * @param context
	 * @param name
	 * @param formatArgs
	 * @return
	 */
	public static String getString(Context context, String name, Object... formatArgs) {
		String result = null;
		int id = getIdByName(context, "string", name);
		try{
		result = context.getString(id, formatArgs);
		} catch(Exception e){
			MyLog.e(e);
		}
		
		return result;
	}
	
	public static Drawable getDrawable(Context context, String name){
		Drawable result = null;
		int id = getIdByName(context, "drawable", name);
		try{
		result = context.getResources().getDrawable(id);
		} catch(Exception e){
			MyLog.e(e);
		} 
		
		return result;
	}
	
	public static Animation getAnimation(Context context, String name){
		Animation result = null;
		int id = getIdByName(context, "anim", name);
		try{
			result = AnimationUtils.loadAnimation(context, id);
		} catch(Exception e){
			MyLog.e(e);
		} 
		
		return result;
	}
	
	public static int getIdByName(Context context, String className, String name) {
		String packageName = ClientInfo.getPackageName();
		int id = 0;
		String tarClassName = packageName + ".R$" + className;
		try {
			Class<?> desireClass = Class.forName(tarClassName);
			id = desireClass.getField(name).getInt(null);
		} catch (ClassNotFoundException e) {
			MyLog.e(e);
		}  catch (NoSuchFieldException e) {
			MyLog.e("field '" + name + "' not found from class:" + tarClassName,
					e);
		}catch (Exception e) {
			MyLog.e(e);
		}
		return id;
	}
	
	public static int[] getIdsByName(Context context, String className, String name) {
		String packageName = ClientInfo.getPackageName();
		String tarClassName = packageName + ".R$" + className;
		int[] ids = null;
		try {
			Class<?> desireClass = Class.forName(tarClassName);
			java.lang.reflect.Field field = desireClass.getField(name);
			if(field.getType().isArray()){
				ids  = (int[]) field.get(null);
			}
		} catch (Exception e) {
			MyLog.e(e);
		}
		return ids;
	}
}
