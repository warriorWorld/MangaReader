package com.truthower.suhang.mangareader.business.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;


public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private ImageView crossIv;
    private EditText userIdEt;
    private EditText emailEt;
    private EditText userPsdEt;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        crossIv = (ImageView) findViewById(R.id.cross_iv);
        userIdEt = (EditText) findViewById(R.id.user_id_et);
        emailEt = (EditText) findViewById(R.id.email_et);
        userPsdEt = (EditText) findViewById(R.id.user_psd_et);
        registerBtn = (Button) findViewById(R.id.register_btn);
        crossIv.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

        hideBaseTopBar();
        setColorHolderColor(R.color.divide);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    private boolean checkData() {
        if (TextUtils.isEmpty(userIdEt.getText().toString())) {
            baseToast.showToast("请输入用户名!");
            return false;
        }
        if (TextUtils.isEmpty(emailEt.getText().toString())) {
            baseToast.showToast("请输入邮箱地址!");
            return false;
        }
        if (TextUtils.isEmpty(userPsdEt.getText().toString())) {
            baseToast.showToast("请输入密码!");
            return false;
        }
        return true;
    }


    private void doRegister() {
    }

    private void doNext() {
        baseToast.showToast("注册成功!");
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cross_iv:
                RegisterActivity.this.finish();
                break;
            case R.id.register_btn:
                if (checkData()) {
                    doRegister();
                }
                break;
        }
    }
}
