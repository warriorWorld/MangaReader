package com.truthower.suhang.mangareader.business.other;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.LoginBean;
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
import com.truthower.suhang.mangareader.widget.popupwindow.EasyPopupWindow;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class AboutActivity extends BaseActivity implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks {
    private ImageView appIconIv;
    private TextView versionTv;
    private RelativeLayout checkUpdateRl;
    private RelativeLayout authorRl;
    private RelativeLayout feedbackRl;
    private RelativeLayout keyboardRl;
    private TextView logoutTv;

    private String versionName, msg;
    private int versionCode;
    private boolean forceUpdate;
    private AVFile downloadFile;
    private MangaDialog versionDialog;
    private DownloadDialog downloadDialog;
    private CheckBox closeTranslateCb, economyModeCb, closeTutorialCb;
    private CheckBox closeTtsCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        doGetVersionInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void refreshUI() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
            logoutTv.setVisibility(View.GONE);
        } else {
            logoutTv.setVisibility(View.VISIBLE);
        }
    }

    private void initUI() {
        appIconIv = (ImageView) findViewById(R.id.app_icon_iv);
        versionTv = (TextView) findViewById(R.id.version_tv);
        checkUpdateRl = (RelativeLayout) findViewById(R.id.check_update_rl);
        authorRl = (RelativeLayout) findViewById(R.id.author_rl);
        feedbackRl = (RelativeLayout) findViewById(R.id.feedback_rl);
        logoutTv = (TextView) findViewById(R.id.logout_tv);
        economyModeCb = (CheckBox) findViewById(R.id.economy_mode_cb);
        economyModeCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.ECONOMY_MODE, isChecked);
            }
        });
        closeTranslateCb = (CheckBox) findViewById(R.id.close_translate_cb);
        closeTranslateCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.CLOSE_TRANSLATE, isChecked);
            }
        });
        closeTutorialCb = (CheckBox) findViewById(R.id.close_tutorial_cb);
        closeTutorialCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.CLOSE_TUTORIAL, isChecked);
            }
        });
        closeTtsCb = (CheckBox) findViewById(R.id.close_tts_cb);
        closeTtsCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.CLOSE_TTS, isChecked);
            }
        });
        keyboardRl= (RelativeLayout) findViewById(R.id.keyboard_rl);
        closeTranslateCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(AboutActivity.this,
                        ShareKeys.CLOSE_TRANSLATE, false));
        closeTutorialCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(AboutActivity.this,
                        ShareKeys.CLOSE_TUTORIAL, true));
        economyModeCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(AboutActivity.this,
                        ShareKeys.ECONOMY_MODE, false));
        closeTtsCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(AboutActivity.this,
                        ShareKeys.CLOSE_TTS, false));
        keyboardRl.setOnClickListener(this);
        appIconIv.setOnClickListener(this);
        checkUpdateRl.setOnClickListener(this);
        authorRl.setOnClickListener(this);
        feedbackRl.setOnClickListener(this);
        logoutTv.setOnClickListener(this);
        baseTopBar.setTitle("设置");
    }

    private void doGetVersionInfo() {
        AVQuery<AVObject> query = new AVQuery<>("VersionInfo");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (LeanCloundUtil.handleLeanResult(AboutActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        versionName = list.get(0).getString("versionName");
                        versionCode = list.get(0).getInt("versionCode");
                        forceUpdate = list.get(0).getBoolean("forceUpdate");
                        msg = list.get(0).getString("description");
                        downloadFile = list.get(0).getAVFile("apk");
                        if (versionCode == BaseParameterUtil.getInstance().getAppVersionCode(AboutActivity.this)) {
                            versionTv.setText(BaseParameterUtil.getInstance().getAppVersionName(AboutActivity.this)
                                    + "(最新版本)");
                        } else {
                            versionTv.setText(BaseParameterUtil.getInstance().getAppVersionName(AboutActivity.this)
                                    + "(有新版本啦~)");
                            showVersionDialog();
                        }
                    }
                }
            }
        });
    }

    private void showVersionDialog() {
        if (null == versionDialog) {
            versionDialog = new MangaDialog(AboutActivity.this);
            versionDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                    versionDialog.dismiss();
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
        versionDialog.setCancelable(true);

        if (!forceUpdate) {
            versionDialog.setCancelText("忽略");
        } else {
            versionDialog.setCancelText("退出");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
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
                    if (LeanCloundUtil.handleLeanResult(AboutActivity.this, e)) {
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

    private void doLogout() {
        AVUser.getCurrentUser().logOut();
        LoginBean.getInstance().clean(this);
        refreshUI();
        baseToast.showToast("退出成功!");
    }

    private void showLogoutDialog() {
        MangaDialog dialog = new MangaDialog(this);
        dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                doLogout();
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setTitle("是否退出登录?");
        dialog.setOkText("是");
        dialog.setCancelText("否");
    }

    private void showAuthorDialog() {
        MangaDialog authorDialog = new MangaDialog(this);
        authorDialog.show();
        authorDialog.setTitle("联系作者");
        authorDialog.setOkText("知道了");
        authorDialog.setMessage("作者:  苏航\n邮箱:  772192594@qq.com");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_icon_iv:
                break;
            case R.id.check_update_rl:
                if (versionCode == BaseParameterUtil.getInstance().getAppVersionCode(AboutActivity.this)) {
                    baseToast.showToast("已经是最新版本啦~");
                } else {
                    showVersionDialog();
                }
                break;
            case R.id.author_rl:
                showAuthorDialog();
                break;
            case R.id.feedback_rl:
                Intent intent = new Intent(AboutActivity.this, FeedbackActivity.class);
                startActivity(intent);
                break;
            case R.id.logout_tv:
                showLogoutDialog();
                break;
            case R.id.keyboard_rl:
                Intent intent1 = new Intent(AboutActivity.this, KeyboardSettingActivity.class);
                startActivity(intent1);
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
//        baseToast.showToast(getResources().getString(R.string.no_permissions), true);
        if (Configure.PERMISSION_FILE_REQUST_CODE == requestCode) {
            MangaDialog peanutDialog = new MangaDialog(AboutActivity.this);
            peanutDialog.show();
            peanutDialog.setTitle("没有文件读写权限,无法更新App!可以授权后重试,或者直接去应用商店下载最新版App");
        }
    }
}
