package com.truthower.suhang.mangareader.base;/**
 * Created by Administrator on 2016/10/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.toast.EasyToast;

import org.greenrobot.eventbus.EventBus;


/**
 * 作者：苏航 on 2016/10/17 11:56
 * 邮箱：772192594@qq.com
 */
public abstract class BaseActivity extends Activity {
    protected TopBar baseTopBar;
    protected EasyToast baseToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        baseToast = new EasyToast(this);
        // 在oncreate里订阅
        EventBus.getDefault().register(this);
    }

    private void initUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_base);
        baseTopBar = (TopBar) findViewById(R.id.base_topbar);
        ViewGroup containerView = (ViewGroup) findViewById(R.id.base_container);
        LayoutInflater.from(this).inflate(getLayoutId(), containerView);

        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                topBarOnLeftClick();
            }

            @Override
            public void onRightClick() {
                topBarOnRightClick();
            }

            @Override
            public void onTitleClick() {
                topBarOnTitleClick();
            }
        });
    }


    protected abstract int getLayoutId();

    protected void hideBaseTopBar() {
        baseTopBar.setVisibility(View.GONE);
    }

    protected void hideBack() {
        baseTopBar.hideLeftButton();
    }

    protected void topBarOnLeftClick() {
        this.finish();
    }

    protected void topBarOnRightClick() {

    }

    protected void topBarOnTitleClick() {

    }

    /**
     * 在主线程中执行,eventbus遍历所有方法,就为了找到该方法并执行.传值自己随意写
     *
     * @param event
     */
    public void onEventMainThread(EventBusEvent event) {
        if (null == event)
            return;
        Intent intent = null;
        switch (event.getEventType()) {
//            case EventBusEvent.NEED_LOGIN:
//                ToastUtil.tipShort(BaseActivity.this, "需要登录");
//                intent = new Intent(BaseActivity.this, LoginActivity.class);
//                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 每次必须取消订阅
        EventBus.getDefault().unregister(this);
    }
}
