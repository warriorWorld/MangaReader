package com.truthower.suhang.mangareader.business.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.dialog.GestureDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.gesture.GestureLockViewGroup;

public class AboutActivity extends BaseActivity implements View.OnClickListener {
    private ImageView appIconIv;
    private TextView versionTv;
    private RelativeLayout checkUpdateRl;
    private RelativeLayout authorRl;
    private RelativeLayout keyboardRl;

    private CheckBox closeTranslateCb, economyModeCb, closeTutorialCb;
    private CheckBox closeTtsCb;
    private View gestureRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
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

        gestureRl.setOnClickListener(this);
        keyboardRl.setOnClickListener(this);
        appIconIv.setOnClickListener(this);
        checkUpdateRl.setOnClickListener(this);
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
        authorDialog.setMessage("作者:  苏航\n邮箱:  772192594@qq.com");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_icon_iv:
                break;
            case R.id.check_update_rl:
                showBaseDialog("欢迎来到404之国", "由于某些不能说的原因,本App彻底与后台服务断开连接,如有更新需求请加qq群782685214", "确定", "取消", null);
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
        }
    }
}
