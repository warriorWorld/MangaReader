package com.truthower.suhang.mangareader.business.threadpooldownload;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.utils.PermissionUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.bar.TopBar;

import java.io.File;

public class ManageDownloadActivity extends BaseActivity implements View.OnClickListener {
    private EditText urlEt;
    private Button downloadBtn;
    private Button downloadBtn1;
    private String folderName;
    private String subFolderName;
    private String nextSubFolderName;

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
        downloadBtn.setText("下载到" + folderName + "/" + subFolderName);
        downloadBtn1.setText("下载到" + folderName + "/" + nextSubFolderName);
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
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    private void assembleDownloadBean() {
//        String content = urlEt.getText().toString().replaceAll(" ", "");
//        urlEt.setText(content);
//        if (TextUtils.isEmpty(content)) {
//            return;
//        }
//        DownloadCaretaker.clean(this);
//        RxDownloadBean downloadBean = new RxDownloadBean();
//        downloadBean.setDownloader(new NDownloader());
//        downloadBean.setMangaName(currentManga.getName());
//        downloadBean.setMangaUrl(currentManga.getUrl());
//        downloadBean.setThumbnailUrl(currentManga.getWebThumbnailUrl());
//        downloadBean.setChapterCount(end - start + 1);
//        ArrayList<RxDownloadChapterBean> chapters = new ArrayList<>();
//        for (int i = start; i <= end; i++) {
//            RxDownloadChapterBean item = new RxDownloadChapterBean();
//            item.setChapterUrl(currentManga.getChapters().get(i).getChapterUrl());
//            item.setChapterName((i + 1) + "");
//            chapters.add(item);
//        }
//        downloadBean.setChapters(chapters);
//        DownloadCaretaker.saveDownloadMemoto(this, downloadBean);
//
//        if (content.contains("\n")) {
//            String[] urls = content.split("\n");
//
//        } else {
//
//        }
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

    private int getMaxNum(int[] nums) {
        int maxNum = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] > maxNum) {
                maxNum = nums[i];
            }
        }
        return maxNum;
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
        } else if (v == downloadBtn1) {
            // Handle clicks for downloadBtn1
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_managedownload;
    }
}
