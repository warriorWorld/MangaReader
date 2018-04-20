package com.truthower.suhang.mangareader.widget.layout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.listener.OnCalendarMonthChangeClickListener;
import com.truthower.suhang.mangareader.utils.NumberUtil;


public class CalendarViewLayout extends LinearLayout implements View.OnClickListener {
    private Context context;
    private CalendarView calendarView;
    private TextView selectedDateTv;
    private RelativeLayout toLastMonthRl;
    private RelativeLayout toNextMonthRl;
    private LinearLayout toggleLl;
    private ImageView toggleCalendarIv;
    private LinearLayout calendarLl;

    private int calendarHeight;
    private ValueAnimator animator;
    private OnCalendarMonthChangeClickListener onCalendarMonthChangeClickListener;

    public CalendarViewLayout(Context context) {
        this(context, null);
    }

    public CalendarViewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.view_calendar_layout, this);
        calendarView = (CalendarView) findViewById(R.id.calendar_cv);
        selectedDateTv = (TextView) findViewById(R.id.selected_date_tv);
        toLastMonthRl = (RelativeLayout) findViewById(R.id.to_last_month_rl);
        toNextMonthRl = (RelativeLayout) findViewById(R.id.to_next_month_rl);
        toggleLl = (LinearLayout) findViewById(R.id.toggle_ll);
        toggleCalendarIv = (ImageView) findViewById(R.id.toggle_calendar_iv);
        calendarLl = (LinearLayout) findViewById(R.id.calendar_ll);
//        calendarEll.setAnimDuration(250);
//        calendarEll.setExllHeight(300);

        toLastMonthRl.setOnClickListener(this);
        toNextMonthRl.setOnClickListener(this);
        toggleLl.setOnClickListener(this);
    }


    public void setSelecties(int[] selectes) {
        calendarView.setSelected(selectes);
    }


    private void toggleExpandLayout() {
        if (null == animator) {
            return;
        }
        if (calendarLl.getHeight() > 0) {
            animator.start();
        } else {
            animator.reverse();
        }
    }

    public void setCurrentMonth(int year, int month) {
        refresh(year, month);
    }

    public int getCurrentMonth() {
        return calendarView.getCurrentMonth();
    }

    public int getCurrentYear() {
        return calendarView.getCurrentYear();
    }

    private void refresh(int year, int month) {
        calendarView.setCurrentMonth(year, month);
        selectedDateTv.setText(year + "年" + NumberUtil.toDoubleNum(month) + "月");
        //TODO 重新设置选择
        calendarLl.post(new Runnable() {
            @Override
            public void run() {
                calendarHeight = calendarLl.getHeight();
                initAnim();
            }
        });
    }


    private void initAnim() {
        if (calendarHeight == 0)
            return;
        animator = ValueAnimator.ofInt(calendarHeight, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) calendarLl.getLayoutParams();
                lp.height = (int) animation.getAnimatedValue();
                calendarLl.setLayoutParams(lp);

                float f = animation.getAnimatedFraction();
                toggleCalendarIv.setRotation(f * 180);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public int getMaxDay() {
        return calendarView.getMaxDay();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_ll:
                toggleExpandLayout();
                break;
            case R.id.to_last_month_rl:
                int year = calendarView.getCurrentYear();
                int month = calendarView.getCurrentMonth();
                if (month <= 1) {
                    year--;
                    month = 12;
                } else {
                    month--;
                }
                refresh(year, month);
                if (null != onCalendarMonthChangeClickListener) {
                    onCalendarMonthChangeClickListener.onChange(year, month);
                }
                break;
            case R.id.to_next_month_rl:
                int year1 = calendarView.getCurrentYear();
                int month1 = calendarView.getCurrentMonth();
                if (month1 >= 12) {
                    year1++;
                    month1 = 1;
                } else {
                    month1++;
                }
                refresh(year1, month1);
                if (null != onCalendarMonthChangeClickListener) {
                    onCalendarMonthChangeClickListener.onChange(year1, month1);
                }
                break;
        }
    }

    public void setOnCalendarMonthChangeClickListener(OnCalendarMonthChangeClickListener onCalendarMonthChangeClickListener) {
        this.onCalendarMonthChangeClickListener = onCalendarMonthChangeClickListener;
    }
}
