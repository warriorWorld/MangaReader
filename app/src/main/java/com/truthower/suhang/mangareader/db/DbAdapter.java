package com.truthower.suhang.mangareader.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.truthower.suhang.mangareader.bean.WordsBookBean;
import com.truthower.suhang.mangareader.config.Configure;

import java.util.ArrayList;


public class DbAdapter {
    public static final String DB_NAME = "books.db";
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    public DbAdapter(Context context) {
        dbHelper = new DbHelper(context, DB_NAME, null, Configure.DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 插入一条生词信息
     */
    public void insertWordsBookTb(String word, String examplePath) {
        int time = queryQueryedTime(word);
        if (time > 0) {
            //如果查过这个单词 那就update 并且time+1
            time++;
            updateTimeTOWordsBook(word, examplePath, time);
        } else {
            db.execSQL(
                    "insert into WordsBook (word,example_path,time) values (?,?,?)",
                    new Object[]{word, examplePath, 1});
        }
    }

    /**
     * 更新生词信息
     */
    public void updateTimeTOWordsBook(String word, String examplePath, int time) {
        db.execSQL("update WordsBook set time=? where word=?",
                new Object[]{time, word});
        db.execSQL("update WordsBook set example_path=? where word=?",
                new Object[]{time, examplePath});
    }

    /**
     * 查询所有生词
     *
     * @return
     */
    public ArrayList<WordsBookBean> queryAllWordsBook() {
        ArrayList<WordsBookBean> resBeans = new ArrayList<WordsBookBean>();
        Cursor cursor = db
                .query("WordsBook", null, null, null, null, null, "createdtime desc");

        while (cursor.moveToNext()) {
            String word = cursor.getString(cursor.getColumnIndex("word"));
            int time = cursor
                    .getInt(cursor.getColumnIndex("time"));
            String examplePath = cursor.getString(cursor.getColumnIndex("example_path"));
            WordsBookBean item = new WordsBookBean();
            item.setWord(word);
            item.setTime(time);
            item.setExample_path(examplePath);
            resBeans.add(item);
        }
        cursor.close();
        return resBeans;
    }

    /**
     * 查询是否查询过
     */
    public boolean queryQueryed(String word) {
        Cursor cursor = db.rawQuery(
                "select word from WordsBook where word=?",
                new String[]{word});
        int count = cursor.getCount();
        cursor.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public int queryQueryedTime(String word) {
        int res = 0;
        Cursor cursor = db.rawQuery(
                "select time from WordsBook where word=?",
                new String[]{word});
        int count = cursor.getCount();
        if (count > 0) {
            while (cursor.moveToNext()) {
                res = cursor.getInt(cursor.getColumnIndex("time"));
            }
        }
        cursor.close();
        return res;
    }

    /**
     * 删除生词
     */
    public void deleteWordByWord(String word) {
        db.execSQL("delete from WordsBook where word=?",
                new Object[]{word});
    }

    /**
     * 根据书籍名称删除统计数据
     */
    public void deleteStatiscticsByBookName(String manga_name) {
        db.execSQL("delete from STATISTICS where manga_name=?",
                new Object[]{manga_name});
    }

    public void closeDb() {
        if (null != db) {
            db.close();
        }
    }
}
