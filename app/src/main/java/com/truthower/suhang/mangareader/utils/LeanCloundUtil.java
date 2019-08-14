//package com.truthower.suhang.mangareader.utils;
//
//import android.content.Context;
//import android.view.WindowManager;
//
//import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
//
//import cn.bmob.v3.exception.BmobException;
//
///**
// * Created by Administrator on 2017/7/29.
// */
//
//public class LeanCloundUtil {
//    public static boolean handleLeanResult(BmobException e) {
//        return e == null;
//    }
//
//    public static boolean handleLeanResult(Context context, BmobException e) {
//        if (e == null) {
//            return true;
//        } else {
//            try {
//                MangaDialog errorDialog = new MangaDialog(context);
//                errorDialog.show();
//                errorDialog.setTitle("出错了");
//                errorDialog.setMessage(e.getMessage());
//            } catch (WindowManager.BadTokenException exception) {
//                exception.printStackTrace();
//            }
//            return false;
//        }
//    }
//}
