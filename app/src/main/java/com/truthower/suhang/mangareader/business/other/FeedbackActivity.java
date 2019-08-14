package com.truthower.suhang.mangareader.business.other;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;

/**
 * 个人信息页
 */
public class FeedbackActivity extends BaseActivity implements View.OnClickListener {
    private EditText feedbackEt;
    private Button okBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        feedbackEt = (EditText) findViewById(R.id.feedback_et);
        okBtn = (Button) findViewById(R.id.ok_btn);

        okBtn.setOnClickListener(this);
        baseTopBar.setTitle("意见反馈");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
    }

    private void doSubmitFeedback() {
//        AVObject object = new AVObject("Feedback");
//        object.put("owner", LoginBean.getInstance().getUserName());
//        object.put("feedback", feedbackEt.getText().toString().trim());
//        object.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(AVException e) {
//                if (LeanCloundUtil.handleLeanResult(FeedbackActivity.this, e)) {
//                    MangaDialog dialog = new MangaDialog(FeedbackActivity.this);
//                    dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
//                        @Override
//                        public void onOkClick() {
//                            FeedbackActivity.this.finish();
//                        }
//
//                        @Override
//                        public void onCancelClick() {
//
//                        }
//                    });
//                    dialog.show();
//                    dialog.setTitle("谢谢支持");
//                    dialog.setMessage("反馈已提交");
//                    dialog.setOkText("确定");
//                }
//            }
//        });
    }

    private boolean checkData() {
        if (TextUtils.isEmpty(feedbackEt.getText().toString().trim())) {
            baseToast.showToast("请输入意见或建议");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn:
                if (checkData()) {
                    doSubmitFeedback();
                }
                break;
        }
    }
}
