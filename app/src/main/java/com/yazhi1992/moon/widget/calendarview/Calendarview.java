package com.yazhi1992.moon.widget.calendarview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by zengyazhi on 2018/3/22.
 */

public class Calendarview extends ViewPager {

    private int[] startDate = new int[]{2017, 1};//日历的开始年、月
    private int[] endDate;//日历的结束年、月
    private int[] initDate;//日历初始显示的年月
    private int count;//ViewPager的页数
    private int currentPosition;
    private OnPagerChangeListener pagerChangeListener;
    private CalendarPagerAdapter mPagerAdapter;

    public void setPagerChangeListener(OnPagerChangeListener pagerChangeListener) {
        this.pagerChangeListener = pagerChangeListener;
    }

    public Calendarview(@NonNull Context context) {
        this(context, null);
    }

    public Calendarview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
//        int[] ints = CalendarUtil.strToArray("2018-05");
        endDate = new int[]{2018, 8};
        initDate = new int[]{2018, 3};

        count = (endDate[0] - startDate[0]) * 12 + endDate[1] - startDate[1] + 1;
        currentPosition = CalendarUtil.dateToPosition(initDate[0], initDate[1], startDate[0], startDate[1]);
        mPagerAdapter = new CalendarPagerAdapter(count, startDate);
        setAdapter(mPagerAdapter);
        setCurrentItem(currentPosition);
        addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.e("zyz", "onPageSelected");

                refreshMonthView(position);
                currentPosition = position;
                if (pagerChangeListener != null) {
                    int[] date = CalendarUtil.positionToDate(position, startDate[0], startDate[1]);
                    pagerChangeListener.onPagerChanged(new int[]{date[0], date[1], 0});
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                Log.e("zyz", "onPageScrollStateChanged" + state);
            }
        });
    }

    private void refreshMonthView(int position) {
        MonthView monthView = mPagerAdapter.getViews().get(position);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getAdapter() != null) {
            MonthView view = (MonthView) getChildAt(0);
            if (view != null) {
                setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), MeasureSpec.EXACTLY));
            }
        }
    }
}
