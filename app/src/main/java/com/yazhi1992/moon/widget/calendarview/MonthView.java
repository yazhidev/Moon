package com.yazhi1992.moon.widget.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yazhi1992.moon.R;
import com.yazhi1992.yazhilib.utils.LibUtils;

import java.util.List;

/**
 * Created by zengyazhi on 2018/3/21.
 */

public class MonthView extends ViewGroup {

    private static final int MAXROW = 6;
    private static final int COLUMN = 7; //7列，周一到周日
    private int mChildWidth; //每个日期的宽高
    private int currentMonthDays;//记录当月天数
    private int lastMonthDays;//记录当月显示的上个月天数
    private int nextMonthDays;//记录当月显示的下个月天数
    private Context mContext;

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setBackgroundColor(Color.GRAY);
    }

    /**
     * @param dates            需要展示的日期数据
     * @param currentMonthDays 当月天数
     */
    public void setDateList(List<DateBean> dates, int currentMonthDays) {
        if (getChildCount() > 0) {
            removeAllViews();
        }

        this.currentMonthDays = currentMonthDays;
        for (int i = 0; i < dates.size(); i++) {
            final DateBean date = dates.get(i);
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_month_layout, null);
            TextView tv = view.findViewById(R.id.solar_day);
            if(date.getType() == 1) {
                tv.setText(String.valueOf(date.getDate()[2]));
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LibUtils.showToast(String.valueOf(date.getDate()[2]));
                    }
                });
            }
            addView(view, i);
        }
//        for (int i = 0; i < dates.size(); i++) {
//
//            if (date.getType() == 0) {
//                lastMonthDays++;
//                if (!mAttrsBean.isShowLastNext()) {
//                    addView(new View(mContext), i);
//                    continue;
//                }
//            }
//
//            if (date.getType() == 2) {
//                nextMonthDays++;
//                if (!mAttrsBean.isShowLastNext()) {
//                    addView(new View(mContext), i);
//                    continue;
//                }
//            }
//
//            View view;
//            TextView solarDay;//阳历TextView
//
//            view = LayoutInflater.from(mContext).inflate(R.layout.item_month_layout, null);
//            solarDay = (TextView) view.findViewById(R.id.solar_day);
//
//            solarDay.setTextColor(mAttrsBean.getColorSolar());
//            solarDay.setTextSize(mAttrsBean.getSizeSolar());
//
//            //设置上个月和下个月的阳历颜色
//            if (date.getType() == 0 || date.getType() == 2) {
//                solarDay.setTextColor(mAttrsBean.getColorLunar());
//            }
//            solarDay.setText(String.valueOf(date.getSolar()[2]));
//
//
//            //找到单选时默认选中的日期，并选中（如果有）
//            if (mAttrsBean.getChooseType() == 0
//                    && mAttrsBean.getSingleDate() != null
//                    && !findSingleDate
//                    && date.getType() == 1
//                    && mAttrsBean.getSingleDate()[0] == date.getSolar()[0]
//                    && mAttrsBean.getSingleDate()[1] == date.getSolar()[1]
//                    && mAttrsBean.getSingleDate()[2] == date.getSolar()[2]) {
//                lastClickedView = view;
//                setDayColor(view, COLOR_SET);
//                findSingleDate = true;
//            }
//
//            //找到多选时默认选中的多个日期，并选中（如果有）
//            if (mAttrsBean.getChooseType() == 1 && mAttrsBean.getMultiDates() != null) {
//                for (int[] d : mAttrsBean.getMultiDates()) {
//                    if (date.getType() == 1
//                            && d[0] == date.getSolar()[0]
//                            && d[1] == date.getSolar()[1]
//                            && d[2] == date.getSolar()[2]) {
//                        setDayColor(view, COLOR_SET);
//                        chooseDays.add(d[2]);
//                        break;
//                    }
//                }
//            }
//
//            //设置禁用日期
//            if (date.getType() == 1) {
//                view.setTag(date.getSolar()[2]);
//                if (mAttrsBean.getDisableStartDate() != null
//                        && (CalendarUtil.dateToMillis(mAttrsBean.getDisableStartDate()) > CalendarUtil.dateToMillis(date.getSolar()))) {
//                    solarDay.setTextColor(mAttrsBean.getColorLunar());
//                    lunarDay.setTextColor(mAttrsBean.getColorLunar());
//                    view.setTag(-1);
//                    addView(view, i);
//                    continue;
//                }
//
//                if (mAttrsBean.getDisableEndDate() != null
//                        && (CalendarUtil.dateToMillis(mAttrsBean.getDisableEndDate()) < CalendarUtil.dateToMillis(date.getSolar()))) {
//                    solarDay.setTextColor(mAttrsBean.getColorLunar());
//                    lunarDay.setTextColor(mAttrsBean.getColorLunar());
//                    view.setTag(-1);
//                    addView(view, i);
//                    continue;
//                }
//            }
//
//            view.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int day = date.getSolar()[2];
//                    CalendarView calendarView = (CalendarView) getParent();
//                    OnSingleChooseListener clickListener = calendarView.getSingleChooseListener();
//                    OnMultiChooseListener chooseListener = calendarView.getMultiChooseListener();
//                    if (date.getType() == 1) {//点击当月
//                        if (mAttrsBean.getChooseType() == 1 && chooseListener != null) {//多选的情况
//                            boolean flag;
//                            if (chooseDays.contains(day)) {
//                                setDayColor(v, COLOR_RESET);
//                                chooseDays.remove(day);
//                                flag = false;
//                            } else {
//                                setDayColor(v, COLOR_SET);
//                                chooseDays.add(day);
//                                flag = true;
//                            }
//                            calendarView.setChooseDate(day, flag, -1);
//                            chooseListener.onMultiChoose(v, date, flag);
//                        } else {
//                            calendarView.setLastClickDay(day);
//                            if (lastClickedView != null) {
//                                setDayColor(lastClickedView, COLOR_RESET);
//                            }
//                            setDayColor(v, COLOR_SET);
//                            lastClickedView = v;
//
//                            if (clickListener != null) {
//                                clickListener.onSingleChoose(v, date);
//                            }
//                        }
//                    } else if (date.getType() == 0) {//点击上月
//                        if (mAttrsBean.isSwitchChoose()) {
//                            calendarView.setLastClickDay(day);
//                        }
//                        calendarView.lastMonth();
//                        if (clickListener != null) {
//                            clickListener.onSingleChoose(v, date);
//                        }
//                    } else if (date.getType() == 2) {//点击下月
//                        if (mAttrsBean.isSwitchChoose()) {
//                            calendarView.setLastClickDay(day);
//                        }
//                        calendarView.nextMonth();
//                        if (clickListener != null) {
//                            clickListener.onSingleChoose(v, date);
//                        }
//                    }
//                }
//            });
//            addView(view, i);
//        }
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.e("zyz", "onMeasure");

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int itemWidth = widthSpecSize / COLUMN;

        //计算日历的最大高度
        if (heightSpecSize > itemWidth * MAXROW) {
            heightSpecSize = itemWidth * MAXROW;
        }

        setMeasuredDimension(widthSpecSize, heightSpecSize);

        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            childView.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() == 0) {
            return;
        }

        Log.e("zyz", "onLayout");

        if(mChildWidth == 0) {
            mChildWidth = getMeasuredWidth() / COLUMN;
        }

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);

            int left = i % COLUMN * mChildWidth;
            int top = i / COLUMN * mChildWidth;
            int right = left + mChildWidth;
            int bottom = top + mChildWidth;
            view.layout(left, top, right, bottom);
        }
    }
}
