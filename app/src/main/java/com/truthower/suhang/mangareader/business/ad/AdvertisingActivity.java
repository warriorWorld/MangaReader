package com.truthower.suhang.mangareader.business.ad;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.AdListRecyclerListAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.AdBean;
import com.truthower.suhang.mangareader.business.user.LoginActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.utils.ActivityPoor;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.widget.dialog.DownloadDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.dialog.SingleLoadBarUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class AdvertisingActivity extends BaseActivity implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks {
    private RecyclerView adListRcv;
    private View emptyView;
    private TextView emptyTv;
    //版本更新
    private AVFile downloadFile;
    private DownloadDialog downloadDialog;
    private AdListRecyclerListAdapter adapter;
    private ArrayList<AdBean> adList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        doGetData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initUI() {
        adListRcv = (RecyclerView) findViewById(R.id.ad_list_rcv);
        adListRcv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        adListRcv.setFocusableInTouchMode(false);
        adListRcv.setFocusable(false);
        adListRcv.setHasFixedSize(true);

        emptyView = findViewById(R.id.empty_view);
        emptyTv = (TextView) findViewById(R.id.empty_text);
        emptyTv.setText("空的啊");
        baseTopBar.setTitle("学英语App家族");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_advertising;
    }

    private void initDateRv() {
        try {
            if (null == adList || adList.size() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
            if (null == adapter) {
                adapter = new AdListRecyclerListAdapter(this, adList);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        showAppDialog(position);
                    }
                });
                adListRcv.setAdapter(adapter);
            } else {
                adapter.setList(adList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void doGetData() {
        SingleLoadBarUtil.getInstance().showLoadBar(AdvertisingActivity.this);
        AVQuery<AVObject> query = new AVQuery<>("AD");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(AdvertisingActivity.this, e)) {
                    adList = new ArrayList<>();
                    if (null != list && list.size() > 0) {
                        AdBean item;
                        for (int i = 0; i < list.size(); i++) {
                            item = new AdBean();
                            item.setTitle(list.get(i).getString("title"));
                            item.setSubtitle(list.get(i).getString("subTitle"));
                            item.setMessage(list.get(i).getString("describe"));
                            item.setAdFile(list.get(i).getAVFile("apk"));
                            adList.add(item);
                        }
                    }
                    initDateRv();
                }
            }
        });
    }

    @AfterPermissionGranted(111)
    private void doDownload() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            showDownLoadDialog();
            final String filePath = Configure.DOWNLOAD_PATH + "/apk";
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
                    if (LeanCloundUtil.handleLeanResult(AdvertisingActivity.this, e)) {
                        File apkFile = FileSpider.getInstance().byte2File(bytes, filePath, "english_ad.apk");

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
                    111, perms);
        }
    }


    private void showAppDialog(final int position) {
        MangaDialog versionDialog = new MangaDialog(AdvertisingActivity.this);
        versionDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                downloadFile = adList.get(position).getAdFile();
                doDownload();
            }

            @Override
            public void onCancelClick() {
            }
        });
        versionDialog.show();

        versionDialog.setTitle(adList.get(position).getTitle());
        versionDialog.setMessage(adList.get(position).getMessage());
        versionDialog.setOkText("马上下载");
        versionDialog.setCancelable(true);
        versionDialog.setCancelText("稍后再说");
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
        Intent intent = null;
        switch (v.getId()) {
        }
        if (null != intent) {
            startActivity(intent);
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
        if (111 == requestCode) {
            MangaDialog peanutDialog = new MangaDialog(AdvertisingActivity.this);
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
            peanutDialog.setTitle("没有文件读写权限,无法更新App!可以授权后重试!");
        }
    }
}
