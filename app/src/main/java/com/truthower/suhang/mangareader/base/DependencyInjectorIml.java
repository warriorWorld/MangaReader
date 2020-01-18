package com.truthower.suhang.mangareader.base;

import android.content.Context;

import com.truthower.suhang.mangareader.db.DbAdapter;

public class DependencyInjectorIml implements DependencyInjector {
    @Override
    public DbAdapter dataRepository(Context context) {
        return new DbAdapter(context);
    }
}
