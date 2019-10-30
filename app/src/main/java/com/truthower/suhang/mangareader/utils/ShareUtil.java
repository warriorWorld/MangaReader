package com.truthower.suhang.mangareader.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;

import com.truthower.suhang.mangareader.R;

import java.util.ArrayList;

public class ShareUtil {
    private static final String EMAIL_ADDRESS = "songyuepeng@ihsmf.com";

    //文案分享
    public static void shareText(Context context, String text, String title) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //分享到QQ，可将包名改为com.tencent.mobileqq，分享页面名改为com.tencent.mobileqq.activity.JumpActivity
        intent.setComponent(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"));
        context.startActivity(intent);
//        context.startActivity(Intent.createChooser(intent, title));
    }

    //单张图片分享
    public static void shareImage(Context context, String title) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/png");
        //将项目图片转换为uri
        BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(R.mipmap.ic_launcher);
        Bitmap bt = bd.getBitmap();
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bt, null, null));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        //若是分享到QQ，可将包名改为com.tencent.mobileqq，分享页面名改为com.tencent.mobileqq.activity.JumpActivity
        //微信朋友圈"com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI"
        intent.setComponent(new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity"));
        context.startActivity(intent);
//        context.startActivity(Intent.createChooser(intent, title));
    }

    public static void sendEmail(Context context, String title) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + EMAIL_ADDRESS));
        context.startActivity(Intent.createChooser(intent, title));
    }

    //多张图片分享
    public static void sendMoreImage(Context context, ArrayList<Uri> imageUris, String title) {
        Intent mulIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        mulIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        mulIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(mulIntent, "多图文件分享"));
    }
}
