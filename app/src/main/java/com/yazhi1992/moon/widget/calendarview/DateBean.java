package com.yazhi1992.moon.widget.calendarview;

import com.yazhi1992.moon.constant.TypeConstant;

/**
 * Created by zengyazhi on 2018/3/21.
 */

public class DateBean {
    private int[] date; //阳历年月日
    private long time;
    private int type; //0上月，1本月，2下月
    private int mcType; //1来，2走, 3中间（不可更改状态）
    private boolean clicked;
    private boolean isToday;

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public int getMcType() {
        return mcType;
    }

    public void setMcType(@TypeConstant.MC_VIEW_TYPE int mcType) {
        this.mcType = mcType;
    }

    public int[] getDate() {
        return date;
    }

    public void setDate(int year, int month, int day) {
        this.date = new int[]{year, month, day};
        this.time = getTimeFromStr(year, month, day);
    }

    public long getTime() {
        return time;
    }

    private long getTimeFromStr(int year, int month, int day) {
        return CalendarUtil.getTime(year, month, day);
    }

    public int getType() {
        return type;
    }

    public void setType(@TypeConstant.MONTH_TYPE int type) {
        this.type = type;
    }
}
