package com.yazhi1992.moon.viewmodel;

import com.yazhi1992.moon.util.AppUtils;

import java.util.Date;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class MemorialDayBean extends IDataBean{

    private String mTitle;
    private long mTime; //纪念日的时间
    private String mTimeStr; //纪念日时间格式化后字符串

    public MemorialDayBean(String title, long time) {
        this.mTitle = title;
        this.mTime = time;
        this.mTimeStr = AppUtils.getTimeStrForMemorialDay(new Date(time));
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        this.mTime = time;
        this.mTimeStr = AppUtils.getTimeStrForMemorialDay(new Date(time));
    }

    public String getTimeStr() {
        return mTimeStr;
    }

}
