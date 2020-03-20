package com.truthower.suhang.mangareader.test;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.WordsBookBean;
import com.truthower.suhang.mangareader.db.DbAdapter;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;

import java.util.ArrayList;

public class TestActivity extends BaseActivity implements View.OnClickListener {
    private Button testBtn, testBtn1;
    private MangaDialog mDialog;
    private String toastOut = "";
    private DbAdapter db;//数据库

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        db = new DbAdapter(this);
    }

    private void initView() {
        testBtn = (Button) findViewById(R.id.test_btn);
        testBtn.setOnClickListener(this);
        testBtn1 = (Button) findViewById(R.id.test_btn1);
        testBtn1.setOnClickListener(this);
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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeDb();
    }
}
