package com.truthower.suhang.mangareader.business.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.business.collect.CollectedActivity;
import com.truthower.suhang.mangareader.business.download.DownloadActivity;
import com.truthower.suhang.mangareader.business.other.AboutActivity;
import com.truthower.suhang.mangareader.business.rxdownload.RxDownloadActivity;
import com.truthower.suhang.mangareader.business.words.WordsActivity;
import com.truthower.suhang.mangareader.business.wordsbook.WordsBookActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.utils.NumberUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.dialog.DownloadDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaImgDialog;
import com.truthower.suhang.mangareader.widget.dialog.ShareDialog;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class UserFragment extends BaseFragment implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks {
    private RelativeLayout newWordBookRl;
    private RelativeLayout downloadRl;
    private RelativeLayout aboutRl;
    private RelativeLayout userTopBarRl;
    private TextView dayTv;
    private TextView monthTv;
    private TextView dayOfWeekTv;
    private int currentMonth, currentDay;
    private String currentDayOfWeek;
    private RelativeLayout collectRl;
    private RelativeLayout shareRl;
    private ClipboardManager clip;//复制文本用
    private TextView qqTv;
    private DownloadDialog downloadDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        clip = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("EEEE");

        currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        currentDayOfWeek = format.format(date);
        initUI(v);
        refreshUI();
        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(getActivity(), ShareKeys.CLOSE_TUTORIAL, true)) {
            MangaDialog dialog = new MangaDialog(getActivity());
            dialog.show();
            dialog.setTitle("教程");
            dialog.setMessage("想要更新App可以进入设置页检查更新下载最新App" +
                    "\n设置页可以关闭本教程");
        }
        return v;
    }

    private void initUI(View v) {
        newWordBookRl = (RelativeLayout) v.findViewById(R.id.new_word_book_rl);
        downloadRl = (RelativeLayout) v.findViewById(R.id.download_rl);
        userTopBarRl = (RelativeLayout) v.findViewById(R.id.user_top_bar_rl);
        aboutRl = (RelativeLayout) v.findViewById(R.id.about_rl);
        dayTv = (TextView) v.findViewById(R.id.day_tv);
        monthTv = (TextView) v.findViewById(R.id.month_tv);
        dayOfWeekTv = (TextView) v.findViewById(R.id.day_of_week_tv);
        collectRl = (RelativeLayout) v.findViewById(R.id.collect_rl);
        shareRl = (RelativeLayout) v.findViewById(R.id.share_rl);
        qqTv = (TextView) v.findViewById(R.id.qq_tv);
        qqTv.setText
                ("获取最新版App，请加qq：" + Configure.QQ + "（点击群号可复制），或直接点击下载最新App。",
                        TextView.BufferType.SPANNABLE);
        qqTv.setMovementMethod(LinkMovementMethod.getInstance());
        qqTv.setHighlightColor(getResources().getColor(R.color.transparency));
        Spannable spannable = (Spannable) qqTv.getText();
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.main_text_color_gray));
//                ds.setUnderlineText(true);
            }
        }, 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//0开始
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                baseToast.showToast("已复制QQ号！");
                clip.setText(Configure.QQ);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.main_text_color_blue));
                ds.setUnderlineText(true);
            }
        }, 14, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.main_text_color_gray));
                ds.setUnderlineText(false);
            }
        }, 23, 36, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                showVersionDialog();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.main_text_color_blue));
                ds.setUnderlineText(true);
            }
        }, 36, 40, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.main_text_color_gray));
                ds.setUnderlineText(false);
            }
        }, 40, 46, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        collectRl.setOnClickListener(this);
        shareRl.setOnClickListener(this);
        newWordBookRl.setOnClickListener(this);
        downloadRl.setOnClickListener(this);
        userTopBarRl.setOnClickListener(this);
        aboutRl.setOnClickListener(this);
    }

    private void showVersionDialog() {
        MangaDialog versionDialog = new MangaDialog(getActivity());
        versionDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                doDownload();
            }

            @Override
            public void onCancelClick() {
            }
        });
        versionDialog.show();

        versionDialog.setTitle("下载最新安装包");
        versionDialog.setMessage("由于云服务已暂停，所以无法获取最新版本号，但是您可以直接下载最新版本，如果最新版本高于当前已安装版本可直接覆盖安装。低于或等于当前版本无影响。\nPS:如果下载失败，请打开VPN后再试。");
        versionDialog.setOkText("下载");
        versionDialog.setCancelable(true);
        versionDialog.setCancelText("取消");
    }

    private void showDownLoadDialog() {
        if (null == downloadDialog) {
            downloadDialog = new DownloadDialog(getActivity());
        }
        downloadDialog.show();
        downloadDialog.setCancelable(false);
    }

    @AfterPermissionGranted(111)
    private void doDownload() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            // Already have permission, do the thing
            // ...
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
                client.download(Configure.DOWNLOAD_URL, filePath + "/manga.apk",
                        new AjaxCallBack<File>() {

                            // 下载失败时回调这个方法
                            @Override
                            public void onFailure(Throwable t, String strMsg) {
                                super.onFailure(t, strMsg);
                                if (null != downloadDialog && downloadDialog.isShowing()) {
                                    downloadDialog.dismiss();
                                }
                                baseToast.showToast("下载失败");
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
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    111, perms);
        }
    }

    private void refreshUI() {
        dayTv.setText(NumberUtil.toDoubleNum(currentDay));
        monthTv.setText(currentMonth + "月");
        dayOfWeekTv.setText(currentDayOfWeek);
    }

    private void showOrderSelectDialog() {
        MangaDialog dialog = new MangaDialog(getActivity());
        dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                Intent intent = new Intent(getActivity(), WordsBookActivity.class);
                intent.putExtra("orderType", "random");
                startActivity(intent);
            }

            @Override
            public void onCancelClick() {
                Intent intent = new Intent(getActivity(), WordsBookActivity.class);
                intent.putExtra("orderType", "order");
                startActivity(intent);
            }
        });
        dialog.show();
        dialog.setTitle("请选择生词展示顺序");
        dialog.setOkText("随机");
        dialog.setCancelText("顺序");
    }

    private void showShareDialog() {
        ShareDialog dialog = new ShareDialog(getActivity());
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.new_word_book_rl:
                intent = new Intent(getActivity(), WordsActivity.class);
                break;
            case R.id.download_rl:
                intent = new Intent(getActivity(), RxDownloadActivity.class);
                break;
            case R.id.about_rl:
                intent = new Intent(getActivity(), AboutActivity.class);
                break;
            case R.id.collect_rl:
                intent = new Intent(getActivity(), CollectedActivity.class);
                break;
            case R.id.share_rl:
                showShareDialog();
                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
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
            MangaDialog peanutDialog = new MangaDialog(getActivity());
            peanutDialog.show();
            peanutDialog.setTitle("没有文件读写权限,无法更新App!可以授权后重试!");
        }
    }
}
