package com.truthower.suhang.mangareader.widget.dialog;/**
 * Created by Administrator on 2016/11/4.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.utils.AudioMgr;
import com.youdao.sdk.common.YouDaoLog;
import com.youdao.sdk.ydtranslate.Translate;
import com.youdao.sdk.ydtranslate.WebExplain;

import androidx.constraintlayout.widget.Group;


/**
 * 作者：苏航 on 2016/11/4 11:08
 * 邮箱：772192594@qq.com
 */
public class TranslateResultDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView wordTv;
    private ImageView ukIv;
    private TextView ukPhoneticTv;
    private ImageView usIv;
    private TextView usPhoneticTv;
    private TextView translateTv;
    private TextView webTranslateTv;
    private TextView okTv;
    private Translate mTranslate;
    private Group webTranslateGroup;
    private Group ukGroup,usGroup;

    public TranslateResultDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_vip_translate);
        init();

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager wm = ((Activity) context).getWindowManager();
        Display d = wm.getDefaultDisplay();
        // lp.height = (int) (d.getHeight() * 0.4);
        lp.width = (int) (d.getWidth() * 1);
        // window.setGravity(Gravity.LEFT | Gravity.TOP);
        window.setGravity(Gravity.CENTER);
//        window.getDecorView().setPadding(0, 0, 0, 0);
        // lp.x = 100;
        // lp.y = 100;
        // lp.height = 30;
        // lp.width = 20;
        window.setAttributes(lp);
    }


    private void init() {
        wordTv = (TextView) findViewById(R.id.word_tv);
        ukIv = (ImageView) findViewById(R.id.uk_iv);
        ukPhoneticTv = (TextView) findViewById(R.id.uk_phonetic_tv);
        usIv = (ImageView) findViewById(R.id.us_iv);
        usPhoneticTv = (TextView) findViewById(R.id.us_phonetic_tv);
        translateTv = (TextView) findViewById(R.id.translate_tv);
        webTranslateTv = (TextView) findViewById(R.id.web_translate_tv);
        okTv = findViewById(R.id.ok_tv);
        ukGroup=findViewById(R.id.uk_group);
        usGroup=findViewById(R.id.us_group);
        webTranslateGroup = findViewById(R.id.web_translate_group);
        webTranslateGroup.setVisibility(View.GONE);
        ukPhoneticTv.setOnClickListener(this);
        usPhoneticTv.setOnClickListener(this);
        ukIv.setOnClickListener(this);
        usIv.setOnClickListener(this);
        okTv.setOnClickListener(this);
    }

    public void setTranslate(Translate translate) {
        mTranslate = translate;
        if (null != translate && null != translate.getExplains() && translate.getExplains().size() > 0) {
            playVoice(translate.getUSSpeakUrl());

            StringBuilder translateSb = new StringBuilder();
            for (int i = 0; i < translate.getExplains().size(); i++) {
                translateSb.append(translate.getExplains().get(i)).append(";\n");
            }
            wordTv.setText(translate.getQuery());
            if (!TextUtils.isEmpty(translate.getUkPhonetic())) {
                ukPhoneticTv.setText("/" + translate.getUkPhonetic() + "/");
                ukGroup.setVisibility(View.VISIBLE);
            } else {
//                ukPhoneticTv.setText("");
                ukGroup.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(translate.getUsPhonetic())) {
                usPhoneticTv.setText("/" + translate.getUsPhonetic() + "/");
                usGroup.setVisibility(View.VISIBLE);
            } else {
                usPhoneticTv.setText("");
                usGroup.setVisibility(View.GONE);
            }
            translateTv.setText(translateSb);
            if (null != translate.getWebExplains() && translate.getWebExplains().size() > 1) {
                webTranslateGroup.setVisibility(View.VISIBLE);
                StringBuilder webTranslateSb = new StringBuilder();
                for (int i = 1; i < translate.getWebExplains().size(); i++) {
                    WebExplain item = translate.getWebExplains().get(i);
                    webTranslateSb.append(item.getKey()).append(":\n");
                    for (int j = 0; j < item.getMeans().size(); j++) {
                        webTranslateSb.append(item.getMeans().get(j)).append(";\n");
                    }
                    webTranslateSb.append("\n");
                }
                webTranslateTv.setText(webTranslateSb);
            } else {
                webTranslateGroup.setVisibility(View.GONE);
            }
        }
    }

    private synchronized void playVoice(String speakUrl) {
        YouDaoLog.e(AudioMgr.PLAY_LOG + "TranslateDetailActivity click to playVoice speakUrl = " + speakUrl);
        if (!TextUtils.isEmpty(speakUrl) && speakUrl.startsWith("http")) {
            AudioMgr.startPlayVoice(speakUrl, new AudioMgr.SuccessListener() {
                @Override
                public void success() {
                    YouDaoLog.e(AudioMgr.PLAY_LOG + "TranslateDetailActivity playVoice success");
                }

                @Override
                public void playover() {
                    YouDaoLog.e(AudioMgr.PLAY_LOG + "TranslateDetailActivity playover");
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_tv:
                dismiss();
                break;
            case R.id.uk_iv:
            case R.id.uk_phonetic_tv:
                playVoice(mTranslate.getUKSpeakUrl());
                break;
            case R.id.us_iv:
            case R.id.us_phonetic_tv:
                playVoice(mTranslate.getUSSpeakUrl());
                break;
        }
    }
}
