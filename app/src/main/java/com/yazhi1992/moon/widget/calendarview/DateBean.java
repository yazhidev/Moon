package com.yazhi1992.moon.widget.calendarview;

/**
 * Created by zengyazhi on 2018/3/21.
 */

public class DateBean {
    private int[] date; //阳历年月日
    private int type; //0上月，1本月，2下月

    public int[] getDate() {
        return date;
    }

    public void setDate(int year, int month, int day) {
        this.date = new int[]{year, month, day};
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
