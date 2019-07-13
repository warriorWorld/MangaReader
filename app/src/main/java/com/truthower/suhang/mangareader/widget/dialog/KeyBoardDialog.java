package com.truthower.suhang.mangareader.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.business.other.KeyboardSettingActivity;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.listener.OnKeyboardChangeListener;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.utils.VibratorUtil;
import com.truthower.suhang.mangareader.widget.button.GestureButton;


public class KeyBoardDialog extends Dialog implements View.OnClickListener, GestureButton.OnResultListener {
    private Context context;
    private GestureButton abcGb;
    private GestureButton defGb;
    private GestureButton ghiGb;
    private GestureButton jklGb;
    private GestureButton mnoGb;
    private GestureButton pqrsGb;
    private GestureButton tuvGb;
    private GestureButton wxyzGb;
    protected TextView finalResTv;
    private TextView resTv;
    private Button deleteBtn;
    private Button spaceBtn;
    private Button okBtn;
    private Button helpBtn;
    private ImageView crossIv;
    protected OnKeyboardChangeListener mOnKeyboardChangeListener;

    public KeyBoardDialog(Context context) {
        super(context);
        // 閺夆晜鐟ч～鎺楀棘閻熸壆纭�闁告瑯鍨禍鎺旀媼閳哄伅顓㈠触閹存繂寮块悘鐑囨嫹 濞达絽妫欏Σ鎼佸极閸喓浜☉鎾崇Т閵囧﹥绺介敓锟�
        // super(context, android.R.style.Theme);
        // setOwnerActivity((Activity) context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());
        init();

        windowSet();
    }

    protected int getLayoutId() {
        return R.layout.dialog_keyboard;
    }

    protected void windowSet() {
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager wm = ((Activity) context).getWindowManager();
        Display d = wm.getDefaultDisplay(); // 闁兼儳鍢茶ぐ鍥╀沪韫囨挾顔庨悗鐟邦潟閿熸垝绶氶悵顕�鎮介敓锟�
        // lp.height = (int) (d.getHeight() * 0.4); // 濡ゅ倹锚鐎瑰磭鎷嬮崜褏鏋�
        lp.width = (int) (d.getWidth() * 1); // 閻庣妫勭�瑰磭鎷嬮崜褏鏋�
        // window.setGravity(Gravity.LEFT | Gravity.TOP);
        window.setGravity(Gravity.BOTTOM);
        // dialog濮掓稒顭堥鑽や焊鏉堛劍绠抪adding 閻庝絻澹堥崵褎淇婇崒娑氫函濞戞挸绉风换鏍ㄧ▕閸綆鍟庣紓鍐惧枤濞堟垹鎷犻敓锟�
        // dialog婵ɑ鐡曠换娆愮▔瀹ュ牆鍘撮柛蹇嬪妼閻拷
        window.getDecorView().setPadding(0, 0, 0, 0);
        // lp.x = 100; // 闁哄倿顣︾紞鍛磾閻㈡棃宕搁幇顓犲灱
        // lp.y = 100; // 闁哄倿顣︾紞鍛磾閻㈡洟宕搁幇顓犲灱
        // lp.height = 30;
        // lp.width = 20;
        window.setAttributes(lp);
        window.setDimAmount(0.2f);//去掉蒙层
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    protected void init() {
        finalResTv = (TextView) findViewById(R.id.final_res_tv);
        abcGb = (GestureButton) findViewById(R.id.abc_gb);
        defGb = (GestureButton) findViewById(R.id.def_gb);
        ghiGb = (GestureButton) findViewById(R.id.ghi_gb);
        jklGb = (GestureButton) findViewById(R.id.jkl_gb);
        mnoGb = (GestureButton) findViewById(R.id.mno_gb);
        pqrsGb = (GestureButton) findViewById(R.id.pqrs_gb);
        tuvGb = (GestureButton) findViewById(R.id.tuv_gb);
        wxyzGb = (GestureButton) findViewById(R.id.wxyz_gb);
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData(context, ShareKeys.IS_OTHER_LETTER_ORDER, false)) {
            setupKeys();
        } else {
            setupKeys1();
        }
        deleteBtn = (Button) findViewById(R.id.delete_btn);
        spaceBtn = (Button) findViewById(R.id.space_btn);
        okBtn = (Button) findViewById(R.id.ok_btn);
        resTv = (TextView) findViewById(R.id.res_tv);
        helpBtn = (Button) findViewById(R.id.help_btn);
        crossIv = (ImageView) findViewById(R.id.cross_iv);

        helpBtn.setOnClickListener(this);
        abcGb.setOnResultListener(this);
        defGb.setOnResultListener(this);
        ghiGb.setOnResultListener(this);
        jklGb.setOnResultListener(this);
        mnoGb.setOnResultListener(this);
        pqrsGb.setOnResultListener(this);
        tuvGb.setOnResultListener(this);
        wxyzGb.setOnResultListener(this);
        deleteBtn.setOnClickListener(this);
        spaceBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
        crossIv.setOnClickListener(this);
        deleteBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                finalResTv.setText("");
                return true;
            }
        });
    }

    private void setupKeys() {
        abcGb.setKeys("bac");
        defGb.setKeys("edf");
        ghiGb.setKeys("hgi");
        jklGb.setKeys("kjl");
        mnoGb.setKeys("nmo");
        pqrsGb.setKeys("qprs");
        tuvGb.setKeys("utv");
        wxyzGb.setKeys("xwyz");
    }

    private void setupKeys1() {
        abcGb.setKeys("abc");
        defGb.setKeys("def");
        ghiGb.setKeys("ghi");
        jklGb.setKeys("jkl");
        mnoGb.setKeys("mno");
        pqrsGb.setKeys("pqrs");
        tuvGb.setKeys("tuv");
        wxyzGb.setKeys("wxyz");
    }

    private void showHelpDialog() {
        MangaDialog dialog = new MangaDialog(context);
        dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                Intent intent = new Intent(context, KeyboardSettingActivity.class);
                context.startActivity(intent);
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setTitle("教程");
        dialog.setMessage("1,按住键盘然后滑动到你想选择的字母然后松手即可输入" +
                "\n2,输入完成点击OK即可查单词" +
                "\n3,不想使用这个键盘可在设置中关闭");
        dialog.setCancelText("知道了");
        dialog.setOkText("键盘设置");
    }

    protected void onOkBtnClick() {
        if (null != mOnKeyboardChangeListener) {
            mOnKeyboardChangeListener.onFinish(finalResTv.getText().toString());
        }
        finalResTv.setText("");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.delete_btn:
                if (finalResTv.getText().toString().length() > 0) {
                    finalResTv.setText(finalResTv.getText().toString().substring(0, finalResTv.getText().length() - 1));
                }
                break;
            case R.id.ok_btn:
                onOkBtnClick();
                break;
            case R.id.space_btn:
                finalResTv.setText(finalResTv.getText().toString() + " ");
                break;
            case R.id.help_btn:
                showHelpDialog();
                break;
            case R.id.cross_iv:
                dismiss();
                break;
        }
    }

    @Override
    public void onResult(String result) {
        resTv.setText("");
        finalResTv.setText(finalResTv.getText().toString() + result);
    }

    @Override
    public void onChange(String result) {
        resTv.setText(result);
        if (null != mOnKeyboardChangeListener) {
            mOnKeyboardChangeListener.onChange(result);
        }
    }

    public void setOnKeyboardChangeListener(OnKeyboardChangeListener onKeyboardChangeListener) {
        mOnKeyboardChangeListener = onKeyboardChangeListener;
    }
}
