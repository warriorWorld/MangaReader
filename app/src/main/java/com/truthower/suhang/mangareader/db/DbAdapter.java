package com.truthower.suhang.mangareader.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.truthower.suhang.mangareader.bean.MangaBean;
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

    public void insertCollect(String name, String url, String webThumbnailUrl) {
        if (!queryCollectExist(url)) {
            db.execSQL(
                    "insert into CollectBook (name,mangaUrl,webThumbnailUrl) values (?,?,?)",
                    new Object[]{name, url, webThumbnailUrl});
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

    public void updateCollectWebThumbilUrl(String url, String webThumbnailUrl) {
        db.execSQL("update CollectBook set webThumbnailUrl=? where mangaUrl=?",
                new Object[]{webThumbnailUrl, url});
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
        long currentTime = System.currentTimeMillis();
        int minGapTime = 6 * 60 * 60 * 1000;
        long minTime = currentTime - minGapTime;

        while (cursor.moveToNext()) {
            String word = cursor.getString(cursor.getColumnIndex("word"));
            int time = cursor
                    .getInt(cursor.getColumnIndex("time"));
            String examplePath = cursor.getString(cursor.getColumnIndex("example_path"));
            long lastKillTime = 0;
            try {
                lastKillTime = cursor.getLong(cursor.getColumnIndex("update_time"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            int killTime = 0;
            try {
                killTime = cursor.getInt(cursor.getColumnIndex("kill_time"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (killTime > 0 && minTime < lastKillTime) {
                //不显示 kill过的并且时间未超过时长的
            } else {
                WordsBookBean item = new WordsBookBean();
                item.setWord(word);
                item.setTime(time);
                item.setExample_path(examplePath);
                item.setUpdate_time(lastKillTime);
                item.setKill_time(killTime);
                resBeans.add(item);
            }
        }
        cursor.close();
        return resBeans;
    }

    public ArrayList<MangaBean> queryAllCollect() {
        ArrayList<MangaBean> resBeans = new ArrayList<MangaBean>();
        Cursor cursor = db
                .query("CollectBook", null, null, null, null, null, "createdtime asc");

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String webThumbnailUrl = cursor.getString(cursor.getColumnIndex("webThumbnailUrl"));
            String mangaUrl = cursor.getString(cursor.getColumnIndex("mangaUrl"));
            MangaBean item = new MangaBean();
            item.setName(name);
            item.setWebThumbnailUrl(webThumbnailUrl);
            item.setUrl(mangaUrl);
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

    /**
     * 查询是否查询过
     */
    public boolean queryCollectExist(String url) {
        Cursor cursor = db.rawQuery(
                "select mangaUrl from CollectBook where mangaUrl=?",
                new String[]{url});
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

    public int queryKilledTime(String word) {
        int res = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "select kill_time from WordsBook where word=?",
                    new String[]{word});
            int count = cursor.getCount();
            if (count > 0) {
                while (cursor.moveToNext()) {
                    res = cursor.getInt(cursor.getColumnIndex("kill_time"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return res;
    }

    public void killWordByWord(String word) {
        int time = queryKilledTime(word);
        time++;
        if (time >= 3) {
            deleteWordByWord(word);
        } else {
            db.execSQL("update WordsBook set kill_time=?,update_time=? where word=?",
                    new Object[]{time,System.currentTimeMillis(), word});
        }
    }

    /**
     * 删除生词
     */
    public void deleteWordByWord(String word) {
        db.execSQL("delete from WordsBook where word=?",
                new Object[]{word});
    }

    public void deleteCollect(String url) {
        db.execSQL("delete from CollectBook where mangaUrl=?",
                new Object[]{url});
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
