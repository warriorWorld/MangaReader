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
import com.truthower.suhang.mangareader.business.ad.AdvertisingActivity;
import com.truthower.suhang.mangareader.business.download.DownloadActivity;
import com.truthower.suhang.mangareader.business.other.AboutActivity;
import com.truthower.suhang.mangareader.business.tag.TagFilterActivity;
import com.truthower.suhang.mangareader.business.user.CollectedActivity;
import com.truthower.suhang.mangareader.business.user.LoginActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.listener.OnShareAppClickListener;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
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
    private RelativeLayout adRl;
    private TextView userNameTv;
    private CircleImage userHeadCiv;
    private OnShareAppClickListener onShareAppClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        initUI(v);
        refreshUI();
        toggleLoginStateUI();
        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(getActivity(), ShareKeys.CLOSE_TUTORIAL, false)) {
            MangaDialog dialog = new MangaDialog(getActivity());
            dialog.show();
            dialog.setTitle("教程");
            dialog.setMessage("1,点击分享App,可以打开该App的下载二维码,让你想分享的人扫描即可下载,也可将该二维码保存(会保存在manga文件夹中)然后发给你想分享的人." +
                    "\n2,想要更新App可以进入设置页检查更新下载最新App" +
                    "\n3,退出登录可以进入设置页退出"+
                    "\n4,设置页可以关闭本教程");
        }
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
        adRl = (RelativeLayout) v.findViewById(R.id.ad_rl);
        finishedMangaRl = (RelativeLayout) v.findViewById(R.id.finished_manga_rl);
        userNameTv = (TextView) v.findViewById(R.id.user_name_tv);

        adRl.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.collect_rl:
                intent = new Intent(getActivity(), CollectedActivity.class);
                intent.putExtra("collectType", Configure.COLLECT_TYPE_COLLECT);
                break;
            case R.id.waiting_for_update_rl:
                intent = new Intent(getActivity(), CollectedActivity.class);
                intent.putExtra("collectType", Configure.COLLECT_TYPE_WAIT_FOR_UPDATE);
                break;
            case R.id.finished_manga_rl:
                intent = new Intent(getActivity(), CollectedActivity.class);
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
            case R.id.ad_rl:
                intent = new Intent(getActivity(), AdvertisingActivity.class);
                break;
            case R.id.share_rl:
                if (null != onShareAppClickListener) {
                    onShareAppClickListener.onClick();
                }
                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }

    public void setOnShareAppClickListener(OnShareAppClickListener onShareAppClickListener) {
        this.onShareAppClickListener = onShareAppClickListener;
    }
}
