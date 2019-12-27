package com.truthower.suhang.mangareader.widget.shotview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

import com.truthower.suhang.mangareader.utils.DisplayUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScreenShot {
    // 获取指定Activity的截屏，保存到png文件
    public static Bitmap takeScreenShot(Activity activity) {
        //View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        //获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        System.out.println(statusBarHeight);

        //获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = DisplayUtil.getScreenRealHeight(activity);
        //去掉标题栏
        //Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    // 获取指定Activity的截屏，保存到png文件
    public static Bitmap takeScreenShot(Dialog dialog,int width,int height) {
        try {
            //View是你需要截图的View
            View view = dialog.getWindow().getDecorView().getRootView();
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap b1 = view.getDrawingCache();

            //获取状态栏高度
            Rect frame = new Rect();
            dialog.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            int statusBarHeight = frame.top;
            System.out.println(statusBarHeight);

            //去掉标题栏
            //Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
            Bitmap b = Bitmap.createBitmap(b1, 0, 0, width, height);
            view.destroyDrawingCache();
            return b;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //保存到sdcard
    private static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //程序入口
    public static void shoot(Activity a) {
        ScreenShot.savePic(ScreenShot.takeScreenShot(a), "sdcard/xx.png");
    }
}