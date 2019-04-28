package com.truthower.suhang.mangareader.business.wordsbook;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;


/**
 * Created by Administrator on 2016/4/4.
 */
public class WordsBookView extends RelativeLayout {
    private Context context;
    private String word, translate, TRANSLATING = "查询中";
    private TextView wordTv;
    private OnWordsBookViewListener onWordsBookViewListener;
    private int DURATION = 500;//动画时间
    private float rotationValue = 0f;

    public WordsBookView(Context context) {
        this(context, null);
    }

    public WordsBookView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_words_book, this);
        wordTv = (TextView) findViewById(R.id.word);
        wordTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playWordTvAnimation();
                if (null != onWordsBookViewListener) {
                    onWordsBookViewListener.onWordClick(word);
                }
            }
        });
        wordTv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != onWordsBookViewListener) {
                    onWordsBookViewListener.onWordLongClick(word);
                }
                return true;
            }
        });
    }

    public void playWordTvAnimation() {
        rotationValue = rotationValue + 360f;
        ObjectAnimator wordTvAnimation = ObjectAnimator.ofFloat(wordTv, "rotationY", rotationValue);
        AnimatorSet set = new AnimatorSet();
        //属性动画监听类
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //切换显示
                if (word.equals(wordTv.getText().toString())) {
                    wordTv.setTextSize(24);
                    if (TextUtils.isEmpty(translate)) {
                        //如果是空的 就通知查询单词
                        wordTv.setText(TRANSLATING);
                        if (null != onWordsBookViewListener) {
                            onWordsBookViewListener.queryWord(word);
                        }
                    } else {
                        wordTv.setText(translate);
                    }
                } else {
                    wordTv.setTextSize(38);
                    wordTv.setText(word);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.playTogether(wordTvAnimation);
        set.setDuration(DURATION);
        set.start();
    }


    public void setWord(String word) {
        this.word = word;
        wordTv.setText(word);
    }

    public void setTranslate(String translate) {
        this.translate = translate;
        if (TRANSLATING.equals(wordTv.getText().toString())) {
            wordTv.setText(translate);
        }
    }

    public void setOnWordsBookViewListener(OnWordsBookViewListener onWordsBookViewListener) {
        this.onWordsBookViewListener = onWordsBookViewListener;
    }


    public interface OnWordsBookViewListener {
        //每次点击都会调用
        public void onWordClick(String word);

        //需要查词的时候才会调用
        public void queryWord(String word);

        //长按都会调用
        public void onWordLongClick(String word);
    }
}
