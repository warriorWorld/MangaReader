package com.truthower.suhang.mangareader.business.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.business.download.DownloadActivity;
import com.truthower.suhang.mangareader.business.other.AboutActivity;
import com.truthower.suhang.mangareader.business.tag.TagFilterActivity;
import com.truthower.suhang.mangareader.business.user.LoginActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.dialog.QrDialog;
import com.truthower.suhang.mangareader.widget.imageview.CircleImage;

public class UserFragment extends BaseFragment implements View.OnClickListener {
    private RelativeLayout collectRl;
    private RelativeLayout statisticsRl;
    private RelativeLayout newWordBookRl;
    private RelativeLayout downloadRl;
    private RelativeLayout aboutRl;
    private RelativeLayout shareRl;
    private RelativeLayout waitingForUpdateRl;
    private RelativeLayout userTopBarRl;
    private RelativeLayout finishedMangaRl;
    private TextView userNameTv;
    private CircleImage userHeadCiv;
    private CheckBox autoToLastReadPositionCb, closeTranslateCb, economyModeCb, closeTutorialCb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        initUI(v);
        refreshUI();
        toggleLoginStateUI();
        return v;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        try {
            if (!hidden) {
                toggleLoginStateUI();
            }
        } catch (Exception e) {
            //这时候有可能fragment还没绑定上activity
        }
    }

    private void initUI(View v) {
        collectRl = (RelativeLayout) v.findViewById(R.id.collect_rl);
        statisticsRl = (RelativeLayout) v.findViewById(R.id.statistics_rl);
        newWordBookRl = (RelativeLayout) v.findViewById(R.id.new_word_book_rl);
        downloadRl = (RelativeLayout) v.findViewById(R.id.download_rl);
        waitingForUpdateRl = (RelativeLayout) v.findViewById(R.id.waiting_for_update_rl);
        userHeadCiv = (CircleImage) v.findViewById(R.id.user_head_civ);
        userTopBarRl = (RelativeLayout) v.findViewById(R.id.user_top_bar_rl);
        userHeadCiv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (LoginBean.getInstance().isMaster()) {
                    Intent intent = new Intent(getActivity(), TagFilterActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
        aboutRl = (RelativeLayout) v.findViewById(R.id.about_rl);
        shareRl = (RelativeLayout) v.findViewById(R.id.share_rl);
        finishedMangaRl = (RelativeLayout) v.findViewById(R.id.finished_manga_rl);
        userNameTv = (TextView) v.findViewById(R.id.user_name_tv);
        autoToLastReadPositionCb = (CheckBox) v.findViewById(R.id.auto_last_position_cb);
        autoToLastReadPositionCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (getActivity(), ShareKeys.AUTO_TO_LAST_POSITION, isChecked);
            }
        });
        economyModeCb = (CheckBox) v.findViewById(R.id.economy_mode_cb);
        economyModeCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (getActivity(), ShareKeys.ECONOMY_MODE, isChecked);
            }
        });
        closeTranslateCb = (CheckBox) v.findViewById(R.id.close_translate_cb);
        closeTranslateCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (getActivity(), ShareKeys.CLOSE_TRANSLATE, isChecked);
            }
        });
        closeTutorialCb = (CheckBox) v.findViewById(R.id.close_tutorial_cb);
        closeTutorialCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setSharedPreferencesData
                        (getActivity(), ShareKeys.CLOSE_TUTORIAL, isChecked);
            }
        });

        autoToLastReadPositionCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(getActivity(),
                        ShareKeys.AUTO_TO_LAST_POSITION, false));
        closeTranslateCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(getActivity(),
                        ShareKeys.CLOSE_TRANSLATE, false));
        closeTutorialCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(getActivity(),
                        ShareKeys.CLOSE_TUTORIAL, false));
        economyModeCb.setChecked
                (SharedPreferencesUtils.getBooleanSharedPreferencesData(getActivity(),
                        ShareKeys.ECONOMY_MODE, false));
        collectRl.setOnClickListener(this);
        statisticsRl.setOnClickListener(this);
        newWordBookRl.setOnClickListener(this);
        downloadRl.setOnClickListener(this);
        userTopBarRl.setOnClickListener(this);
        aboutRl.setOnClickListener(this);
        finishedMangaRl.setOnClickListener(this);
        shareRl.setOnClickListener(this);
        waitingForUpdateRl.setOnClickListener(this);
    }

    private void refreshUI() {
    }

    private void toggleLoginStateUI() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
            userNameTv.setText("点击登录");
            userTopBarRl.setEnabled(true);
        } else {
            if (LoginBean.getInstance().isMaster()) {
                userNameTv.setTextColor(getResources().getColor(R.color.master));
            } else {
                userNameTv.setTextColor(getResources().getColor(R.color.white));
            }
            userNameTv.setText(LoginBean.getInstance().getUserName());
            userTopBarRl.setEnabled(false);
        }
    }

    private void showQrDialog() {
        QrDialog qrDialog = new QrDialog(getActivity());
        qrDialog.show();
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.collect_rl:
//                intent = new Intent(getActivity(), CollectedActivity.class);
                intent.putExtra("collectType", Configure.COLLECT_TYPE_COLLECT);
                break;
            case R.id.waiting_for_update_rl:
//                intent = new Intent(getActivity(), CollectedActivity.class);
                intent.putExtra("collectType", Configure.COLLECT_TYPE_WAIT_FOR_UPDATE);
                break;
            case R.id.finished_manga_rl:
//                intent = new Intent(getActivity(), CollectedActivity.class);
                intent.putExtra("collectType", Configure.COLLECT_TYPE_FINISHED);
                break;
            case R.id.statistics_rl:
                baseToast.showToast("待开发");
                break;
            case R.id.new_word_book_rl:
                baseToast.showToast("待开发");
                break;
            case R.id.download_rl:
                intent = new Intent(getActivity(), DownloadActivity.class);
                break;
            case R.id.user_top_bar_rl:
                intent = new Intent(getActivity(), LoginActivity.class);
                break;
            case R.id.about_rl:
                intent = new Intent(getActivity(), AboutActivity.class);
                break;
            case R.id.share_rl:
                showQrDialog();
                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }
}
