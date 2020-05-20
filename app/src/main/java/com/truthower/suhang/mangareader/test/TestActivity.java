package com.truthower.suhang.mangareader.test;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.RxDownloadChapterBean;
import com.truthower.suhang.mangareader.bean.WordsBookBean;
import com.truthower.suhang.mangareader.db.DbAdapter;
import com.truthower.suhang.mangareader.utils.Logger;
import com.truthower.suhang.mangareader.utils.SerializableSparseArray;
import com.truthower.suhang.mangareader.utils.ShareObjUtil;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestActivity extends BaseActivity implements View.OnClickListener {
    private Button testBtn, testBtn1, testBtn2;
    private MangaDialog mDialog;
    private String toastOut = "";
    private DbAdapter db;//数据库
    private int cpuCount = Runtime.getRuntime().availableProcessors();
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(cpuCount);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        db = new DbAdapter(this);
        Logger.setTag("TestActivity");
    }

    private void initView() {
        testBtn = (Button) findViewById(R.id.test_btn);
        testBtn.setOnClickListener(this);
        testBtn1 = (Button) findViewById(R.id.test_btn1);
        testBtn1.setOnClickListener(this);
        testBtn2 = findViewById(R.id.test_btn2);
        testBtn2.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    private void test(final String title, String message, final String toast) {
        if (null == mDialog) {
            mDialog = new MangaDialog(TestActivity.this);
            mDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                    baseToast.showToast(toast + "\n" + toastOut);
                }

                @Override
                public void onCancelClick() {

                }
            });
        }
        mDialog.show();
        mDialog.setTitle(title);
        mDialog.setMessage(message);
    }

    private void test2() {
        Logger.d(cpuCount + " CPU");
        for (int i = 0; i < 30; i++) {
            mExecutorService.execute(new TestRunner());
            Logger.d("for :" + i);
        }
        Logger.d("test done");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.test_btn:
                toastOut = "btn";
                test("btn", "btn", "btn");
                break;
            case R.id.test_btn1:
                //创建期待匹配的uri
                Uri uri1 = Uri.parse("content://com.insightsurfface.myword/WordsBook");

                ArrayList<WordsBookBean> words = db.queryAllWordsBook();

                for (int i = 0; i < words.size(); i++) {
                    WordsBookBean item = words.get(i);
                    ContentValues values = new ContentValues();
                    values.put("word", item.getWord());
                    values.put("time", item.getTime());
                    values.put("example_path", item.getExample_path());
                    values.put("update_time", item.getUpdate_time());
                    values.put("kill_time", item.getKill_time());
                    //获得ContentResolver对象，调用方法
                    getContentResolver().insert(uri1, values);
                }

                test("btn", "btn", "btn");
                break;
            case R.id.test_btn2:
                test2();
                SerializableSparseArray<RxDownloadChapterBean> test = new SerializableSparseArray();
                test.put(3, new RxDownloadChapterBean());
                ShareObjUtil.saveObject(TestActivity.this, test, "test");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeDb();
    }
}
