package com.yazhi1992.moon.viewmodel;

import com.yazhi1992.moon.util.AppUtils;

import java.util.Date;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class MemorialDayBean extends HistoryBean{

    private String title;
    private long time;
    private String timeStr;

    public MemorialDayBean(String title, long time) {
        this.title = title;
        this.time = time;
        this.timeStr = AppUtils.getTimeStrForMemorialDay(new Date(time));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
        this.timeStr = AppUtils.getTimeStrForMemorialDay(new Date(time));
    }

    public String getTimeStr() {
        return timeStr;
    }
}
