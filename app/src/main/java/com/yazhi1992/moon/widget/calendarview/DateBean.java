package com.yazhi1992.moon.widget.calendarview;

import android.support.annotation.IntDef;

/**
 * Created by zengyazhi on 2018/3/21.
 */

public class DateBean {
    private int[] date; //阳历年月日
    private int type; //0上月，1本月，2下月

    private int mcType; //1来，2走, 3中间（不可更改状态）

    public static final int NORMAL = 0;
    public static final int MC_COME = 1;
    public static final int MC_GO = 2;
    public static final int MC_MIDDLE = 3;
    public static final int CLICKED = 4;

    @IntDef({MC_COME, MC_GO, MC_MIDDLE, NORMAL, CLICKED})
    public @interface VIEW_TYPE {}

    public int getMcType() {
        return mcType;
    }

    public void setMcType(@VIEW_TYPE int mcType) {
        this.mcType = mcType;
    }

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
