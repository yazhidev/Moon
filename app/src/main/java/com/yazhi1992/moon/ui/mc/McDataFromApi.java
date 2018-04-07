package com.yazhi1992.moon.ui.mc;

/**
 * Created by zengyazhi on 2018/4/5.
 */

public class McDataFromApi {
    long mTime; //时间戳
    int mYear;
    int mMonth;
    int mDay;
    /**
     * @see com.yazhi1992.moon.constant.TypeConstant.McAction
     */
    int mAction;

    public McDataFromApi(long time, int year, int month, int day, int action) {
        mTime = time;
        mYear = year;
        mMonth = month;
        mDay = day;
        mAction = action;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int month) {
        mMonth = month;
    }

    public int getDay() {
        return mDay;
    }

    public void setDay(int day) {
        mDay = day;
    }

    public int getAction() {
        return mAction;
    }

    public void setAction(int action) {
        mAction = action;
    }
}
