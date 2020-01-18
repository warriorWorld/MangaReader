package com.truthower.suhang.mangareader.business.words;

import com.truthower.suhang.mangareader.base.BasePresenter;
import com.truthower.suhang.mangareader.base.BaseView;
import com.truthower.suhang.mangareader.bean.WordsBookBean;

import java.util.ArrayList;

public interface WordsContract {
    interface Presenter extends BasePresenter {
        void onViewCreated();

        void getWords();

        void killWord(int position);

        void translateWord();
    }

    interface View extends BaseView<Presenter> {
        void displayWords(ArrayList<WordsBookBean> list);

        void displayTranslate(String translate);

        void displayKillWord();
    }
}
