package com.truthower.suhang.mangareader.widget.keyboard;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.business.other.KeyboardSettingActivity;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.listener.OnKeyboardChangeListener;
import com.truthower.suhang.mangareader.listener.OnKeyboardListener;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.button.GestureButton;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;


public class English9KeyBoardView extends RelativeLayout implements View.OnClickListener, GestureButton.OnResultListener {
    private Context context;
    private GestureButton abcGb;
    private GestureButton defGb;
    private GestureButton ghiGb;
    private GestureButton jklGb;
    private GestureButton mnoGb;
    private GestureButton pqrsGb;
    private GestureButton tuvGb;
    private GestureButton wxyzGb;
    protected TextView finalResTv;
    private View deleteBtn;
    private View spaceBtn;
    private View okBtn;
    private View helpBtn;
    private View optionsBtn;
    protected OnKeyboardChangeListener mOnKeyboardChangeListener;
    private OnKeyboardListener mOnKeyboardListener;

    public English9KeyBoardView(Context context) {
        this(context, null);
    }

    public English9KeyBoardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public English9KeyBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    protected void init() {
        LayoutInflater.from(context).inflate(R.layout.keyboard_english9, this);
        finalResTv = (TextView) findViewById(R.id.final_res_tv);
        abcGb = (GestureButton) findViewById(R.id.abc_gb);
        defGb = (GestureButton) findViewById(R.id.def_gb);
        ghiGb = (GestureButton) findViewById(R.id.ghi_gb);
        jklGb = (GestureButton) findViewById(R.id.jkl_gb);
        mnoGb = (GestureButton) findViewById(R.id.mno_gb);
        pqrsGb = (GestureButton) findViewById(R.id.pqrs_gb);
        tuvGb = (GestureButton) findViewById(R.id.tuv_gb);
        wxyzGb = (GestureButton) findViewById(R.id.wxyz_gb);
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData(context, ShareKeys.IS_OTHER_LETTER_ORDER, false)) {
            setupKeys();
        } else {
            setupKeys1();
        }
        deleteBtn = findViewById(R.id.delete_btn);
        spaceBtn = findViewById(R.id.space_btn);
        okBtn = findViewById(R.id.ok_btn);
        helpBtn = findViewById(R.id.help_btn);
        optionsBtn=findViewById(R.id.options_iv);

        optionsBtn.setOnClickListener(this);
        helpBtn.setOnClickListener(this);
        abcGb.setOnResultListener(this);
        defGb.setOnResultListener(this);
        ghiGb.setOnResultListener(this);
        jklGb.setOnResultListener(this);
        mnoGb.setOnResultListener(this);
        pqrsGb.setOnResultListener(this);
        tuvGb.setOnResultListener(this);
        wxyzGb.setOnResultListener(this);
        deleteBtn.setOnClickListener(this);
        spaceBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
        deleteBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                finalResTv.setText("");
                return true;
            }
        });
    }

    private void setupKeys() {
        abcGb.setKeys("bac");
        defGb.setKeys("edf");
        ghiGb.setKeys("hgi");
        jklGb.setKeys("kjl");
        mnoGb.setKeys("nmo");
        pqrsGb.setKeys("qprs");
        tuvGb.setKeys("utv");
        wxyzGb.setKeys("xwyz");
    }

    private void setupKeys1() {
        abcGb.setKeys("abc");
        defGb.setKeys("def");
        ghiGb.setKeys("ghi");
        jklGb.setKeys("jkl");
        mnoGb.setKeys("mno");
        pqrsGb.setKeys("pqrs");
        tuvGb.setKeys("tuv");
        wxyzGb.setKeys("wxyz");
    }

    protected void onOkBtnClick() {
        if (null != mOnKeyboardChangeListener) {
            mOnKeyboardChangeListener.onFinish(finalResTv.getText().toString());
        }
        finalResTv.setText("");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.delete_btn:
                if (finalResTv.getText().toString().length() > 0) {
                    finalResTv.setText(finalResTv.getText().toString().substring(0, finalResTv.getText().length() - 1));
                }
                break;
            case R.id.ok_btn:
                onOkBtnClick();
                break;
            case R.id.space_btn:
                finalResTv.setText(finalResTv.getText().toString() + " ");
                break;
            case R.id.help_btn:
                if (null!=mOnKeyboardListener){
                    mOnKeyboardListener.onQuestionClick();
                }
                break;
            case R.id.options_iv:
                if (null!=mOnKeyboardListener){
                    mOnKeyboardListener.onOptionsClick();
                }
                break;
        }
    }

    @Override
    public void onResult(String result) {
        finalResTv.setText(finalResTv.getText().toString() + result);
    }

    @Override
    public void onChange(String result) {
        if (null != mOnKeyboardChangeListener) {
            mOnKeyboardChangeListener.onChange(result);
        }
    }

    public void setOnKeyboardChangeListener(OnKeyboardChangeListener onKeyboardChangeListener) {
        mOnKeyboardChangeListener = onKeyboardChangeListener;
    }

    public void setOnKeyboardListener(OnKeyboardListener onKeyboardListener) {
        mOnKeyboardListener = onKeyboardListener;
    }
}
