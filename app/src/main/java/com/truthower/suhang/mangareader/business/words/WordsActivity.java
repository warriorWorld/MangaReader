package com.truthower.suhang.mangareader.business.words;

import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.WordsAdapter;
import com.truthower.suhang.mangareader.base.DependencyInjectorIml;
import com.truthower.suhang.mangareader.base.TTSActivity;
import com.truthower.suhang.mangareader.bean.WordsBookBean;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.listener.OnRecycleItemLongClickListener;
import com.truthower.suhang.mangareader.utils.VibratorUtil;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.recyclerview.LinearLayoutMangerWithoutBug;

import java.util.ArrayList;
import java.util.Collections;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * /storage/sdcard0/reptile/one-piece
 * <p/>
 * Created by Administrator on 2016/4/4.
 */
public class WordsActivity extends TTSActivity implements WordsContract.View {
    private WordsAdapter adapter;
    private View emptyView;
    private ArrayList<WordsBookBean> wordsList = new ArrayList<WordsBookBean>();
    private int nowPosition = 0;
    private ClipboardManager clip;//复制文本用
    private WordsContract.Presenter mPresenter;
    private RecyclerView wordsRcv;
    private TextView sizeTv;

    @Override
    public void displayWords(ArrayList<WordsBookBean> list) {
        wordsList = list;
        initRec();
        sizeTv.setText(list.size() + "");
    }

    @Override
    public void displayTranslate(int position, String translate) {
        wordsList.get(position).setTranslate(translate);
        adapter.setList(wordsList);
        adapter.notifyItemChanged(position);
    }

    @Override
    public void displayKillWord(int position) {
        VibratorUtil.Vibrate(WordsActivity.this, 60);
        adapter.remove(position);
        sizeTv.setText(wordsList.size() + "");
    }

    @Override
    public void displayErrorMsg(String msg) {
        baseToast.showToast(msg);
    }

    @Override
    public void setPresenter(WordsContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        initUI();
        setPresenter(new WordsPresenter(this, new DependencyInjectorIml(), this));
        mPresenter.onViewCreated();
    }


    @Override
    protected void onResume() {
        super.onResume();
//        text2Speech(wordsList.get(nowPosition).getWord());
    }

    private void initUI() {
        emptyView = findViewById(R.id.empty_view);
        wordsRcv = findViewById(R.id.words_rcv);
        sizeTv = findViewById(R.id.size_tv);
        wordsRcv.setLayoutManager
                (new LinearLayoutMangerWithoutBug
                        (this, LinearLayoutManager.VERTICAL, false));
        wordsRcv.setFocusable(false);
        wordsRcv.setHasFixedSize(true);
        LayoutAnimationController controller = new LayoutAnimationController(AnimationUtils.loadAnimation(this, R.anim.recycler_load));
        wordsRcv.setLayoutAnimation(controller);
        baseTopBar.setTitle("生词本");
        baseTopBar.setRightBackground(R.drawable.ic_shuffle_card);
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                Collections.shuffle(wordsList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_words;
    }


    private void initRec() {
        try {
            if (null == wordsList || wordsList.size() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
            if (null == adapter) {
                adapter = new WordsAdapter(this);
                adapter.setList(wordsList);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        mPresenter.killWord(position, wordsList.get(position).getWord());
                    }
                });
                adapter.setOnTranslateItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        translation(position);
                    }
                });
                adapter.setOnRecycleItemLongClickListener(new OnRecycleItemLongClickListener() {
                    @Override
                    public void onItemLongClick(int position) {
                        clip.setText(wordsList.get(position).getWord());
                    }
                });
                wordsRcv.setAdapter(adapter);
            } else {
                adapter.setList(wordsList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void translation(int position) {
        String word = wordsList.get(position).getWord();
        clip.setText(word);
        text2Speech(word);
        if (TextUtils.isEmpty(wordsList.get(position).getTranslate())) {
            mPresenter.translateWord(position, word);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }
}
