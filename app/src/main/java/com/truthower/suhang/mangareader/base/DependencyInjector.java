package com.truthower.suhang.mangareader.base;

import android.content.Context;

import com.truthower.suhang.mangareader.db.DbAdapter;

public interface DependencyInjector {
    DbAdapter dataRepository(Context context);
}
