package com.truthower.suhang.mangareader.base;

import android.os.Bundle;
import android.view.View;

import com.truthower.suhang.mangareader.R;

import androidx.fragment.app.FragmentTransaction;


/**
 * 投资记录页
 */
public abstract class FragmentContainerActivity extends BaseFragmentActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    protected abstract BaseFragment getFragment();

    protected abstract String getTopBarTitle();

    private void initUI() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base_container, getFragment());
        transaction.commit();

        baseTopBar.setTitle(getTopBarTitle());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_base;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

}
