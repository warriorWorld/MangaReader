package com.truthower.suhang.mangareader.utils;/**
 * Created by Administrator on 2016/10/27.
 */

import android.text.TextUtils;

import java.math.RoundingMode;

/**
 * 作者：苏航 on 2016/10/27 14:15
 * 邮箱：772192594@qq.com
 */
public class NumberUtil {
    public static String toCommaNum(double num) {
        try {
            //这种方式会导致数值不精确
//        int numI = (int) (num * 100);
//        num = numI / 100f;
            java.text.DecimalFormat df = new java.text.DecimalFormat("###,##0.00");
            df.setRoundingMode(RoundingMode.FLOOR);
            return df.format(num);
        } catch (Exception e) {
            return num + "";
        }
    }

    public static String toCommaNum(String num) {
        if (TextUtils.isEmpty(num)) {
            return "";
        }
        try {
            return toCommaNum(Double.valueOf(num));
        } catch (Exception e) {
            return num + "";
        }
    }

    public static String toCommaNumButNoDot(String num) {
        if (TextUtils.isEmpty(num)) {
            return "";
        }
        try {
            return toCommaNumButNoDot(Double.valueOf(num));
        } catch (Exception e) {
            return num + "";
        }
    }

    public static String toCommaNumButNoDot(double num) {
        try {
            java.text.DecimalFormat df = new java.text.DecimalFormat("###,###.##");
            return df.format(num);
        } catch (Exception e) {
            return num + "";
        }
    }

    public static String cutNum(double num) {
        try {
//        int numI = (int) (num * 100);
//        num = numI / 100f;
            //##.##代表0去掉,
            java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.FLOOR);
            return df.format(num);
        } catch (Exception e) {
            return num + "";
        }
    }

    public static String doubleDecimals(float num) {
        try {
//        int numI = (int) (num * 100);
//        num = numI / 100f;
            java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
            df.setRoundingMode(RoundingMode.FLOOR);
            return df.format(num);
        } catch (Exception e) {
            return num + "";
        }
    }

    //四舍五入的方式
    public static String toCommaNumRound(String num) {
        if (TextUtils.isEmpty(num)) {
            return "";
        }
        try {
            return toCommaNumRound(Double.valueOf(num));
        } catch (Exception e) {
            return num + "";
        }
    }

    //四舍五入的方式
    public static String toCommaNumRound(double num) {
        try {
            java.text.DecimalFormat df = new java.text.DecimalFormat("###,##0.00");
            return df.format(num);
        } catch (Exception e) {
            return num + "";
        }
    }

    public static String cutNum(String num) {
        if (TextUtils.isEmpty(num)) {
            return "0";
        }
        try {
            return cutNum(Double.valueOf(num));
        } catch (Exception e) {
            return num + "";
        }
    }

    public static String toDoubleNum(double num) {
        try {
            java.text.DecimalFormat df = new java.text.DecimalFormat("00");
            return df.format(num);
        } catch (Exception e) {
            return num + "";
        }
    }
}
