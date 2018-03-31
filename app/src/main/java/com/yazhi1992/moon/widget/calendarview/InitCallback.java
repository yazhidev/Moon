package com.yazhi1992.moon.widget.calendarview;

/**
 * 初始化回调接口
 */
public interface InitCallback {
    /**
     * @param date
     * @param movePx 初始化要移动多少距离
     */
    void onInit(int[] date, int movePx);
}
