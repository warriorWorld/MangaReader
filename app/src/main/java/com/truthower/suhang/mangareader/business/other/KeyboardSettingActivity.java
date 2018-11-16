package com.truthower.suhang.mangareader.business.other;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;

public class KeyboardSettingActivity extends BaseActivity {
    private CheckBox closeShKeyboardCb;
    private CheckBox closeKeyboardSoundCb;
    private CheckBox closeKeyboardVibrationCb;
    private CheckBox toggleKeyboardOrderCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        closeShKeyboardCb = (CheckBox) findViewById(R.id.close_sh_keyboard_cb);
        closeKeyboardSoundCb = (CheckBox) findViewById(R.id.close_keyboard_sound_cb);
        closeKeyboardVibrationCb = (CheckBox) findViewById(R.id.close_keyboard_vibration_cb);
        toggleKeyboardOrderCb = (CheckBox) findViewById(R.id.toggle_keyboard_order_cb);
        closeShKeyboardCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                baseToast.showToast("关闭后可从设置中再次开启");
                SharedPreferencesUtils.setSharedPreferencesData
                        (KeyboardSettingActivity.this, ShareKeys.CLOSE_SH_KEYBOARD, isChecked);
            }
        });
        closeShKeyboardCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(KeyboardSettingActivity.this,
                        ShareKeys.CLOSE_SH_KEYBOARD, false));

        closeKeyboardSoundCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (KeyboardSettingActivity.this, ShareKeys.CLOSE_SH_KEYBOARD_SOUND, isChecked);
            }
        });
        closeKeyboardSoundCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(KeyboardSettingActivity.this,
                        ShareKeys.CLOSE_SH_KEYBOARD_SOUND, false));

        closeKeyboardVibrationCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (KeyboardSettingActivity.this, ShareKeys.CLOSE_SH_KEYBOARD_VIBRATION, isChecked);
            }
        });
        closeKeyboardVibrationCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(KeyboardSettingActivity.this,
                        ShareKeys.CLOSE_SH_KEYBOARD_VIBRATION, false));

        toggleKeyboardOrderCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (KeyboardSettingActivity.this, ShareKeys.IS_OTHER_LETTER_ORDER, isChecked);
            }
        });
        toggleKeyboardOrderCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(KeyboardSettingActivity.this,
                        ShareKeys.IS_OTHER_LETTER_ORDER, false));
        baseTopBar.setTitle("键盘设置");
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_keyboard_setting;
    }
}
