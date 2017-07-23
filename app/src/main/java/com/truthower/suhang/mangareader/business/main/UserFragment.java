package com.truthower.suhang.mangareader.business.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.widget.dialog.DownloadDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.imageview.CircleImage;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;

public class UserFragment extends BaseFragment implements View.OnClickListener {
    private RelativeLayout collectRl;
    private RelativeLayout statisticsRl;
    private RelativeLayout newWordBookRl;
    private RelativeLayout downloadRl;
    private RelativeLayout versionRl;
    private RelativeLayout chooseDirectoryRl;
    private TextView versionNameTv;
    private TextView logoutTv;
    private RelativeLayout userTopBarRl;
    private TextView userNameTv;
    private TextView choosedDirectoryTv;
    private CircleImage userHeadCiv;
    private boolean isHidden = true;

    private MangaDialog versionDialog;
    private DownloadDialog downloadDialog;

    private String versionName, msg, url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        initUI(v);
        refreshUI();
        toggleLoginStateUI();
        return v;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHidden = hidden;
        if (!isHidden) {
            toggleLoginStateUI();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden) {
            toggleLoginStateUI();
        }
    }

    private void initUI(View v) {
        chooseDirectoryRl = (RelativeLayout) v.findViewById(R.id.choose_directory_rl);
        choosedDirectoryTv = (TextView) v.findViewById(R.id.directory_name_tv);
        collectRl = (RelativeLayout) v.findViewById(R.id.collect_rl);
        statisticsRl = (RelativeLayout) v.findViewById(R.id.statistics_rl);
        newWordBookRl = (RelativeLayout) v.findViewById(R.id.new_word_book_rl);
        downloadRl = (RelativeLayout) v.findViewById(R.id.download_rl);
        versionRl = (RelativeLayout) v.findViewById(R.id.version_rl);
        versionNameTv = (TextView) v.findViewById(R.id.version_name_tv);
        logoutTv = (TextView) v.findViewById(R.id.logout_tv);
        userHeadCiv = (CircleImage) v.findViewById(R.id.user_head_civ);
        userTopBarRl = (RelativeLayout) v.findViewById(R.id.user_top_bar_rl);
        userNameTv = (TextView) v.findViewById(R.id.user_name_tv);

        collectRl.setOnClickListener(this);
        statisticsRl.setOnClickListener(this);
        newWordBookRl.setOnClickListener(this);
        downloadRl.setOnClickListener(this);
        versionRl.setOnClickListener(this);
        logoutTv.setOnClickListener(this);
        userTopBarRl.setOnClickListener(this);
        chooseDirectoryRl.setOnClickListener(this);
    }

    private void refreshUI() {
        versionNameTv.setText("V" + Configure.versionName);
    }

    private void toggleLoginStateUI() {
        if (true) {
            userNameTv.setText("点击登录");
            userTopBarRl.setEnabled(true);
        } else {
//            userNameTv.setText(LoginBean.getInstance().getMobile());
            userTopBarRl.setEnabled(false);
        }
    }


    private void doLogout() {
        toggleLoginStateUI();
    }

    private void doGetVersionInfo() {
    }

    private void showVersionDialog(final boolean alreadyNewest) {
        if (null == versionDialog) {
            versionDialog = new MangaDialog(getActivity());
            versionDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                    if (!TextUtils.isEmpty(url) && !alreadyNewest) {
                        versionDialog.dismiss();
                        doDownload();
                    }
                }

                @Override
                public void onCancelClick() {
                }
            });
        }
        versionDialog.show();
        if (alreadyNewest) {
            versionDialog.setTitle("已经是最新版" + "v_" + versionName);
        } else {
            versionDialog.setTitle("有新版本啦" + "v_" + versionName);
        }

        versionDialog.setMessage(msg);
        if (alreadyNewest) {
            versionDialog.setOkText("确定");
        } else {
            versionDialog.setOkText("升级");
        }
        if (!alreadyNewest) {
            versionDialog.setCancelText("取消");
        }
        versionDialog.setCancelable(true);
    }

    private void doDownload() {
        showDownLoadDialog();
        // 下载apk，自动安装
        FinalHttp client = new FinalHttp();
        // url:下载的地址
        // target:保存的地址，包含文件的名称
        // callback 下载时的回调对象
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/manga/apk";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            client.download(url, filePath + "/manga_reader.apk",
                    new AjaxCallBack<File>() {

                        // 下载失败时回调这个方法
                        @Override
                        public void onFailure(Throwable t, String strMsg) {
                            super.onFailure(t, strMsg);
                            if (null != downloadDialog && downloadDialog.isShowing()) {
                                downloadDialog.dismiss();
                            }
                            baseToast.showToast("请检查你的网络");
                        }

                        // 下载时回调这个方法
                        // count ：下载文件需要的总时间，单位是毫秒
                        // current :当前进度,单位是毫秒
                        @Override
                        public void onLoading(long count, long current) {
                            super.onLoading(count, current);
                            String progress = current * 100 / count + "";
                            Integer integer = Integer.parseInt(progress);
                            downloadDialog.setProgress(integer);
                        }

                        // 下载成功时回调这个方法
                        @Override
                        public void onSuccess(File t) {
                            super.onSuccess(t);
                            // 开始使其显示。
                            if (null != downloadDialog && downloadDialog.isShowing()) {
                                downloadDialog.dismiss();
                            }
                            baseToast.showToast("下载成功,文件保存在" + t.getPath());
                            Intent intent = new Intent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction("android.intent.action.VIEW");
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
                            startActivity(intent);
                        }
                    });
        }
    }

    private void showDownLoadDialog() {
        if (null == downloadDialog) {
            downloadDialog = new DownloadDialog(getActivity());
        }
        downloadDialog.show();
        downloadDialog.setCancelable(false);
    }

    public void refreshDirctoryPath() {
        choosedDirectoryTv.setText(Configure.DST_FOLDER_NAME);
    }

    /**
     * 调用文件选择软件来选择文件
     **/
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");//设置类型和后缀
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.collect_rl:
                break;
            case R.id.statistics_rl:
                break;
            case R.id.new_word_book_rl:
                break;
            case R.id.download_rl:
                break;
            case R.id.version_rl:
                doGetVersionInfo();
                break;
            case R.id.logout_tv:
                doLogout();
                break;
            case R.id.user_top_bar_rl:
                break;
            case R.id.choose_directory_rl:
                showFileChooser();
                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }
}
