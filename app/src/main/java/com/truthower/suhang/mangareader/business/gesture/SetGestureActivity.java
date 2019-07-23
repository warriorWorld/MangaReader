package com.truthower.suhang.mangareader.business.gesture;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.GestureGridAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.utils.ThreeDESUtil;
import com.truthower.suhang.mangareader.widget.dialog.SingleLoadBarUtil;
import com.truthower.suhang.mangareader.widget.gesture.GestureLockViewGroup;

import java.util.ArrayList;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 个人信息页
 */
public class SetGestureActivity extends BaseActivity implements View.OnClickListener {
    private GestureLockViewGroup mGestureLockViewGroup;
    private TextView explainTv;
    private int drawNum = 0;//绘制次数
    private String lastAnswer = "";
    private RecyclerView gestureRv;
    private GestureGridAdapter adapter;
    private ArrayList<Boolean> gestureList = new ArrayList<>();
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(this))) {
            this.finish();
        }
        initUI();
    }

    private void initUI() {
        gestureRv = (RecyclerView) findViewById(R.id.gesture_rv);
        gestureRv.setLayoutManager(new GridLayoutManager(this, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
        gestureRv.setFocusableInTouchMode(false);
        gestureRv.setFocusable(false);
        gestureRv.setHasFixedSize(true);
        mGestureLockViewGroup = (GestureLockViewGroup) findViewById(R.id.id_gestureLockViewGroup);
        explainTv = (TextView) findViewById(R.id.gesture_explain);
        mGestureLockViewGroup.setAnswer(new int[]{1, 2, 3, 4, 5});
        mGestureLockViewGroup
                .setOnGestureLockViewListener(new GestureLockViewGroup.OnGestureLockViewListener() {

                    @Override
                    public void onUnmatchedExceedBoundary() {
                    }

                    @Override
                    public void onGestureEvent(boolean matched) {
                    }

                    @Override
                    public void onGestureEvent(String choose) {
                        resetState(choose);
                    }

                    @Override
                    public void onBlockSelected(int cId) {
                    }
                });
        for (int i = 0; i < 9; i++) {
            gestureList.add(false);
        }
        initGridView();
        baseTopBar.setTitle("设置儿童锁");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_gesture;
    }

    private void initGridView() {
        try {
            if (null == adapter) {
                adapter = new GestureGridAdapter(this);
                adapter.setDatas(gestureList);
                gestureRv.setAdapter(adapter);
            } else {
                adapter.setDatas(gestureList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doSetGesture() {
        String userName = LoginBean.getInstance().getUserName(this);
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(this);

        AVQuery<AVObject> query = new AVQuery<>("Gesture");
        query.whereEqualTo("owner", userName);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject account, AVException e) {
                if (null == account) {
                    //第一次设置
                    AVObject object = new AVObject("Gesture");
                    object.put("owner", LoginBean.getInstance().getUserName(SetGestureActivity.this));
                    object.put("password", ThreeDESUtil.encode(Configure.key, password));
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            SingleLoadBarUtil.getInstance().dismissLoadBar();
                            if (LeanCloundUtil.handleLeanResult(SetGestureActivity.this, e)) {
                                baseToast.showToast("设置成功");
                                finish();
                            }
                        }
                    });
                } else {
                    account.put("password", ThreeDESUtil.encode(Configure.key, password));
                    account.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (LeanCloundUtil.handleLeanResult(SetGestureActivity.this, e)) {
                                baseToast.showToast("设置成功");
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    private void resetState(String choose) {
        if (choose.length() < 4) {
            baseToast.showToast("至少需要连接四个点");
            setGestureError();
            return;
        }
        gestureList.clear();
        for (int i = 0; i < 9; i++) {
            gestureList.add(false);
        }
        String[] chooses = choose.split("");
        for (int i = 0; i < chooses.length; i++) {
            try {
                int item = Integer.valueOf(chooses[i]);
                gestureList.set(item, true);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        initGridView();
        mGestureLockViewGroup.reset();
        switch (drawNum) {
            case 0:
                lastAnswer = choose;
                drawNum++;
                explainTv.setText("请再次绘制手势密码");
                break;
            case 1:
                if (!choose.equals(lastAnswer)) {
                    explainTv.setText("两次绘制的图案不一致,请重新设置!");
                    gestureList.clear();
                    for (int i = 0; i < 9; i++) {
                        gestureList.add(false);
                    }
                    initGridView();
                    drawNum = 0;
                } else {
                    // 请求接口
                    password = choose;
                    doSetGesture();
                }
                break;
        }
    }

    private void setGestureError() {
        mGestureLockViewGroup.setErrorMode();
        mGestureLockViewGroup.invalidate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mGestureLockViewGroup.reset();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
