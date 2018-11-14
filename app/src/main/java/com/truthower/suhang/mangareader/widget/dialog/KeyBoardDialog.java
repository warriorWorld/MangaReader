package com.truthower.suhang.mangareader.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;


public class KeyBoardDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private RelativeLayout abcRl;
    private TextView aTv;
    private TextView bTv;
    private TextView cTv;
    private RelativeLayout defRl;
    private TextView dTv;
    private TextView eTv;
    private TextView fTv;
    private RelativeLayout ghiRl;
    private TextView gTv;
    private TextView hTv;
    private TextView iTv;
    private RelativeLayout jklRl;
    private TextView jTv;
    private TextView kTv;
    private TextView lTv;
    private RelativeLayout mnoRl;
    private TextView mTv;
    private TextView nTv;
    private TextView oTv;
    private RelativeLayout pqrsRl;
    private TextView pTv;
    private TextView qTv;
    private TextView rTv;
    private TextView sTv;
    private RelativeLayout tuvRl;
    private TextView tTv;
    private TextView uTv;
    private TextView vTv;
    private RelativeLayout wxyzRl;
    private TextView wTv;
    private TextView xTv;
    private TextView yTv;
    private TextView zTv;
    private Button deleteBtn;
    private Button spaceBtn;
    private Button okBtn;

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
        setContentView(R.layout.dialog_keyboard);
        init();

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

        window.setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void init() {
        abcRl = (RelativeLayout) findViewById(R.id.abc_rl);
        aTv = (TextView) findViewById(R.id.a_tv);
        bTv = (TextView) findViewById(R.id.b_tv);
        cTv = (TextView) findViewById(R.id.c_tv);
        defRl = (RelativeLayout) findViewById(R.id.def_rl);
        dTv = (TextView) findViewById(R.id.d_tv);
        eTv = (TextView) findViewById(R.id.e_tv);
        fTv = (TextView) findViewById(R.id.f_tv);
        ghiRl = (RelativeLayout) findViewById(R.id.ghi_rl);
        gTv = (TextView) findViewById(R.id.g_tv);
        hTv = (TextView) findViewById(R.id.h_tv);
        iTv = (TextView) findViewById(R.id.i_tv);
        jklRl = (RelativeLayout) findViewById(R.id.jkl_rl);
        jTv = (TextView) findViewById(R.id.j_tv);
        kTv = (TextView) findViewById(R.id.k_tv);
        lTv = (TextView) findViewById(R.id.l_tv);
        mnoRl = (RelativeLayout) findViewById(R.id.mno_rl);
        mTv = (TextView) findViewById(R.id.m_tv);
        nTv = (TextView) findViewById(R.id.n_tv);
        oTv = (TextView) findViewById(R.id.o_tv);
        pqrsRl = (RelativeLayout) findViewById(R.id.pqrs_rl);
        pTv = (TextView) findViewById(R.id.p_tv);
        qTv = (TextView) findViewById(R.id.q_tv);
        rTv = (TextView) findViewById(R.id.r_tv);
        sTv = (TextView) findViewById(R.id.s_tv);
        tuvRl = (RelativeLayout) findViewById(R.id.tuv_rl);
        tTv = (TextView) findViewById(R.id.t_tv);
        uTv = (TextView) findViewById(R.id.u_tv);
        vTv = (TextView) findViewById(R.id.v_tv);
        wxyzRl = (RelativeLayout) findViewById(R.id.wxyz_rl);
        wTv = (TextView) findViewById(R.id.w_tv);
        xTv = (TextView) findViewById(R.id.x_tv);
        yTv = (TextView) findViewById(R.id.y_tv);
        zTv = (TextView) findViewById(R.id.z_tv);
        deleteBtn = (Button) findViewById(R.id.delete_btn);
        spaceBtn = (Button) findViewById(R.id.space_btn);
        okBtn = (Button) findViewById(R.id.ok_btn);

        abcRl.setOnClickListener(this);
        defRl.setOnClickListener(this);
        ghiRl.setOnClickListener(this);
        jklRl.setOnClickListener(this);
        pqrsRl.setOnClickListener(this);
        mnoRl.setOnClickListener(this);
        tuvRl.setOnClickListener(this);
        wxyzRl.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        spaceBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        switch (view.getId()) {
        }
    }
}
