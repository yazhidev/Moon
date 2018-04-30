package com.yazhi1992.moon.widget.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.event.OnMcStatusChanged;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by zengyazhi on 2018/3/21.
 */

public class MonthView extends ViewGroup {

    public static final int MAXROW = 6;
    public static final int COLUMN = 7; //7列，周一到周日
    private int mChildWidth; //每个日期的宽高
    public static final double HEIGHT_SIZE = 0.9; //高度是宽度的0.8
    private int mChildHeight; //每个日期的高度
    private int currentMonthDays;//记录当月天数
    private int lastMonthDays;//记录当月显示的上个月天数
    private int nextMonthDays;//记录当月显示的下个月天数
    private Context mContext;
    List<DateBean> dates;
    private int year;
    private int month;
    private boolean mClickble;

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setBackgroundColor(Color.parseColor("#efefef"));
    }

    public MonthView(boolean clickble, Context context) {
        super(context);
        mClickble = clickble;
    }

    public void setBuildInfo(int year, int month, int currentMonthDays) {
        this.year = year;
        this.month = month;
        this.currentMonthDays = currentMonthDays;
        startBuildData();
    }

    public void startBuildData() {
        CalendarHelper.getInstance().buildMonthData(year, month, new DataCallback<List<DateBean>>() {
            @Override
            public void onSuccess(List<DateBean> data) {
                setDateList(data, currentMonthDays);
            }

            @Override
            public void onFailed(int code, String msg) {
                LibUtils.showToast(msg);
            }
        });
    }

    public void setClickble(boolean clickble) {
        mClickble = clickble;
    }

    /**
     * @param dates            需要展示的日期数据
     * @param currentMonthDays 当月天数
     */
    public void setDateList(List<DateBean> dates, int currentMonthDays) {
        this.dates = dates;

        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                DayView childAt = (DayView) getChildAt(i);
                childAt.setDataBean(dates.get(i));
                childAt.postInvalidate();
            }
            EventBus.getDefault().post(new OnMcStatusChanged());
            return;
        }

        int[] currentDate = CalendarUtil.getCurrentDate();

        this.currentMonthDays = currentMonthDays;
        for (int i = 0; i < dates.size(); i++) {
            final DateBean date = dates.get(i);
            DayView view = new DayView(mContext);
            if (date.getType() == 1) {
                view.setTag(date.getDate()[2]);
                int finalI = i;
                if (currentDate[0] == date.getDate()[0]
                        && currentDate[1] == date.getDate()[1]
                        && currentDate[2] == date.getDate()[2]) {
                    date.setToday(true);
                }
                view.setDataBean(date);
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mClickble) return;

                        if (mOnSingleChooseListener != null) {
                            mOnSingleChooseListener.onSingleChoose(v, date, finalI);
                        }
                        Calendarview parent = (Calendarview) getParent();
                        OnSingleChooseListener singleChooseListener = parent.getSingleChooseListener();
                        if (singleChooseListener != null) {
                            singleChooseListener.onSingleChoose(v, date, finalI);
                        }
                    }
                });
            }
            addView(view, i);
        }
        requestLayout();
    }

    private OnSingleChooseListener mOnSingleChooseListener;

    public void setOnSingleChooseListener(OnSingleChooseListener onSingleChooseListener) {
        mOnSingleChooseListener = onSingleChooseListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int itemWidth = widthSpecSize / COLUMN;

        //计算日历的最大高度
        if (heightSpecSize > itemWidth * MAXROW) {
            heightSpecSize = (int) (itemWidth * MAXROW * HEIGHT_SIZE);
        }

        setMeasuredDimension(widthSpecSize, heightSpecSize);

        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            childView.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY));
        }
    }

    public int getChildHeight() {
        return mChildHeight;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() == 0) {
            return;
        }

        if (mChildWidth == 0) {
            mChildWidth = getMeasuredWidth() / COLUMN;
            mChildHeight = (int) (mChildWidth * HEIGHT_SIZE);
        }

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);

            int left = i % COLUMN * mChildWidth;
            int top = i / COLUMN * mChildHeight;
            int right = left + mChildWidth;
            int bottom = top + mChildHeight;
            view.layout(left, top, right, bottom);
        }
    }

    public void fresh(DateBean date) {
        DayView destView = (DayView) findDestView(date.getDate()[2]);
        freshView(destView, date);
    }

    private void freshView(DayView destView, DateBean date) {
        destView.setDataBean(date);
    }

    /**
     * 查找要跳转到的页面需要展示的日期View
     *
     * @param day
     * @return
     */
    private View findDestView(int day) {
        View view = null;
        for (int i = lastMonthDays; i < getChildCount() - nextMonthDays; i++) {
            if (getChildAt(i).getTag() != null && (Integer) getChildAt(i).getTag() == day) {
                view = getChildAt(i);
                break;
            }
        }

        if (view == null) {
            view = getChildAt(currentMonthDays + lastMonthDays - 1);
        }

        if ((Integer) view.getTag() == -1) {
            view = null;
        }
        return view;
    }
}
