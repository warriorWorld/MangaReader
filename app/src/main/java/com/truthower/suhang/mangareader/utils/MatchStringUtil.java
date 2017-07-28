package com.truthower.suhang.mangareader.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchStringUtil {

    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    public static boolean isChinese(String text) {
        // 只能输入汉字
        if (!text.matches("[\u4e00-\u9fa5]+")) {
            return false;
        }
        return true;
    }

    // 座机判断
    public static boolean isTelePhoneNum(String text) {
        // 只能输入汉字
        if (text.length() != 11) {
            return false;
        }
        return true;
    }

    /**
     * 验证手机号码
     *
     * @param phoneNumber 手机号码
     * @return boolean
     */
    public static boolean checkPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() != 11) {
            return false;
        } else {
            return true;
        }
        //客户端不进行正则判断 交给服务端判断
//		// Pattern pattern = Pattern.compile("^1[0-9]{10}$");
//		Pattern pattern = Pattern
//				.compile("^(0\\d{2,3}-\\d{7,8}(-\\d{3,5}){0,1})|(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})$");
//		Matcher matcher = pattern.matcher(phoneNumber);
//		return matcher.matches();
    }

    /**
     * 验证密码
     */
    public static boolean checkPassword(String password) {
        if (password.length() < 6 || password.length() > 12) {
            return false;
        }

        if (password.matches("[A-Za-z0-9]+")) {
            Pattern p1 = Pattern.compile("[a-z]+");
            Pattern p2 = Pattern.compile("[A-Z]+");
            Pattern p3 = Pattern.compile("[0-9]+");
            Matcher m = p3.matcher(password);
            if (!m.find())
                return false;
            else {
                m.reset().usePattern(p2);
                if (!m.find() && !m.reset().usePattern(p1).find()) {
                    return false;
                }
                return true;
            }
        } else {
            return false;
        }
    }
}
