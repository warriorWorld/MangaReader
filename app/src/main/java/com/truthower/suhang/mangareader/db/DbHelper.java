package com.truthower.suhang.mangareader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private Context xcontext;
    public static final String DATA_STATISTICS = "create table if not exists STATISTICS ("
            + "id integer primary key autoincrement,"
            + "query_word_c integer," + "read_page integer,"
            + "manga_name text)";
    public static final String WORDS_BOOK = "create table if not exists WordsBook ("
            + "id integer primary key autoincrement,"
            + "word text," + "time integer," + "createdtime TimeStamp NOT NULL DEFAULT (datetime('now','localtime')))";

    public DbHelper(Context context, String name, CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        xcontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATA_STATISTICS);
        db.execSQL(WORDS_BOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DATA_STATISTICS);
        db.execSQL(WORDS_BOOK);
    }

}
