package com.truthower.suhang.mangareader.utils;/**
 * Created by Administrator on 2016/10/27.
 */

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import java.util.ArrayList;

/**
 * 作者：苏航 on 2016/10/27 10:56
 * 邮箱：772192594@qq.com
 */
public class UltimateTextSizeUtil {
    public static SpannableString getEmphasizedSpannableString(String text, ArrayList<String> emphasizeTexts,
                                                               int textSize, int color, int textStyle) {
        String[] empasize = new String[emphasizeTexts.size()];
        for (int i = 0; i < emphasizeTexts.size(); i++) {
            empasize[i] = emphasizeTexts.get(i);
        }
        return getEmphasizedSpannableString(text, empasize, textSize, color, textStyle);
    }

    public static SpannableString getEmphasizedSpannableString(String text, String emphasizeText,
                                                               int textSize, int color, int textStyle) {
        String[] empasize = new String[1];
        empasize[0] = emphasizeText;
        return getEmphasizedSpannableString(text, empasize, textSize, color, textStyle);
    }

    public static SpannableString getEmphasizedSpannableString(String text, String[] emphasizeTexts,
                                                               int textSize, int color, int textStyle) {
        SpannableString spannableString = new SpannableString(text);
        String emphasizeText;
        int start = -1, end = 0;

        for (int i = 0; i < emphasizeTexts.length; i++) {
            emphasizeText = emphasizeTexts[i];
            if (!text.contains(emphasizeText)) {
                break;
            }
            //从上一个的结束开始搜 否则如果有重复的就会有bug
            start = text.indexOf(emphasizeText, end);

            end = start + emphasizeText.length();

            if (start == -1) {
                break;
            }
            //必须得每次都new一遍 否则只有一个起效
            if (color != 0) {
                spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (textSize != 0) {
                spannableString.setSpan(new AbsoluteSizeSpan(textSize, true), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (textStyle != 0) {
                spannableString.setSpan(new StyleSpan(textStyle), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }
}
