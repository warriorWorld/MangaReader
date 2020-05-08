package com.truthower.suhang.mangareader.business.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.listener.OnEditResultListener;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;
import com.truthower.suhang.mangareader.utils.PermissionUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.dialog.GestureDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaEditDialog;
import com.truthower.suhang.mangareader.widget.gesture.GestureLockViewGroup;

public class AboutActivity extends BaseActivity implements View.OnClickListener {
    private ImageView appIconIv;
    private TextView versionTv;
    private RelativeLayout checkUpdateRl;
    private RelativeLayout authorRl;
    private RelativeLayout keyboardRl;
    private View killableTimeRl, killPeriodRl;

    private CheckBox closeTranslateCb, economyModeCb, closeTutorialCb, closeWordsImgCb, closeClickImgCb;
    private CheckBox closeTtsCb;
    private CheckBox closeWrapCb;
    private RelativeLayout openPremiumRl;
    private CheckBox openPremiumCb;
    private RelativeLayout openPremiumVoiceRl;
    private CheckBox openPremiumVoiceCb;
    private View gestureRl;
    private ClipboardManager clip;//复制文本用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void refreshUI() {
        versionTv.setText(BaseParameterUtil.getInstance().getAppVersionName(this));
    }

    private void initUI() {
        appIconIv = (ImageView) findViewById(R.id.app_icon_iv);
        versionTv = (TextView) findViewById(R.id.version_tv);
        checkUpdateRl = (RelativeLayout) findViewById(R.id.check_update_rl);
        authorRl = (RelativeLayout) findViewById(R.id.author_rl);
        gestureRl = findViewById(R.id.gesture_rl);
        killableTimeRl = findViewById(R.id.killable_time_rl);
        killPeriodRl = findViewById(R.id.kill_peroid_rl);
        openPremiumRl = findViewById(R.id.open_premium_rl);
        openPremiumVoiceRl = findViewById(R.id.open_premium_voice_rl);
        if (PermissionUtil.isMaster(this) || PermissionUtil.isCreator(this)) {
            openPremiumRl.setVisibility(View.VISIBLE);
            openPremiumVoiceRl.setVisibility(View.VISIBLE);
        } else {
            openPremiumRl.setVisibility(View.GONE);
            openPremiumVoiceRl.setVisibility(View.GONE);
        }
        economyModeCb = (CheckBox) findViewById(R.id.economy_mode_cb);
        economyModeCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.ECONOMY_MODE, isChecked);
            }
        });
        closeTranslateCb = (CheckBox) findViewById(R.id.close_translate_cb);
        closeTranslateCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.CLOSE_TRANSLATE, isChecked);
            }
        });
        closeTutorialCb = (CheckBox) findViewById(R.id.close_tutorial_cb);
        closeTutorialCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.CLOSE_TUTORIAL, isChecked);
            }
        });
        closeTtsCb = (CheckBox) findViewById(R.id.close_tts_cb);
        closeTtsCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.CLOSE_TTS, isChecked);
            }
        });
        closeWrapCb = findViewById(R.id.close_wrap_cb);
        closeWrapCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.CLOSE_WRAP_IMG, isChecked);
            }
        });
        closeWordsImgCb = findViewById(R.id.close_words_img_cb);
        closeWordsImgCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.CLOSE_WORD_IMG, isChecked);
            }
        });
        closeClickImgCb = findViewById(R.id.close_click_img_cb);
        closeClickImgCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.CLOSE_CLICK_IMG, isChecked);
            }
        });
        openPremiumCb = findViewById(R.id.open_premium_cb);
        openPremiumCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.OPEN_PREMIUM_KEY, isChecked);
                if (isChecked){
                    openPremiumVoiceRl.setVisibility(View.VISIBLE);
                }else {
                    openPremiumVoiceCb.setChecked(false);
                    openPremiumVoiceRl.setVisibility(View.GONE);
                }
            }
        });
        openPremiumVoiceCb = findViewById(R.id.open_premium_voice_cb);
        openPremiumVoiceCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.OPEN_PREMIUM_VOICE_KEY, isChecked);
            }
        });
        keyboardRl = (RelativeLayout) findViewById(R.id.keyboard_rl);
        closeTranslateCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(AboutActivity.this,
                        ShareKeys.CLOSE_TRANSLATE, false));
        closeTutorialCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(AboutActivity.this,
                        ShareKeys.CLOSE_TUTORIAL, true));
        economyModeCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(AboutActivity.this,
                        ShareKeys.ECONOMY_MODE, false));
        closeTtsCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(AboutActivity.this,
                        ShareKeys.CLOSE_TTS, false));
        closeWrapCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(AboutActivity.this,
                        ShareKeys.CLOSE_WRAP_IMG, false));
        closeWordsImgCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(AboutActivity.this,
                        ShareKeys.CLOSE_WORD_IMG, false));
        closeClickImgCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(AboutActivity.this,
                        ShareKeys.CLOSE_CLICK_IMG, false));
        openPremiumCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(AboutActivity.this,
                        ShareKeys.OPEN_PREMIUM_KEY, false));
        openPremiumVoiceCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(AboutActivity.this,
                        ShareKeys.OPEN_PREMIUM_VOICE_KEY, false));
        gestureRl.setOnClickListener(this);
        keyboardRl.setOnClickListener(this);
        appIconIv.setOnClickListener(this);
        checkUpdateRl.setOnClickListener(this);
        killPeriodRl.setOnClickListener(this);
        killableTimeRl.setOnClickListener(this);
        authorRl.setOnClickListener(this);
        baseTopBar.setTitle("设置");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    private void showAuthorDialog() {
        MangaDialog authorDialog = new MangaDialog(this);
        authorDialog.show();
        authorDialog.setTitle("联系作者");
        authorDialog.setOkText("知道了");
        authorDialog.setMessage("qq:" + Configure.QQ);
    }

    private void showGestureDialog() {
        final GestureDialog gestureDialog = new GestureDialog(this);
        gestureDialog.show();
        gestureDialog.setOnGestureLockViewListener(new GestureLockViewGroup.OnGestureLockViewListener() {
            @Override
            public void onBlockSelected(int cId) {

            }

            @Override
            public void onGestureEvent(boolean matched) {

            }

            @Override
            public void onGestureEvent(String choose) {
                SharedPreferencesUtils.setSharedPreferencesData(AboutActivity.this, ShareKeys.IS_MASTER, false);
                SharedPreferencesUtils.setSharedPreferencesData(AboutActivity.this, ShareKeys.IS_CREATOR, false);
                if (choose.equals("7485")) {
                    SharedPreferencesUtils.setSharedPreferencesData(AboutActivity.this, ShareKeys.IS_CREATOR, true);
                } else if (choose.equals("3427")) {
                    SharedPreferencesUtils.setSharedPreferencesData(AboutActivity.this, ShareKeys.IS_MASTER, true);
                }
                gestureDialog.dismiss();
            }

            @Override
            public void onUnmatchedExceedBoundary() {

            }
        });
    }

    private void showUpdateDialog() {
        MangaDialog dialog = new MangaDialog(this);
        dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                clip.setText(Configure.QQ);
                baseToast.showToast("复制成功");
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setTitle("云服务停用");
        dialog.setMessage("由于失去云服务,后续更新只能在qq群里发布所以请大家加qq" + Configure.QQ + ".");
        dialog.setOkText("复制群号");
    }

    private void showKillableTimeDialog() {
        MangaEditDialog dialog = new MangaEditDialog(this);
        dialog.setOnEditResultListener(new OnEditResultListener() {
            @Override
            public void onResult(String text) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.KILLABLE_TIME_KEY, Integer.valueOf(text));
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setOnlyNumInput(true);
        dialog.setTitle("可斩次数设置");
        dialog.setHint("默认值为3 仅供参考");
        dialog.setMessage("请输入要设置的可斩次数，如需一次既斩请输入1。");
        int height = SharedPreferencesUtils.getIntSharedPreferencesData(this, ShareKeys.KILLABLE_TIME_KEY, -1);
        if (height != -1) {
            dialog.setEditText(height + "");
        }
    }

    private void showKillPeriodDialog() {
        MangaEditDialog dialog = new MangaEditDialog(this);
        dialog.setOnEditResultListener(new OnEditResultListener() {
            @Override
            public void onResult(String text) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (AboutActivity.this, ShareKeys.KILL_PERIOD_KEY, Integer.valueOf(text));
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setOnlyNumInput(true);
        dialog.setTitle("已斩单词出现间隔时长设置");
        dialog.setHint("默认值为6小时 仅供参考");
        dialog.setMessage("请输入要设置的已斩单词出现间隔时长（单位：小时）");
        int height = SharedPreferencesUtils.getIntSharedPreferencesData(this, ShareKeys.KILL_PERIOD_KEY, -1);
        if (height != -1) {
            dialog.setEditText(height + "");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_icon_iv:
                break;
            case R.id.check_update_rl:
                showUpdateDialog();
                break;
            case R.id.author_rl:
                showAuthorDialog();
                break;
            case R.id.keyboard_rl:
                Intent intent1 = new Intent(AboutActivity.this, KeyboardSettingActivity.class);
                startActivity(intent1);
                break;
            case R.id.gesture_rl:
                showGestureDialog();
                break;
            case R.id.killable_time_rl:
                showKillableTimeDialog();
                break;
            case R.id.kill_peroid_rl:
                showKillPeriodDialog();
                break;
        }
    }
}
