package com.truthower.suhang.mangareader.business.threadpooldownload;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.RxDownloadBean;
import com.truthower.suhang.mangareader.bean.RxDownloadChapterBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.business.rxdownload.DownloadCaretaker;
import com.truthower.suhang.mangareader.business.rxdownload.FailedPageCaretaker;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.spider.NDownloader;
import com.truthower.suhang.mangareader.utils.Logger;
import com.truthower.suhang.mangareader.utils.PermissionUtil;
import com.truthower.suhang.mangareader.utils.ServiceUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.bar.TopBar;

import java.io.File;
import java.util.ArrayList;

public class ManageDownloadActivity extends BaseActivity implements View.OnClickListener {
    private EditText urlEt;
    private Button downloadBtn;
    private Button downloadBtn1;
    private String folderName;
    private String subFolderName;
    private String nextSubFolderName;
    private int lastChapterNum;//文件夹中最后一个文件夹的名字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!PermissionUtil.isMaster(this)) {
            finish();
        }
        initUI();
        getData();
        getFileName();
        refreshUI();
    }

    private void getData() {
        if (!TextUtils.isEmpty(SharedPreferencesUtils.getSharedPreferencesData(this, ShareKeys.STORY_FOLDER_NAME))) {
            folderName = SharedPreferencesUtils.getSharedPreferencesData(this, ShareKeys.STORY_FOLDER_NAME);
        }
    }

    private void refreshUI() {
        downloadBtn.setText("下载到当前(" + folderName + "/" + subFolderName + ")");
        downloadBtn1.setText("下载到下一个(" + folderName + "/" + nextSubFolderName + ")");
        if (!TextUtils.isEmpty(ManageDownloadCaretaker.getContentMemoto(this))) {
            urlEt.setText(ManageDownloadCaretaker.getContentMemoto(this));
        }
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2020-04-12 13:49:22 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void initUI() {
        urlEt = (EditText) findViewById(R.id.url_et);
        downloadBtn = (Button) findViewById(R.id.download_btn);
        downloadBtn1 = (Button) findViewById(R.id.download_btn1);

        downloadBtn.setOnClickListener(this);
        downloadBtn1.setOnClickListener(this);
        baseTopBar.setTitle("新建下载");
        baseTopBar.setRightText("清空");
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                urlEt.setText("");
                ManageDownloadCaretaker.clean(ManageDownloadActivity.this);
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    private void assembleDownloadBean(boolean isNext) {
        String content = urlEt.getText().toString().replaceAll(" ", "");
        urlEt.setText(content);
        if (TextUtils.isEmpty(content)) {
            return;
        }
        String mangaName;
        if (isNext) {
            //意味着该文件夹还不存在
            mangaName = folderName + "/" + nextSubFolderName;
            lastChapterNum = -1;
        } else {
            mangaName = folderName + "/" + subFolderName;
            lastChapterNum = getLastChapterNum(Configure.storagePath + "/" + mangaName);
            Logger.d("lastChapterNum: " + lastChapterNum);
        }
        DownloadCaretaker.clean(this);
        FailedPageCaretaker.clean(this);
        RxDownloadBean downloadBean = new RxDownloadBean();
        downloadBean.setDownloader(new NDownloader());
        downloadBean.setMangaName(mangaName);
        downloadBean.setThumbnailUrl("http://ww1.sinaimg.cn/mw600/00745YaMgy1gdqxtg9rqij30h40gomz1.jpg");
        ArrayList<RxDownloadChapterBean> chapters = new ArrayList<>();
        if (content.contains("\n")) {
            String[] urls = content.split("\n");
            for (int i = 0; i < urls.length; i++) {
                if (!TextUtils.isEmpty(urls[i])) {
                    RxDownloadChapterBean item = new RxDownloadChapterBean();
                    item.setChapterUrl(urls[i]);
                    item.setChapterName((lastChapterNum + i + 1) + "");
                    chapters.add(item);
                }
            }
        } else {
            RxDownloadChapterBean item = new RxDownloadChapterBean();
            item.setChapterUrl(content);
            item.setChapterName((lastChapterNum + 1) + "");
            chapters.add(item);
        }
        downloadBean.setChapters(chapters);
        downloadBean.setChapterCount(chapters.size());
        DownloadCaretaker.saveDownloadMemoto(this, downloadBean);
    }

    private void startDownload() {
        Intent serviceIntent = new Intent(this, TpDownloadService.class);
        //重新打开
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        Intent intent = new Intent(this, TpDownloadActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void getFileName() {
        try {
            String filePath = "";
            filePath = Configure.storagePath + "/" + folderName;
            File f = new File(filePath);

            File[] files = f.listFiles();
            if (null == files || files.length == 0) {
                //空文件夹
                subFolderName = folderName + "0";
                nextSubFolderName = subFolderName;
            } else {
                int[] fileNums = new int[files.length];
                String replaceString = folderName;
                for (int i = 0; i < files.length; i++) {
                    String numString = files[i].getName();
                    numString = numString.replaceAll(replaceString, "");
                    fileNums[i] = Integer.valueOf(numString);
                }
                int fileNum = getMaxNum(fileNums);

                subFolderName = replaceString + fileNum;
                nextSubFolderName = replaceString + (fileNum + 1);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private int getLastChapterNum(String folderName) {
        int res = 0;
        File f = new File(folderName);

        File[] files = f.listFiles();
        if (null == files || files.length == 0) {
            return 0;
        } else {
            int[] fileNums = new int[files.length];
            for (int i = 0; i < files.length; i++) {
                String numString = files[i].getName();
                fileNums[i] = Integer.valueOf(numString);
            }
            res = getMaxNum(fileNums);
            return res;
        }
    }

    private int getMaxNum(int[] nums) {
        int maxNum = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > maxNum) {
                maxNum = nums[i];
            }
        }
        return maxNum;
    }

    private void doDownload(boolean isNext) {
        Intent stopIntent = new Intent(this, TpDownloadService.class);
        if (ServiceUtil.isServiceWork(this,
                TpDownloadService.SERVICE_PCK_NAME)) {
            //先结束
            stopService(stopIntent);
        }
        assembleDownloadBean(isNext);
        startDownload();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!TextUtils.isEmpty(urlEt.getText().toString().replaceAll(" ", ""))) {
            ManageDownloadCaretaker.saveContentMemoto(this, urlEt.getText().toString().replaceAll(" ", ""));
        }
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2020-04-12 13:49:22 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == downloadBtn) {
            // Handle clicks for downloadBtn
            doDownload(false);
        } else if (v == downloadBtn1) {
            // Handle clicks for downloadBtn1
            doDownload(true);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_managedownload;
    }
}
