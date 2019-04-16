package com.truthower.suhang.mangareader.utils;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class VolumeUtil {
    public static String getVoiceStatus(Context context) {//获取手机设置声音模式
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int mode = audioManager.getRingerMode();
        switch (mode) {
            case AudioManager.RINGER_MODE_NORMAL:
                //普通模式2
                return AudioManager.RINGER_MODE_NORMAL + "";
            case AudioManager.RINGER_MODE_VIBRATE:
                // 振动模式1
                return AudioManager.RINGER_MODE_VIBRATE + "";
            case AudioManager.RINGER_MODE_SILENT:
                //静音模式0
                return AudioManager.RINGER_MODE_SILENT + "";
        }
        return "000";
    }

    public static int getCallColume(Context context) {//获取通话音量
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);//最大
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);//当前
        Log.d("aaaaaaaa", "max : " + max + " current : " + current);
        return current;
    }

    public static float getMusicVolumeRate(Context context) {//获取通话音量
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//最大
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);//当前
        Log.d("aaaaaaaa", "max : " + max + " current : " + current);
        return Float.valueOf(current) / Float.valueOf(max);
    }

    public static boolean getHeadPhoneStatus(Context context) {//获取耳机状态
        AudioManager localAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        localAudioManager.isWiredHeadsetOn();
        return localAudioManager.isWiredHeadsetOn()||localAudioManager.isBluetoothA2dpOn();
    }
}
