package com.truthower.suhang.mangareader.widget.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.config.Configure;

public class ImgKeyboardDialog extends KeyBoardDialog {
    private ImageView imgIv;

    public ImgKeyboardDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_image_keyboard;
    }

    @Override
    protected void init() {
        super.init();
        imgIv = (ImageView) findViewById(R.id.image_view);
        imgIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setImgRes(String uri) {
        ImageLoader.getInstance().displayImage(uri, imgIv, Configure.smallImageOptions);
//        ImageLoader.getInstance().displayImage(uri, imgIv);
    }

    public void setImgRes(Bitmap bitmap) {
        imgIv.setImageBitmap(bitmap);
    }
}
