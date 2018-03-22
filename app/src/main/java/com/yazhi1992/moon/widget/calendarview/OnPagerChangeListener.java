package com.yazhi1992.moon.widget.calendarview;

/**
 * 页面切换接口
 */
public interface OnPagerChangeListener {

    /**
     * @param date date[0]年份  date[1]月份  date[2]日
     */
    void onPagerChanged(int[] date);

    /**
     * 滑动状态改变
     * @param state
     * @param movePx >0 行数变多，下移， <0 行数变少，上移
     */
    void onPageScrollStateChanged(int state, int movePx);

}
