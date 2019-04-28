package com.truthower.suhang.mangareader.widget.dialog;/**
 * Created by Administrator on 2016/11/4.
 */

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.spider.FileSpider;

import java.io.File;


/**
 * 作者：苏航 on 2016/11/4 11:08
 * 邮箱：772192594@qq.com
 */
public class MangaImgConfirmDialog extends MangaImgDialog implements View.OnClickListener {
    private String saveName;
    private Button okBtn;
    private Button cancelBtn;

    public MangaImgConfirmDialog(Context context) {
        super(context);
    }

    protected int getLayoutId() {
        return R.layout.dialog_image_confirm;
    }

    @Override
    protected void init() {
        super.init();
        okBtn = (Button) findViewById(R.id.ok_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);

        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (v == okBtn) {
            FileSpider.saveBitmap(bpRescource, Configure.WORDS_FOLDER_NAME, File.separator + saveName + ".png");
        }
    }
}
