package com.truthower.suhang.mangareader.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.widget.toast.EasyToast;

import java.io.File;

/**
 * Created by Administrator on 2016/10/10.
 */
public class QrDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView copyImgTv;
    private ImageView imageView;

    public QrDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_qrcode);
        init();
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager wm = ((Activity) context).getWindowManager();
        Display d = wm.getDefaultDisplay();
        // lp.height = (int) (d.getHeight() * 0.4);
        lp.width = (int) (d.getWidth() * 0.75);
        // window.setGravity(Gravity.LEFT | Gravity.TOP);
        window.setGravity(Gravity.CENTER);
//        window.getDecorView().setPadding(0, 0, 0, 0);
        // lp.x = 100;
        // lp.y = 100;
        // lp.height = 30;
        // lp.width = 20;
        window.setAttributes(lp);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void init() {
        copyImgTv = (TextView) findViewById(R.id.copy_qr_tv);
        imageView = (ImageView) findViewById(R.id.image_dialog_iv);
        copyImgTv.setOnClickListener(this);
    }

    public void setImg(String path) {
        ImageLoader.getInstance().displayImage(path, imageView, Configure.smallImageOptions);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.copy_qr_tv:
//                BitmapDrawable drawble = (BitmapDrawable) (context.getResources().getDrawable(R.drawable.qr_172));
//                String fileName = "/"+context.getResources().getString(R.string.app_name) + "二维码.png";
//                String folderName = "App二维码";
//                FileSpider.getInstance().saveBitmap(drawble.getBitmap(), folderName, fileName);
//
                EasyToast toast = new EasyToast(context);
                toast.showToast("已将二维码保存至manga文件夹", true);
                dismiss();
                break;
        }
        dismiss();
    }
}
