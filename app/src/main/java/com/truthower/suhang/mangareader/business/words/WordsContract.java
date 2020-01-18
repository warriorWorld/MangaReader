package com.truthower.suhang.mangareader.business.words;

import com.truthower.suhang.mangareader.base.BasePresenter;
import com.truthower.suhang.mangareader.base.BaseView;
import com.truthower.suhang.mangareader.bean.WordsBookBean;

import java.util.ArrayList;

public interface WordsContract {
    interface Presenter extends BasePresenter {
        void onViewCreated();

        void getWords();

        void killWord(int position, String word);

        void translateWord(int position, String word);
    }

    interface View extends BaseView<Presenter> {
        void displayWords(ArrayList<WordsBookBean> list);

        void displayTranslate(int position, String translate);

        void displayKillWord(int position);

        void displayErrorMsg(String msg);
    }
}
