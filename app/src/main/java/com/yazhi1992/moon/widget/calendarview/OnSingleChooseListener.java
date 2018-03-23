package com.yazhi1992.moon.widget.calendarview;

import android.view.View;

/**
 * 日期点击接口
 */
public interface OnSingleChooseListener {
    /**
     * @param view
     * @param date
     */
    void onSingleChoose(View view, DateBean date, int position);
}
