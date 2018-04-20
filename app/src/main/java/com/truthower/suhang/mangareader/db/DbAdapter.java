package com.truthower.suhang.mangareader.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.truthower.suhang.mangareader.bean.StatisticsBean;
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
    public void insertWordsBookTb(String word) {
        int time = queryQueryedTime(word);
        if (time > 0) {
            //如果查过这个单词 那就update 并且time+1
            time++;
            updateTimeTOWordsBook(word, time);
        } else {
            db.execSQL(
                    "insert into WordsBook (word,time) values (?,?)",
                    new Object[]{word, 1});
        }
    }

    /**
     * 更新生词信息
     */
    public void updateTimeTOWordsBook(String word, int time) {
        db.execSQL("update WordsBook set time=? where word=?",
                new Object[]{time, word});
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
            WordsBookBean item = new WordsBookBean();
            item.setWord(word);
            item.setTime(time);
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
     * 插入一条统计信息
     */
    private void insertStatiscticsTb(int query_word_c, int read_page, String manga_name) {
//        if (queryStatisticsed(book_name)) {
//            updateStatistics(dateEnd, query_word_c, book_name);
//        } else {
        db.execSQL(
                "insert into STATISTICS (query_word_c,read_page,manga_name) values (?,?,?)",
                new Object[]{query_word_c, read_page, manga_name});
//        }
    }

    /**
     * 更新统计信息
     */
    public void updateStatistics(int query_word_c, int read_page, String manga_name) {
        //初始记录
        if (isStatisticsed(manga_name)) {
            insertStatiscticsTb(query_word_c, read_page, manga_name);
        } else {
            db.execSQL("update STATISTICS set query_word_c=?,read_page=? where manga_name=?",
                    new Object[]{query_word_c, read_page, manga_name});
        }
    }

    /**
     * 查询统计数据是否已经添加过
     */
    public boolean isStatisticsed(String manga_name) {
        Cursor cursor = db.rawQuery(
                "select * from STATISTICS where manga_name=?",
                new String[]{manga_name});
        int count = cursor.getCount();
        cursor.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public StatisticsBean queryStatisticsByBookName(String manga_name) {
        Cursor cursor = db.rawQuery(
                "select * from STATISTICS where manga_name=?",
                new String[]{manga_name});
        StatisticsBean item = new StatisticsBean();
        int count = cursor.getCount();
        if (count > 0) {
            while (cursor.moveToNext()) {
                int query_word_c = cursor.getInt(cursor.getColumnIndex("query_word_c"));
                int read_page = cursor.getInt(cursor.getColumnIndex("read_page"));
                item.setManga_name(manga_name);
                item.setQuery_word_c(query_word_c);
                item.setRead_page(read_page);
            }
        }
        cursor.close();
        return item;
    }

    /**
     * 查询统计数据
     */
    public ArrayList<StatisticsBean> queryAllStatistic() {
        ArrayList<StatisticsBean> list = new ArrayList<StatisticsBean>();
        Cursor cursor = db
                .query("STATISTICS", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            StatisticsBean item = new StatisticsBean();
            String manga_name = cursor.getString(cursor
                    .getColumnIndex("manga_name"));
            int query_word_c = cursor.getInt(cursor.getColumnIndex("query_word_c"));
            int read_page = cursor.getInt(cursor.getColumnIndex("read_page"));
            item.setManga_name(manga_name);
            item.setQuery_word_c(query_word_c);
            item.setRead_page(read_page);
            list.add(item);
        }
        cursor.close();
        return list;
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
