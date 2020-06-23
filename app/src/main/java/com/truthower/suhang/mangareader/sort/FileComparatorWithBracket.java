package com.truthower.suhang.mangareader.sort;

import android.util.Log;

import java.util.Comparator;
import java.util.regex.Pattern;

public class FileComparatorWithBracket implements Comparator<String> {
    private int chapterL;
    private int chapterR;

    @Override
    public int compare(String lhs, String rhs) {
        analysis(lhs, true);
        analysis(rhs, false);

        int res;
        if (chapterL > chapterR) {
            res = 1;
        } else if (chapterL < chapterR) {
            res = -1;
        } else {
            res = 0;
        }
        // 返回-1代表前者小，0代表两者相等，1代表前者大。
        return res;
    }

    private void analysis(String s, boolean which) {
        if (s.contains(".jpg") || s.contains(".png") || s.contains(".bmp")||s.contains(".gif")) {
            s = s.substring(0, s.length() - 1 - 4);
            Log.d("s", "裁剪后的字符串" + s);
        } else if (s.contains(".jpeg")) {
            s = s.substring(0, s.length() - 1 - 5);
            Log.d("s", "裁剪后的字符串" + s);
        }
        // 从文件名中得到章节和页码
        String[] arr = s.split("\\(");
        Pattern p;
        p = Pattern.compile("^\\d*$");// 数字的正则表达式
        for (int i = 0; i < arr.length; i++) {
            if (p.matcher(arr[i]).matches()) {
                // 如果符合正则表达式
                Log.d("排序", "数组" + arr[i]);
                if (which) {
                    // true代表L
                    // 章节
                    chapterL = Integer.valueOf(arr[i]);
                    Log.d("排序L", chapterL + "章");

                } else {
                    // false代表R
                    // 章节
                    chapterR = Integer.valueOf(arr[i]);
                    Log.d("排序R", chapterR + "章");
                }

            }
        }
    }
}
