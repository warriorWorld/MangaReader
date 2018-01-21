package com.truthower.suhang.mangareader.widget.dialog;/**
 * Created by Administrator on 2016/11/4.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OnlyTextListAdapter;
import com.truthower.suhang.mangareader.listener.OnSevenFourteenListDialogListener;


/**
 * 作者：苏航 on 2016/11/4 11:08
 * 邮箱：772192594@qq.com
 */
public class ListDialog extends Dialog implements View.OnClickListener{
    private Context context;
    private ListView optionsLv;
    private TextView cancelTv;
    private OnlyTextListAdapter onlyTextListAdapter;
    private String[] list;
    private String[] codeList;
    private OnSevenFourteenListDialogListener onSevenFourteenListDialogListener;

    public ListDialog(Context context) {
        super(context);
        // 閺夆晜鐟ч～鎺楀棘閻熸壆纭�闁告瑯鍨禍鎺旀媼閳哄伅顓㈠触閹存繂寮块悘鐑囨嫹 濞达絽妫欏Σ鎼佸极閸喓浜☉鎾崇Т閵囧﹥绺介敓锟�
        // super(context, android.R.style.Theme);
        // setOwnerActivity((Activity) context);
        this.context = context;
    }

    public ListDialog(Context context, String[] list, String[] codeList) {
        super(context);
        this.context = context;
        this.list = list;
        this.codeList = codeList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_list);
        init();
        initListView();

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
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }


    private void init() {
        optionsLv = (ListView) findViewById(R.id.options_lv);
        cancelTv = (TextView) findViewById(R.id.cancel_tv);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setOptionsList(String[] list) {
        this.list = list;
        initListView();
    }

    public void setCodeList(String[] codeList) {
        this.codeList = codeList;
    }

    private void initListView() {
        if (null == onlyTextListAdapter) {
            onlyTextListAdapter = new OnlyTextListAdapter(
                    context, list);
            optionsLv.setAdapter(onlyTextListAdapter);
            optionsLv.setFocusable(true);
            optionsLv.setFocusableInTouchMode(true);
            optionsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (null != onSevenFourteenListDialogListener) {
                        if (null != list && list.length > 0) {
                            onSevenFourteenListDialogListener.onItemClick(position);
                            onSevenFourteenListDialogListener.onItemClick(list[position]);
                            if (null != codeList && codeList.length > 0) {
                                onSevenFourteenListDialogListener.onItemClick(list[position], codeList[position]);
                            }
                            dismiss();
                        }
                    }
                }
            });
        } else {
            onlyTextListAdapter.setList(list);
            onlyTextListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                break;
        }
    }

    public void setOnSevenFourteenListDialogListener(OnSevenFourteenListDialogListener onSevenFourteenListDialogListener) {
        this.onSevenFourteenListDialogListener = onSevenFourteenListDialogListener;
    }
}
