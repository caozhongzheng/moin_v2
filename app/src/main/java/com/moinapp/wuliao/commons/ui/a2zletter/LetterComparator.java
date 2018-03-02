package com.moinapp.wuliao.commons.ui.a2zletter;

import java.util.Comparator;

/**
 * Created by moying on 15/7/8.
 */
public class LetterComparator implements Comparator<String> {
    @Override
    public int compare(String s, String t) {
        String s1 = convertToPinyin(s);
        String s2 = convertToPinyin(t);
        return s1.compareToIgnoreCase(s2);
    }

    private String convertToPinyin(String str) {
        char[] arr = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            char c = arr[i];

            for(String s: LetterUtil.getFirstPinyin(c)){
                sb.append(s);
            }
            
        }
        String result = new String(sb);
//        System.out.println(str + " 所得字符串： " + result);
        return result;
    }
}
