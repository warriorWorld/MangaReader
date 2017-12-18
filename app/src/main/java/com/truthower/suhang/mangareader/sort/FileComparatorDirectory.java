package com.truthower.suhang.mangareader.sort;

import android.util.Log;

import java.util.Comparator;

public class FileComparatorDirectory implements Comparator<String> {
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
        String[] arri = s.split("/");
        s = arri[arri.length - 1];
        String[] arri1=s.split("-");
        s=arri1[0];
        try {
            if (which) {
                chapterL = Integer.valueOf(s);
            } else {
                chapterR = Integer.valueOf(s);
            }
        } catch (Exception e) {
            chapterL = chapterR = 0;
        }
    }
}
