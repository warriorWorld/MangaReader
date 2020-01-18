package com.truthower.suhang.mangareader.business.words;

import android.content.Context;

import com.truthower.suhang.mangareader.base.DependencyInjector;
import com.truthower.suhang.mangareader.bean.WordsBookBean;

import java.util.ArrayList;

public class WordsPresenter implements WordsContract.Presenter {
    private Context mContext;
    private DependencyInjector mInjector;
    private WordsContract.View mView;

    public WordsPresenter(Context context, DependencyInjector injector, WordsContract.View view) {
        this.mContext = context;
        this.mInjector = injector;
        this.mView = view;
    }

    @Override
    public void onViewCreated() {
        getWords();
    }

    @Override
    public void getWords() {
        mView.displayWords(mInjector.dataRepository(mContext).queryAllWordsBook());
    }

    @Override
    public void killWord(int position) {

    }

    @Override
    public void translateWord() {

    }

    @Override
    public void onDestroy() {
        mContext = null;
        mInjector = null;
        mView = null;
    }
}
