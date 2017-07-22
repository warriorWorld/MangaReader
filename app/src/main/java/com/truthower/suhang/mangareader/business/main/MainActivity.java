package com.truthower.suhang.mangareader.business.main;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.base.BaseFragmentActivity;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;


public class MainActivity extends BaseFragmentActivity implements View.OnClickListener {
    private LinearLayout mTabOnlinePageLL, mTabLocalLL, mTabUserLL;
    /**
     * Tab显示内容TextView
     */
    private TextView mTabOnlinePageTv, mTabLocalTv, mTabUserTv;
    private ImageView mTabOnlinePageIv, mTabLocalIv, mTabUserIv;
    /**
     * Fragment
     */
    private OnlineMangaFragment onlinePageFg;
    private LocalMangaFragment localFg;
    private UserFragment userFg;
    /**
     * 当前选中页
     */
    private BaseFragment curFragment;

    private MangaDialog logoutDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏透明
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        hideBaseTopBar();
        initUI();
        initFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void initUI() {
        mTabLocalTv = (TextView) this.findViewById(R.id.local_bottom_tv);
        mTabOnlinePageTv = (TextView) this.findViewById(R.id.online_bottom_tv);
        mTabUserTv = (TextView) this.findViewById(R.id.user_bottom_tv);
        mTabOnlinePageIv = (ImageView) findViewById(R.id.online_bottom_iv);
        mTabLocalIv = (ImageView) findViewById(R.id.local_bottom_iv);
        mTabUserIv = (ImageView) findViewById(R.id.user_bottom_iv);
        mTabOnlinePageLL = (LinearLayout) findViewById(R.id.online_bottom_ll);
        mTabLocalLL = (LinearLayout) findViewById(R.id.local_bottom_ll);
        mTabUserLL = (LinearLayout) findViewById(R.id.user_bottom_ll);


        mTabOnlinePageLL.setOnClickListener(this);
        mTabLocalLL.setOnClickListener(this);
        mTabUserLL.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private void initFragment() {
        localFg = new LocalMangaFragment();
        userFg = new UserFragment();
        onlinePageFg = new OnlineMangaFragment();

        switchContent(null, onlinePageFg);
    }

    /**
     * 在主线程中执行,eventbus遍历所有方法,就为了找到该方法并执行.传值自己随意写
     *
     * @param event
     */
    public void onEventMainThread(EventBusEvent event) {
        if (null == event)
            return;
        switch (event.getEventType()) {
        }
    }


    public void switchContent(BaseFragment from, BaseFragment to) {
        if (curFragment != to) {
            curFragment = to;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (!to.isAdded()) { // 先判断是否被add过
                if (null != from) {
                    transaction.hide(from);
                }
                transaction.add(R.id.container, to, to.getFragmentTag())
                        .addToBackStack(to.getTag()).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                if (null != from) {
                    transaction.hide(from);
                }
                transaction.show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    @Override
    public void onBackPressed() {
        showLogoutDialog();
    }


    private void showLogoutDialog() {
        if (null == logoutDialog) {
            logoutDialog = new MangaDialog(MainActivity.this);
            logoutDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                    MainActivity.this.finish();
                }

                @Override
                public void onCancelClick() {

                }
            });
        }
        logoutDialog.show();

        logoutDialog.setTitle("确定退出?");
        logoutDialog.setOkText("退出");
        logoutDialog.setCancelText("再逛逛");
        logoutDialog.setCancelable(true);
    }

    private void toggleBottomBar(View v) {
        mTabOnlinePageIv.setImageResource(R.drawable.home_unclick);
        mTabLocalIv.setImageResource(R.drawable.invest_unclick);
        mTabUserIv.setImageResource(R.drawable.user_unclick);
        mTabOnlinePageTv.setTextColor(getResources().getColor(R.color.main_text_color_gray));
        mTabLocalTv.setTextColor(getResources().getColor(R.color.main_text_color_gray));
        mTabUserTv.setTextColor(getResources().getColor(R.color.main_text_color_gray));
        switch (v.getId()) {
            case R.id.online_bottom_ll:
                mTabOnlinePageIv.setImageResource(R.drawable.home_click);
                mTabOnlinePageTv.setTextColor(getResources().getColor(R.color.manga_reader));
                break;
            case R.id.local_bottom_ll:
                mTabLocalIv.setImageResource(R.drawable.invest_click);
                mTabLocalTv.setTextColor(getResources().getColor(R.color.manga_reader));
                break;
            case R.id.user_bottom_ll:
                mTabUserIv.setImageResource(R.drawable.user_click);
                mTabUserTv.setTextColor(getResources().getColor(R.color.manga_reader));
                break;
        }
    }


    @Override
    public void onClick(View v) {
        toggleBottomBar(v);
        switch (v.getId()) {
            case R.id.online_bottom_ll:
                switchContent(curFragment, onlinePageFg);
                break;
            case R.id.local_bottom_ll:
                switchContent(curFragment, localFg);
                break;
            case R.id.user_bottom_ll:
                switchContent(curFragment, userFg);
                break;
        }
    }
}
