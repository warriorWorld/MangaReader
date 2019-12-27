package com.truthower.suhang.mangareader.widget.dialog;/**
 * Created by Administrator on 2016/11/4.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.utils.ImageUtil;
import com.truthower.suhang.mangareader.widget.toast.EasyToast;

import androidx.annotation.DrawableRes;


/**
 * 作者：苏航 on 2016/11/4 11:08
 * 邮箱：772192594@qq.com
 */
public class ShareDialog extends Dialog implements View.OnClickListener {
    protected Context context;
    private TextView shareTv;
    private ImageView shareIv;
    private Button shareBtn;
    private Button saveBtn;
    private Button closeBtn;
    private EasyToast mEasyToast;

    public ShareDialog(Context context) {
        super(context);
        this.context = context;
        mEasyToast = new EasyToast(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());
        init();

        Window window = this.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager wm = ((Activity) context).getWindowManager();
        Display d = wm.getDefaultDisplay();
        lp.width = (int) (d.getWidth() * 0.9);
        window.setAttributes(lp);
    }

    protected int getLayoutId() {
        return R.layout.dialog_share;
    }

    protected void init() {
        shareTv = (TextView) findViewById(R.id.share_tv);
        shareIv = (ImageView) findViewById(R.id.share_iv);
        shareBtn = (Button) findViewById(R.id.share_btn);
        saveBtn = (Button) findViewById(R.id.save_btn);
        closeBtn = (Button) findViewById(R.id.close_btn);

        shareBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
    }

    private String getResourcesUri(@DrawableRes int id) {
        Resources resources = context.getResources();
        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id);
        return uriPath;
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2019-12-27 17:13:12 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == shareBtn) {
            // Handle clicks for shareBtn
            String path = getResourcesUri(R.drawable.qrcode_manga);
            Intent imageIntent = new Intent(Intent.ACTION_SEND);
            imageIntent.setType("image/jpeg");
            imageIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
            context.startActivity(Intent.createChooser(imageIntent, "分享"));
        } else if (v == saveBtn) {
            // Handle clicks for saveBtn
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.qrcode_manga);
            ImageUtil.saveImageToGallery(context, bmp);
            mEasyToast.showToast("二维码已保存至系统相册");
            dismiss();
        } else if (v == closeBtn) {
            // Handle clicks for closeBtn
            dismiss();
        }
    }
}
