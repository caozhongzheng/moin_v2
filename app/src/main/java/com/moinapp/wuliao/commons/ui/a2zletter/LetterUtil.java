package com.moinapp.wuliao.commons.ui.a2zletter;

import net.sourceforge.pinyin4j.PinyinHelper;

/**
 * 字母工具类
 * Created by moying on 15/7/8.
 */
public class LetterUtil {
    /**
     * @param chinese 一个汉字
     * @return 拼音首字母
     * @Description:
     */
    public static String[] getFirstPinyin(char chinese)
    {
        return PinyinHelper.toHanyuPinyinStringArray(chinese);
    }

    /**
     * 是否是字母
     *
     * @return true 字母¸,false 非字母
     * @Description:
     */
    public static boolean isLetter(char c)
    {
        return (c >= 65 && c <= 90) || (c >= 97 && c <= 112);
    }
}
