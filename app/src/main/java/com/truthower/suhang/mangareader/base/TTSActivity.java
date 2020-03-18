package com.truthower.suhang.mangareader.base;

import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.text.TextUtils;


import com.truthower.suhang.mangareader.utils.AudioMgr;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.utils.VolumeUtil;
import com.youdao.sdk.common.YouDaoLog;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Administrator on 2018/4/12.
 */

public abstract class TTSActivity extends BaseActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTTS();
    }

    private void initTTS() {
        tts = new TextToSpeech(this, this); // 参数Context,TextToSpeech.OnInitListener
    }

    protected void text2Speech(String text) {
        text2Speech(text, true);
    }

    protected void text2Speech(String text, boolean breakSpeaking) {
        if (tts == null) {
            return;
        }
        if (tts.isSpeaking()) {
            if (breakSpeaking) {
                tts.stop();
            } else {
                return;
            }
        }
        tts.setPitch(1f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        HashMap<String, String> myHashAlarm = new HashMap();
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                String.valueOf(AudioManager.STREAM_ALARM));
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_VOLUME,
                VolumeUtil.getMusicVolumeRate(this) + "");

        if (VolumeUtil.getHeadPhoneStatus(this)) {
            AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
//            mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
            mAudioManager.startBluetoothSco();
        }
        tts.speak(text,
                TextToSpeech.QUEUE_FLUSH, myHashAlarm);
    }

    public synchronized void playVoice(String speakUrl) {
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
    protected void onDestroy() {
        super.onDestroy();
        tts.stop(); // 不管是否正在朗读TTS都被打断
        tts.shutdown(); // 关闭，释放资源
    }

    /**
     * 用来初始化TextToSpeech引擎
     * status:SUCCESS或ERROR这2个值
     * setLanguage设置语言，帮助文档里面写了有22种
     * TextToSpeech.LANG_MISSING_DATA：表示语言的数据丢失。
     * TextToSpeech.LANG_NOT_SUPPORTED:不支持
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.UK);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                baseToast.showToast("数据丢失或不支持");
            }
        }
    }
}
