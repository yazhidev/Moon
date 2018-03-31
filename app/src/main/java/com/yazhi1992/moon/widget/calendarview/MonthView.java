package com.yazhi1992.moon.widget.calendarview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
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

    public static final int MAXROW = 6;
    public static final int COLUMN = 7; //7列，周一到周日
    private int mChildWidth; //每个日期的宽高
    private int currentMonthDays;//记录当月天数
    private int lastMonthDays;//记录当月显示的上个月天数
    private int nextMonthDays;//记录当月显示的下个月天数
    private Context mContext;
    List<DateBean> dates;
    private DateBean clickDate;
    private View lastClickedView;//记录上次点击的Item

    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setBackgroundColor(Color.WHITE);
    }

    /**
     * @param dates            需要展示的日期数据
     * @param currentMonthDays 当月天数
     */
    public void setDateList(List<DateBean> dates, int currentMonthDays) {
        this.dates = dates;


        if (getChildCount() > 0) {
            removeAllViews();
        }

        int[] currentDate = CalendarUtil.getCurrentDate();

        this.currentMonthDays = currentMonthDays;
        for (int i = 0; i < dates.size(); i++) {
            final DateBean date = dates.get(i);
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_month_layout, null);

            freshView(view, date.getMcType());

            TextView tv = view.findViewById(R.id.solar_day);

            if (date.getType() == 1) {
                view.setTag(date.getDate()[2]);
                tv.setText(String.valueOf(date.getDate()[2]));
                int finalI = i;
                if(currentDate[0] == date.getDate()[0]
                        &&currentDate[1] == date.getDate()[1]
                        &&currentDate[2] == date.getDate()[2]) {
//                if(currentDate == date.getDate()) {
                    tv.setTextColor(Color.RED);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(lastClickedView != null && date.getMcType() == DateBean.NORMAL) {
                            freshView(lastClickedView, DateBean.NORMAL);
                        }
                        freshView(v, DateBean.CLICKED);
                        lastClickedView = v;

                        Calendarview parent = (Calendarview) getParent();
                        OnSingleChooseListener singleChooseListener = parent.getSingleChooseListener();
                        if(singleChooseListener != null) {
                            singleChooseListener.onSingleChoose(v, date, finalI);
                        }
                        LibUtils.showToast(String.valueOf(date.getDate()[2]));
                    }
                });
            }
            addView(view, i);
        }
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

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

    public int getChildWidth() {
        return mChildWidth;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() == 0) {
            return;
        }

        if (mChildWidth == 0) {
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

    public void fresh(DateBean date) {
        View destView = findDestView(date.getDate()[2]);
        freshView(destView, date.getMcType());
    }

    private void freshView(View destView, @DateBean.VIEW_TYPE int type) {
        switch (type) {
            case DateBean.MC_COME:
                destView.setBackgroundColor(Color.RED);
                break;
            case DateBean.MC_GO:
                destView.setBackgroundColor(Color.GRAY);
                break;
            case DateBean.MC_MIDDLE:
                destView.setBackgroundColor(Color.BLUE);
                break;
            case DateBean.CLICKED:
                destView.setBackgroundColor(Color.GREEN);
                break;
            default:
                destView.setBackgroundColor(Color.WHITE);
                break;
        }
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
            if(getChildAt(i).getTag() != null && (Integer)getChildAt(i).getTag()  == day) {
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
