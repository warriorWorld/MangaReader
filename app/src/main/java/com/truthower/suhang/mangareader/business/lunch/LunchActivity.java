package com.truthower.suhang.mangareader.business.lunch;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.business.main.MainActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.utils.ActivityPoor;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.dialog.DownloadDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class LunchActivity extends BaseActivity implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks {
    private String versionName, msg;
    private int versionCode;
    private boolean forceUpdate;
    private AVFile downloadFile;
    private MangaDialog versionDialog;
    private DownloadDialog downloadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        BaseParameterUtil.getInstance(this);
        doGetVersionInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initUI() {
        hideBaseTopBar();
    }

    private void doGetVersionInfo() {
        AVQuery<AVObject> query = new AVQuery<>("VersionInfo");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (LeanCloundUtil.handleLeanResult(LunchActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        versionName = list.get(0).getString("versionName");
                        versionCode = list.get(0).getInt("versionCode");
                        forceUpdate = list.get(0).getBoolean("forceUpdate");
                        msg = list.get(0).getString("description");
                        downloadFile = list.get(0).getAVFile("apk");
                        if (BaseParameterUtil.getInstance(LunchActivity.this).
                                getAppVersionCode() >= versionCode || SharedPreferencesUtils.
                                getBooleanSharedPreferencesData(LunchActivity.this,
                                        ShareKeys.IGNORE_THIS_VERSION_KEY + versionName, false)) {
//                            baseToast.showToast("已经是最新版啦~~");
                            toNext();
                        } else {
                            showVersionDialog();
                        }
                    }
                }
            }
        });
    }

    private void toNext() {
        if (isFinishing()) {
            return;
        }
        Intent intent = new Intent(LunchActivity.this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void showVersionDialog() {
        if (null == versionDialog) {
            versionDialog = new MangaDialog(LunchActivity.this);
            versionDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                    versionDialog.dismiss();
                    doDownload();
                }

                @Override
                public void onCancelClick() {
                    if (forceUpdate) {
                        LunchActivity.this.finish();
                    } else {
                        SharedPreferencesUtils.setSharedPreferencesData(LunchActivity.this,
                                ShareKeys.IGNORE_THIS_VERSION_KEY + versionName, true);
                        baseToast.showToast("忽略后可在'我的'页中点击'版本'按钮升级至最新版!", true);
                        toNext();
                    }
                }
            });
        }
        versionDialog.show();

        versionDialog.setTitle("有新版本啦" + "v_" + versionName);
        versionDialog.setMessage(msg);
        versionDialog.setOkText("升级");
        versionDialog.setCancelable(false);

        if (!forceUpdate) {
            versionDialog.setCancelText("忽略");
        } else {
            versionDialog.setCancelText("退出");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lunch;
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
                    if (LeanCloundUtil.handleLeanResult(LunchActivity.this, e)) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
//        baseToast.showToast(getResources().getString(R.string.no_permissions), true);
        if (Configure.PERMISSION_FILE_REQUST_CODE == requestCode) {
            MangaDialog peanutDialog = new MangaDialog(LunchActivity.this);
            peanutDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                    ActivityPoor.finishAllActivity();
                }

                @Override
                public void onCancelClick() {

                }
            });
            peanutDialog.show();
            peanutDialog.setTitle("没有文件读写权限,无法更新App!可以授权后重试,或者直接去应用商店下载最新版App");
        }
    }
}
