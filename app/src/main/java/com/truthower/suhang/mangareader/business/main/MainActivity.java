package com.truthower.suhang.mangareader.business.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.base.BaseFragmentActivity;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.user.CollectedActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.eventbus.JumpEvent;
import com.truthower.suhang.mangareader.eventbus.TagClickEvent;
import com.truthower.suhang.mangareader.listener.GetVersionListener;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.utils.Logger;
import com.truthower.suhang.mangareader.widget.dialog.DownloadDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends BaseFragmentActivity implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks {
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
    private String versionName, msg;
    private int versionCode;
    private MangaDialog versionDialog;
    private DownloadDialog downloadDialog;
    private AVFile downloadFile;

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
        if (null != curFragment) {
            curFragment.onHiddenChanged(false);
        }
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

    private void doGetVersionInfo() {
        AVQuery<AVObject> query = new AVQuery<>("VersionInfo");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (LeanCloundUtil.handleLeanResult(MainActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        versionName = list.get(0).getString("versionName");
                        versionCode = list.get(0).getInt("versionCode");
                        msg = list.get(0).getString("description");
                        downloadFile = list.get(0).getAVFile("apk");
                        if (BaseParameterUtil.getInstance(MainActivity.this).
                                getAppVersionCode() >= versionCode) {
//                            baseToast.showToast("已经是最新版啦~~");
                        } else {
                            showVersionDialog();
                        }
                    }
                }
            }
        });
    }

    private void showVersionDialog() {
        if (null == versionDialog) {
            versionDialog = new MangaDialog(MainActivity.this);
            versionDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                    doDownload();
                }

                @Override
                public void onCancelClick() {
                }
            });
        }
        versionDialog.show();

        versionDialog.setTitle("有新版本啦" + "v_" + versionName);
        versionDialog.setMessage(msg);
        versionDialog.setOkText("升级");
        versionDialog.setCancelable(false);

        versionDialog.setCancelText("忽略");
    }

    @AfterPermissionGranted(Configure.PERMISSION_FILE_REQUST_CODE)
    private void doDownload() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            showDownLoadDialog();
            final String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/manga/apk";
            final File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            downloadFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    // bytes 就是文件的数据流
                    if (null != downloadDialog && downloadDialog.isShowing()) {
                        downloadDialog.dismiss();
                    }
                    if (LeanCloundUtil.handleLeanResult(MainActivity.this, e)) {
                        File apkFile = FileSpider.getInstance().byte2File(bytes, filePath, "manga_reader.apk");

                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("android.intent.action.VIEW");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                        startActivity(intent);
                    }
                }
            }, new ProgressCallback() {
                @Override
                public void done(Integer integer) {
                    // 下载进度数据，integer 介于 0 和 100。
                    downloadDialog.setProgress(integer);
                }
            });

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    Configure.PERMISSION_FILE_REQUST_CODE, perms);
        }
    }

    private void showDownLoadDialog() {
        if (null == downloadDialog) {
            downloadDialog = new DownloadDialog(this);
        }
        downloadDialog.show();
        downloadDialog.setCancelable(false);
    }

    private void initFragment() {
        localFg = new LocalMangaFragment();
        userFg = new UserFragment();
        userFg.setGetVersionListener(new GetVersionListener() {
            @Override
            public void onGetVersionClick() {
                doGetVersionInfo();
            }
        });
        onlinePageFg = new OnlineMangaFragment();

        switchContent(null, onlinePageFg);
    }

    /**
     * 在主线程中执行,eventbus遍历所有方法,就为了找到该方法并执行.传值自己随意写
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(JumpEvent event) {
        if (null == event)
            return;
        if (event.getEventType() == EventBusEvent.JUMP_EVENT) {
            switch (event.getJumpPoint()) {
                case 0:
                    switchContent(curFragment, onlinePageFg);
                    toggleBottomBar(mTabOnlinePageLL);
                    break;
                case 1:
                    switchContent(curFragment, localFg);
                    toggleBottomBar(mTabLocalLL);
                    break;
                case 2:
                    switchContent(curFragment, userFg);
                    toggleBottomBar(mTabUserLL);
                    break;
            }
        }
    }

    @Subscribe
    public void onEventMainThread(TagClickEvent event) {
        if (null == event)
            return;
        if (event.getEventType() == EventBusEvent.TAG_CLICK_EVENT) {
            if (TextUtils.isEmpty(event.getSelectCode())) {
                onlinePageFg.toggleTag(event.getSelectTag());
            } else {
                onlinePageFg.toggleTag(event.getSelectTag(), event.getSelectCode());
            }
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
            to.onHiddenChanged(false);
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
        mTabOnlinePageTv.setTextColor(getResources().getColor(R.color.main_text_color_gray));
        mTabLocalTv.setTextColor(getResources().getColor(R.color.main_text_color_gray));
        mTabUserTv.setTextColor(getResources().getColor(R.color.main_text_color_gray));
        switch (v.getId()) {
            case R.id.online_bottom_ll:
                mTabOnlinePageTv.setTextColor(getResources().getColor(R.color.manga_reader));
                break;
            case R.id.local_bottom_ll:
                mTabLocalTv.setTextColor(getResources().getColor(R.color.manga_reader));
                break;
            case R.id.user_bottom_ll:
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        baseToast.showToast("已获得授权,请继续!");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (Configure.PERMISSION_FILE_REQUST_CODE == requestCode) {
            MangaDialog peanutDialog = new MangaDialog(MainActivity.this);
            peanutDialog.show();
            peanutDialog.setTitle("没有文件读写权限,无法更新App!可以授权后重试.");
        }
    }
}
