package com.truthower.suhang.mangareader.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;

public class TestActivity extends BaseActivity implements View.OnClickListener {
    private Button testBtn, testBtn1;
    private MangaDialog mDialog;
    private String toastOut = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        testBtn = (Button) findViewById(R.id.test_btn);
        testBtn.setOnClickListener(this);
        testBtn1 = (Button) findViewById(R.id.test_btn1);
        testBtn1.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    private void test(final String title, String message, final String toast) {
        if (null == mDialog) {
            mDialog = new MangaDialog(TestActivity.this);
            mDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                    baseToast.showToast(toast+"\n"+toastOut);
                }

                @Override
                public void onCancelClick() {

                }
            });
        }
        mDialog.show();
        mDialog.setTitle(title);
        mDialog.setMessage(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test_btn:
                toastOut="btn";
                test("btn", "btn", "btn");
                break;
            case R.id.test_btn1:
                toastOut="btn1";
                test("btn1", "btn1", "btn1");
                break;
        }
    }
}
