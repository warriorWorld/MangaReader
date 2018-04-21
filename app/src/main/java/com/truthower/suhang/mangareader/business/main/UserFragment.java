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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.business.ad.AdvertisingActivity;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.business.download.DownloadActivity;
import com.truthower.suhang.mangareader.business.other.AboutActivity;
import com.truthower.suhang.mangareader.business.statistics.StatisticsActivity;
import com.truthower.suhang.mangareader.business.tag.TagFilterActivity;
import com.truthower.suhang.mangareader.business.user.CollectedActivity;
import com.truthower.suhang.mangareader.business.user.LoginActivity;
import com.truthower.suhang.mangareader.business.user.UserCenterActivity;
import com.truthower.suhang.mangareader.business.wordsbook.WordsBookActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.listener.OnShareAppClickListener;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.dialog.QrDialog;
import com.truthower.suhang.mangareader.widget.imageview.CircleImage;
import com.umeng.analytics.MobclickAgent;

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
    private TextView user_center_explain;
    private TextView user_msg_tip_tv;
    private int msgCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        initUI(v);
        refreshUI();
        toggleLoginStateUI();
        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(getActivity(), ShareKeys.CLOSE_TUTORIAL, true)) {
            MangaDialog dialog = new MangaDialog(getActivity());
            dialog.show();
            dialog.setTitle("教程");
            dialog.setMessage("1,点击分享App,可以打开该App的下载二维码,让你想分享的人扫描即可下载,也可将该二维码保存(会保存在manga文件夹中)然后发给你想分享的人." +
                    "\n2,想要更新App可以进入设置页检查更新下载最新App" +
                    "\n3,退出登录可以进入设置页退出" +
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
                doGetMsgCount();
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
        user_center_explain = (TextView) v.findViewById(R.id.user_center_explain);
        aboutRl = (RelativeLayout) v.findViewById(R.id.about_rl);
        shareRl = (RelativeLayout) v.findViewById(R.id.share_rl);
        adRl = (RelativeLayout) v.findViewById(R.id.ad_rl);
        finishedMangaRl = (RelativeLayout) v.findViewById(R.id.finished_manga_rl);
        userNameTv = (TextView) v.findViewById(R.id.user_name_tv);
        user_msg_tip_tv = (TextView) v.findViewById(R.id.user_msg_tip_tv);

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
            user_center_explain.setVisibility(View.GONE);
            user_msg_tip_tv.setVisibility(View.GONE);
        } else {
            if (LoginBean.getInstance().isMaster()) {
                userNameTv.setTextColor(getResources().getColor(R.color.master));
            } else {
                userNameTv.setTextColor(getResources().getColor(R.color.white));
            }
            user_center_explain.setVisibility(View.VISIBLE);
            userNameTv.setText(LoginBean.getInstance().getUserName());
        }
    }

    private void doGetMsgCount() {
        AVQuery<AVObject> query = new AVQuery<>("Comment");
        query.whereEqualTo("reply_user", LoginBean.getInstance().getUserName());
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (LeanCloundUtil.handleLeanResult(getActivity(), e)) {
                    msgCount = i;
                    int newMsgCount = i - SharedPreferencesUtils.getIntSharedPreferencesData(getActivity(), ShareKeys.READ_COMMENT_COUNT_KEY);
                    if (newMsgCount > 0) {
                        user_msg_tip_tv.setText(newMsgCount + "");
                        user_msg_tip_tv.setVisibility(View.VISIBLE);
                    } else {
                        user_msg_tip_tv.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.collect_rl:
                MobclickAgent.onEvent(getActivity(), "collect_type_collect");
                intent = new Intent(getActivity(), CollectedActivity.class);
                intent.putExtra("collectType", Configure.COLLECT_TYPE_COLLECT);
                break;
            case R.id.waiting_for_update_rl:
                MobclickAgent.onEvent(getActivity(), "collect_type_wait_for_update");
                intent = new Intent(getActivity(), CollectedActivity.class);
                intent.putExtra("collectType", Configure.COLLECT_TYPE_WAIT_FOR_UPDATE);
                break;
            case R.id.finished_manga_rl:
                MobclickAgent.onEvent(getActivity(), "collect_type_finished");
                intent = new Intent(getActivity(), CollectedActivity.class);
                intent.putExtra("collectType", Configure.COLLECT_TYPE_FINISHED);
                break;
            case R.id.statistics_rl:
                intent = new Intent(getActivity(), StatisticsActivity.class);
                MobclickAgent.onEvent(getActivity(), "statistics");
                break;
            case R.id.new_word_book_rl:
                intent = new Intent(getActivity(), WordsBookActivity.class);
                MobclickAgent.onEvent(getActivity(), "new_word_book");
                break;
            case R.id.download_rl:
                MobclickAgent.onEvent(getActivity(), "download");
                intent = new Intent(getActivity(), DownloadActivity.class);
                break;
            case R.id.user_top_bar_rl:
                intent = new Intent(getActivity(), UserCenterActivity.class);
                SharedPreferencesUtils.setSharedPreferencesData(getActivity(),ShareKeys.READ_COMMENT_COUNT_KEY,msgCount);
                intent.putExtra("owner", LoginBean.getInstance().getUserName(getActivity()));
                break;
            case R.id.about_rl:
                intent = new Intent(getActivity(), AboutActivity.class);
                break;
            case R.id.ad_rl:
                MobclickAgent.onEvent(getActivity(), "ad");
                intent = new Intent(getActivity(), AdvertisingActivity.class);
                break;
            case R.id.share_rl:
                MobclickAgent.onEvent(getActivity(), "share");
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
