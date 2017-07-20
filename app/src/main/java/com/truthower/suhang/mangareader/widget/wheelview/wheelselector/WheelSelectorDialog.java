package com.truthower.suhang.mangareader.widget.wheelview.wheelselector;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.widget.wheelview.WheelView;
import com.truthower.suhang.mangareader.widget.wheelview.adapters.ArrayWheelAdapter;


public class WheelSelectorDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private String selectedRes, selectedCodeRes, selectedTypeRes;
    private OnSingleSelectedListener onSingleSelectedListener;
    private TextView okBtn;
    private WheelView wheelView;
    private String[] options;
    private String[] optionTypes;
    private String[] optionCodes;
    // private int VISIBILITY_NUM = 5;
    // private int ITEM_TEXTSIZE = 19;
    // private int itemTextColor = Color.BLACK;
    private ArrayWheelAdapter<String> optionsAdapter;

    public WheelSelectorDialog(Context context) {
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
        setContentView(R.layout.view_single_selector);
        init();

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager wm = ((Activity) context).getWindowManager();
        Display d = wm.getDefaultDisplay(); // 闁兼儳鍢茶ぐ鍥╀沪韫囨挾顔庨悗鐟邦潟閿熸垝绶氶悵顕�鎮介敓锟�
        // lp.height = (int) (d.getHeight() * 0.4); // 濡ゅ倹锚鐎瑰磭鎷嬮崜褏鏋�
//        lp.width = (int) (d.getWidth() * 1); // 閻庣妫勭�瑰磭鎷嬮崜褏鏋�
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
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

        //
        window.setBackgroundDrawableResource(android.R.color.white);
    }

    // public void setDatas(String[] options, String[] optionCodes) {
    // this.options = options;
    // this.optionCodes = optionCodes;
    // }
    public void initOptionsData(String[] options) {
        initOptionsData(options, null, null);
    }

    public void initOptionsData(String[] options, String[] optionCodes) {
        initOptionsData(options, optionCodes, null);
    }

    public void initOptionsData(String[] options, String[] optionCodes, String[] optionTypes) {
        // 閻熸瑱绲鹃悗钘夘嚗濡わ拷閸╁矂鎯囨担鍝ヮ伋闁告牞妗ㄦ穱濠囧箒閿燂拷
        if (null == optionsAdapter) {
            optionsAdapter = new ArrayWheelAdapter<String>(context);
            optionsAdapter.setItemResource(R.layout.item_wheel_view);
            // optionsAdapter.setTextSize(ITEM_TEXTSIZE);
            // optionsAdapter.setTextColor(itemTextColor);
        }
        this.options = options;
        this.optionCodes = optionCodes;
        this.optionTypes = optionTypes;
        optionsAdapter.setItems(options);
        wheelView.setViewAdapter(optionsAdapter);
        // wheelView.setVisibleItems(VISIBILITY_NUM);
        wheelView.setCurrentItem(0);
    }

    private void init() {
        okBtn = (TextView) findViewById(R.id.btn_confirm);
        wheelView = (WheelView) findViewById(R.id.id_selector);

        // itemTextColor =
        // context.getResources().getColor(R.color.wheelview_text);

        okBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                selectedRes = options[wheelView.getCurrentItem()];
                if (null != optionCodes && optionCodes.length > 0) {
                    selectedCodeRes = optionCodes[wheelView.getCurrentItem()];
                }
                if (null != optionTypes && optionTypes.length > 0) {
                    selectedTypeRes = optionTypes[wheelView.getCurrentItem()];
                }

                if (null != onSingleSelectedListener) {
                    onSingleSelectedListener.onOkBtnClick(wheelView.getCurrentItem());
                }
                if (null != onSingleSelectedListener && !TextUtils.isEmpty(selectedRes)) {
                    onSingleSelectedListener.onOkBtnClick(selectedRes, selectedCodeRes);
                }
                if (null != onSingleSelectedListener && !TextUtils.isEmpty(selectedRes) && !TextUtils.isEmpty(selectedTypeRes)) {
                    onSingleSelectedListener.onOkBtnClick(selectedRes, selectedCodeRes, selectedTypeRes);
                }
                this.dismiss();
                break;
        }
    }

    public void setOnSingleSelectedListener(OnSingleSelectedListener onSingleSelectedListener) {
        this.onSingleSelectedListener = onSingleSelectedListener;
    }

    public interface OnSingleSelectedListener {
        void onOkBtnClick(String selectedRes, String selectedCodeRes);

        void onOkBtnClick(String selectedRes, String selectedCodeRes, String selectedTypeRes);

        void onOkBtnClick(int position);
    }
}
