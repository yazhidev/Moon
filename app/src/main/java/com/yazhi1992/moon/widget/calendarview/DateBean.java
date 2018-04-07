package com.yazhi1992.moon.widget.calendarview;

import android.support.annotation.IntDef;
import android.util.Log;

import com.yazhi1992.moon.constant.TypeConstant;
import com.zhihu.matisse.internal.ui.PreviewItemFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by zengyazhi on 2018/3/21.
 */

public class DateBean {
    private int[] date; //阳历年月日
    private long time;
    private int type; //0上月，1本月，2下月
    private int mcType; //1来，2走, 3中间（不可更改状态）
    private boolean clicked;

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
        Calendar instance = Calendar.getInstance();
        instance.set(year, month - 1, day);
        return instance.getTime().getTime() / 1000;
    }

    public int getType() {
        return type;
    }

    public void setType(@TypeConstant.MONTH_TYPE int type) {
        this.type = type;
    }
}
