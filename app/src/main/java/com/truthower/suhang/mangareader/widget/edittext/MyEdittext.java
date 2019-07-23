package com.truthower.suhang.mangareader.widget.edittext;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.truthower.suhang.mangareader.R;


public class MyEdittext extends LinearLayout {
    private Context context;
    private EditText mdEt;
    private ImageView clearIv;
    private View holderV;
    private TextInputLayout mdTil;

    public MyEdittext(Context context) {
        this(context, null);
    }

    public MyEdittext(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyEdittext(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(getLayoutId(), this);
        clearIv = (ImageView) findViewById(R.id.clear_iv);
        clearIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mdEt.setText("");
            }
        });
        holderV = findViewById(R.id.hodler_view);
        mdEt = (EditText) findViewById(R.id.md_et);
        mdTil = (TextInputLayout) findViewById(R.id.md_til);
        mdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mdTil.setError("");
                if (TextUtils.isEmpty(s)) {
                    clearIv.setVisibility(GONE);
                } else {
                    clearIv.setVisibility(VISIBLE);
                }
            }
        });
    }

    protected int getLayoutId() {
        return R.layout.view_error_edittext;
    }

    public void setEetFocusListener(OnFocusChangeListener listener) {
        mdEt.setOnFocusChangeListener(listener);
    }

    public void setEetKeyDownListener(OnKeyListener listener) {
        mdEt.setOnKeyListener(listener);
    }


    public void setErrorEtGravity(int gravity) {
        mdEt.setGravity(gravity);
    }

    public String getErrorText() {
        return mdTil.getError().toString();
    }

    public void setText(String text) {
        mdEt.setText(text);
    }

    public void setMaxLen(int length) {
        mdEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)}); //最大输入长度
    }

    public void setOnKeyListener(OnKeyListener listener) {
        mdEt.setOnKeyListener(listener);
    }

    public void setInputType(int type) {
        mdEt.setInputType(type);
    }

    public void setPasswordMode() {
        holderV.setVisibility(VISIBLE);
        mdEt.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    public void setErrorText(String errorText) {
        mdTil.setError(errorText);
    }

    public void setHint(String hint) {
        mdTil.setHint(hint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mdEt.isEnabled()) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setEditTextEnable(boolean enable) {
        mdEt.setEnabled(enable);
    }

    public String getText() {
        return mdEt.getText().toString();
    }
}
