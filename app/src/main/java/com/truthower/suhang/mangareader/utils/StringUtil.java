package com.truthower.suhang.mangareader.utils;

public class StringUtil {
    public static boolean checkLength(String str, int length) {
        int valueLength = 0;

        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度�?2，否则为1 */
        for (int i = 0; i < str.length(); i++) {
            /* 获取�?个字�? */
            String temp = str.substring(i, i + 1);
			/* 判断是否为中文字�? */
            if (temp.matches(chinese)) {
				/* 中文字符长度�?2 */
                valueLength += 2;
            } else {
				/* 其他字符长度�?1 */
                valueLength += 1;
            }
        }

        return length < valueLength;
    }

    /**
     * 截取指定位数的条码
     */
    public static String cutString(String string, int targetNum) {
        if (string.length() > targetNum) {
            string = string.substring(0, targetNum);
            Logger.d("截取方法" + string);
        }
        return string;
    }

    /**
     * 截取webservice,专用
     */
    public static String cutString(String string) {
        if (string.length() > 48) {
            int end = string.indexOf("</string>");
            string = string.substring(48, end);
            Logger.d("截取方法" + string);
        }
        return string;
    }

    public static String cutString(String string, int start, int end) {
        if (string.length() > 0) {
            string = string.substring(start, end);
        }
        return string;
    }

    /**
     * 判断是否是规范的数字
     */
    public static boolean verifyNumber(String string) {
        if (string.length() == 1 && "+".equals(string) || "-".equals(string)
                || ".".equals(string)) {
            return false;
        }
        return true;
    }
}
