package com.moinapp.wuliao.utils;

import android.content.Context;
import android.text.TextUtils;

import com.moinapp.wuliao.R;
import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.sensitiveWord.SensitivewordFilter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtil {

    private static ILogger MyLog = LoggerFactory.getLogger("StringUtil");
    private static final long MINUTE = 60 * 1000;
    private static final long HOUR = 60 * MINUTE;
    private static final long ONE_DAY = 24 * HOUR;

    public static SensitivewordFilter mSensitivewordFilter;

    static {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mSensitivewordFilter = new SensitivewordFilter();
            }
        }).start();
    }
    /**
     * @param longDateTime 1970年1月1日到现在的毫秒数 ，类型为long
     * @param pattern     格式，如日期yyyy-MM-dd，如日期加时间yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String formatDate(long longDateTime, String pattern) {
        if (longDateTime == 0) {
            return "";
        }
        try {
            return new SimpleDateFormat(pattern).format(new Date(longDateTime));
        } catch (Exception e) {
            MyLog.e("formatDate " + e.toString());
            return "";
        }
    }

    /**
     * 根据格式化时间字符串时间，返回1970年1月1日到此时间的毫秒数
     *
     * @param formatDate
     * @param pattern    格式，如日期yyyy-MM-dd，如日期加时间yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static long getDateTime(String formatDate, String pattern) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.parse(formatDate).getTime();
        } catch (Exception e) {
            MyLog.e("getDateTime " + e.toString());
            return -1;
        }
    }

    /**
     * 是否为null或""
     *
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.length() == 0;
    }

    /**
     * null转为""
     *
     * @param str
     * @return
     */
    public static String nullToEmpty(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * null转为"",为了不报错临时用，要删掉。????
     *
     * @param param
     * @return
     */
    public static String nullToEmpty(int param) {
        if (param == 0) {
            return "";
        } else {
            return String.valueOf(param);
        }
    }
    public static boolean isEmail(String strEmail) {
        String strPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(strEmail);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCellphone(String strPhone) {
        Pattern pattern = Pattern.compile("1[0-9]{10}");
        Matcher matcher = pattern.matcher(strPhone);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    public static char[] passwordDigits() {
        return new char[] {'1','2','3','4','5','6','7','8','9','0',
                ',','.','+','-','*','/','_','=','%','[',']','{','}','\\','|','~','`','!','@','#','$','^','(',')','?',
                'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
                'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
    }
    /**
     * 输出M、K等单位
     *
     * @param size
     * @return
     */
    public static String formatSize(long size) {
        if (size <= 0) {
            return "";
        }
        DecimalFormat df = new DecimalFormat("###.#");
        float f;
        f = (float) ((float) size / (float) (1024 * 1024));
        if (f < 0.1f)
            f = 0.1f;
        return (df.format(new Float(f).doubleValue()) + "MB");
        /*if (size < 1024 * 1024) {
			f = (float) ((float) size / (float) 1024);
			return (df.format(new Float(f).doubleValue()) + "K");
		} else {
			f = (float) ((float) size / (float) (1024 * 1024));
			return (df.format(new Float(f).doubleValue()) + "M");
		}*/
    }

    /**
     * 获取0到size范围内（包括0和size）的2个随机数。
     *
     * @param size
     */
    public static int[] getTwoRandomNum(int size) {
        int[] intRet = new int[2];
        intRet[0] = (int) Math.round(Math.random() * size);
        intRet[1] = (int) Math.round(Math.random() * size);
        return intRet;
    }

	public static Object getExt(String strIconUrl) {
		// TODO Auto-generated method stub
		String JPG = ".jpg";
		String JPEG = ".jpeg";
		String PNG = ".png";
		String ext = strIconUrl.substring(strIconUrl.lastIndexOf(".")).toLowerCase();
		if(ext.startsWith(JPG) || ext.startsWith(JPEG))
			return JPG;
		else if(ext.startsWith(PNG))
			return PNG;
		if(ext.contains("?"))
			return ext.substring(0, ext.lastIndexOf("?"));
		return ext;
	}

    public static String humanDate(long longDateTime, String pattern) {
        if (longDateTime == 0) {
            return "";
        }
        // pattern :yyyy.MM.dd
//        long now = System.currentTimeMillis();
        long now = new Date().getTime();
        long minus = now - longDateTime;
        if(ONE_DAY < minus) {
            return formatDate(longDateTime, pattern);
        } else if(HOUR < minus) {
            return (minus/HOUR) + ApplicationContext.getContext().getString(R.string.hours_before);
        } else if(MINUTE < minus) {
            return (minus/MINUTE) + ApplicationContext.getContext().getString(R.string.minutes_before);
        } else {
            return ApplicationContext.getContext().getString(R.string.just_before);
        }
    }

    public static String getGender(Context context, String sex, boolean isShow) {
        String man = context.getResources().getString(R.string.male);
        String woman = context.getResources().getString(R.string.female);
        if("1".equals(sex) || "male".equals(sex) || man.equals(sex)) {
            return isShow ? man : "male";
        } else if("2".equals(sex) || "female".equals(sex) || woman.equals(sex)) {
            return isShow ? woman : "female";
        } else {
            return context.getResources().getString(R.string.unknown_gender);
        }
    }

    /**
     *  list转成逗号分隔的字符串（与服务器约定好的）
     */
    public static String list2String(List<String> pics) {
        return list2String(pics, ",");
    }

    /**
     *  list转成逗号分隔的字符串（与服务器约定好的）
     */
    public static String list2String(List<String> pics, String separator) {
        if (pics == null || pics.size() == 0) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (String pic:pics) {
            if (!TextUtils.isEmpty(pic)) {
                builder.append(pic).append(separator);
            }
        }

        String s = builder.toString();
        if (s.length() > 0)
            return s.substring(0, s.length()-separator.length());
        else
            return null;
    }


    /**
     *  过滤敏感词
     * */
    public static String filter(String txt) {
        long beginTime = System.currentTimeMillis();
        Set<String> set = mSensitivewordFilter.getSensitiveWord(txt, 2);
        String result = mSensitivewordFilter.replaceSensitiveWord(txt, 2, "*");
        long endTime = System.currentTimeMillis();
        MyLog.i("语句中包含敏感词的个数为：" + set.size() + "。包含：" + set);
        Iterator<String> iterator = set.iterator();
        while(iterator.hasNext()) {
            String key = iterator.next();
            MyLog.i("包含敏感词:"+key);
        }
        MyLog.i("总共消耗时间为：" + (endTime - beginTime));
        return result;
    }
}
