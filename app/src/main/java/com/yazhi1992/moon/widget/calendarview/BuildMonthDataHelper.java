package com.yazhi1992.moon.widget.calendarview;

import com.yazhi1992.moon.ui.mc.McDataFromApi;

import java.util.List;

/**
 * Created by zengyazhi on 2018/4/6.
 */

public class BuildMonthDataHelper {
    List<DateBean> monthDatas;
    List<McDataFromApi> apiDatas;
    int firstDayPosition;
    int year;
    int month;
    int lastComeDay = -1; //如果不想去将上一个来之后的日期设置为中间，则设为-1（来，去，去。只闭合第一个来去）

    public int getLastComeDay() {
        return lastComeDay;
    }

    public void setLastComeDay(int lastComeDay) {
        this.lastComeDay = lastComeDay;
    }

    public List<DateBean> getMonthDatas() {
        return monthDatas;
    }

    public void setMonthDatas(List<DateBean> monthDatas) {
        this.monthDatas = monthDatas;
    }

    public List<McDataFromApi> getApiDatas() {
        return apiDatas;
    }

    public void setApiDatas(List<McDataFromApi> apiDatas) {
        this.apiDatas = apiDatas;
    }

    public int getFirstDayPosition() {
        return firstDayPosition;
    }

    public void setFirstDayPosition(int firstDayPosition) {
        this.firstDayPosition = firstDayPosition;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
