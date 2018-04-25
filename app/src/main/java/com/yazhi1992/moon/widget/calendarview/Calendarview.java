package com.yazhi1992.moon.widget.calendarview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;

/**
 * Created by zengyazhi on 2018/3/22.
 */

public class Calendarview extends ViewPager {

    private int[] startDate = new int[]{2017, 1};//日历的开始年、月
    private int[] endDate;//日历的结束年、月
    private int[] initDate;//日历初始显示的年月
    private int count;//ViewPager的页数
    private int currentPosition;
    private int lastPosition;
    private OnPagerChangeListener pagerChangeListener;
    private CalendarPagerAdapter mPagerAdapter;
    private OnSingleChooseListener singleChooseListener;
    private int mMovePx;
    private InitCallback mInitCallback;

    public void setInitCallback(InitCallback initCallback) {
        mInitCallback = initCallback;
    }

    public void setPagerChangeListener(OnPagerChangeListener pagerChangeListener) {
        this.pagerChangeListener = pagerChangeListener;
    }

    public void setSingleChooseListener(OnSingleChooseListener singleChooseListener) {
        this.singleChooseListener = singleChooseListener;
    }

    public OnSingleChooseListener getSingleChooseListener() {
        return singleChooseListener;
    }

    public Calendarview(@NonNull Context context) {
        this(context, null);
    }

    public Calendarview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        int[] currentDate = CalendarUtil.getCurrentDate();
        endDate = new int[]{currentDate[0] + 1, currentDate[1]};
        initDate = currentDate;

        count = (endDate[0] - startDate[0]) * 12 + endDate[1] - startDate[1] + 1;
        currentPosition = CalendarUtil.dateToPosition(initDate[0], initDate[1], startDate[0], startDate[1]);
        mPagerAdapter = new CalendarPagerAdapter(count, startDate);
        setAdapter(mPagerAdapter);
        setCurrentItem(currentPosition);
        addOnPageChangeListener(new SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                refreshMonthViewByPosition(position);
                currentPosition = position;
                if (pagerChangeListener != null) {
                    int[] date = CalendarUtil.positionToDate(position, startDate[0], startDate[1]);
                    pagerChangeListener.onPagerChanged(new int[]{date[0], date[1], 0});
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == 1) {
                    lastPosition = currentPosition;
                }
                if (state == 0 && pagerChangeListener != null) {
                    if (lastPosition == currentPosition) {
                        pagerChangeListener.onPageScrollStateChanged(state, mMovePx);
                        return;
                    }
                    int[] date = CalendarUtil.positionToDate(currentPosition, startDate[0], startDate[1]);
                    int currentRows = CalendarUtil.getMonthRows(date[0], date[1]);
                    int[] lastDate = CalendarUtil.positionToDate(lastPosition, startDate[0], startDate[1]);
                    int lastRows = CalendarUtil.getMonthRows(lastDate[0], lastDate[1]);
                    MonthView monthView = mPagerAdapter.getViews().get(currentPosition);
                    mMovePx += (currentRows - lastRows) * monthView.getChildHeight();
                    pagerChangeListener.onPageScrollStateChanged(state, mMovePx);
                    Log.e("zyz", date[0] + "-" + date[1] + ":" + currentRows + "===" + lastDate[0] + "-" + lastDate[1] + ":" + lastRows);
                }
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mInitCallback != null) {
            int rowHeight = (int) (w / MonthView.COLUMN * MonthView.HEIGHT_SIZE);
            int currentRows = CalendarUtil.getMonthRows(initDate[0], initDate[1]);
            mMovePx += (currentRows - MonthView.MAXROW) * rowHeight;
            mInitCallback.onInit(initDate, mMovePx);
            Log.e("zyz", mMovePx + ":" + currentRows + "=== onSizeChanged");
        }
    }

    /**
     * 刷新某一天的view
     *
     * @param date
     */
    public void refreshViewByDateBean(DateBean date) {
        MonthView monthView = mPagerAdapter.getViews().get(currentPosition);
        monthView.fresh(date);
    }

    public void rebuildView() {
        SparseArray<MonthView> views = mPagerAdapter.getViews();
        for (int i = 0, nsize = views.size(); i < nsize; i++) {
            MonthView view = views.valueAt(i);
            view.startBuildData();
        }
    }

    /**
     * 刷新某个月份
     *
     * @param position
     */
    private void refreshMonthViewByPosition(int position) {
        MonthView monthView = mPagerAdapter.getViews().get(position);
        monthView.postInvalidate();
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
