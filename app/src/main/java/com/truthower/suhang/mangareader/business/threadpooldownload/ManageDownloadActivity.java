package com.truthower.suhang.mangareader.business.threadpooldownload;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;

public class ManageDownloadActivity extends BaseActivity implements View.OnClickListener {
    private EditText urlEt;
    private Button downloadBtn;
    private Button downloadBtn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2020-04-12 13:49:22 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void initUI() {
        urlEt = (EditText) findViewById(R.id.url_et);
        downloadBtn = (Button) findViewById(R.id.download_btn);
        downloadBtn1 = (Button) findViewById(R.id.download_btn1);

        downloadBtn.setOnClickListener(this);
        downloadBtn1.setOnClickListener(this);
    }

    private void handleURLs(){
        String content=urlEt.getText().toString().replaceAll(" ","");
        urlEt.setText(content);
        if (TextUtils.isEmpty(content)){
            return;
        }
        if (content.contains("\n")){
            String[] urls=content.split("\n");

        }else {

        }
    }
    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2020-04-12 13:49:22 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == downloadBtn) {
            // Handle clicks for downloadBtn
        } else if (v == downloadBtn1) {
            // Handle clicks for downloadBtn1
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_managedownload;
    }
}
