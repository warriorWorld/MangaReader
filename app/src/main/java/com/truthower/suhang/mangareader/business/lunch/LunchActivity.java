package com.truthower.suhang.mangareader.business.lunch;

import android.content.Intent;
import android.os.Bundle;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.business.main.MainActivity;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;

public class LunchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        BaseParameterUtil.getInstance();
        toNext();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initUI() {
        hideBaseTopBar();
    }

    private void toNext() {
        Intent intent = new Intent(LunchActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lunch;
    }
}
