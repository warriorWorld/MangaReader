package com.truthower.suhang.mangareader.utils;

/**
 * Created by Administrator on 2018/4/9.
 */

public class ReplaceUtil {
    public static String getWordAgain(String s) {
        String str = s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&;*（）——+|{}【】\"‘；：”“’。，、？|]", "");
        //留着空格
//        str = str.replaceAll("\n", "");
//        str = str.replaceAll("\r", "");
//        str = str.replaceAll("\\s", "");
        str = str.replaceAll("_", "");
        return str;
    }

    public static String onlyNumber(String s) {
        String str = s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&;*（）——+|{}【】\"‘；：”“’。，、？|]", "");
        str = str.replaceAll("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ]", "");
        //留着空格
//        str = str.replaceAll("\n", "");
//        str = str.replaceAll("\r", "");
//        str = str.replaceAll("\\s", "");
        str = str.replaceAll("_", "");
        return str;
    }
}
