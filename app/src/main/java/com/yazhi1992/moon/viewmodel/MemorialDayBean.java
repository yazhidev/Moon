package com.yazhi1992.moon.viewmodel;

import android.databinding.ObservableField;

import com.yazhi1992.moon.util.AppUtils;
import com.yazhi1992.yazhilib.utils.LibTimeUtils;

import java.util.Date;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class MemorialDayBean extends IDataBean{

    private String mTitle;
    public ObservableField<String> mFinalTitle = new ObservableField<>();
    public ObservableField<Long> mTime = new ObservableField<>(); //纪念日的时间
    public ObservableField<String> mGapDayNum = new ObservableField<>(); //纪念日的时间
    private String mTimeStr; //纪念日时间格式化后字符串

    public MemorialDayBean(String title, long time) {
        this.mTitle = title;
        this.mTime.set(time);
        Date date = new Date(time);
        this.mTimeStr = AppUtils.getTimeStrForMemorialDay(date);
        mGapDayNum.set(String.valueOf(Math.abs(LibTimeUtils.getGapBetweenTwoDay(new Date(), date))));
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setFinalTitle(String finalTitle) {
        mFinalTitle.set(finalTitle);
    }

    public String getFinalTitle() {
        return mFinalTitle.get();
    }

    public String getGapDayNum() {
        return mGapDayNum.get();
    }

    public long getTime() {
        return mTime.get();
    }

    public void setTime(long time) {
        this.mTime.set(time);
        this.mTimeStr = AppUtils.getTimeStrForMemorialDay(new Date(time));
    }

    public String getTimeStr() {
        return mTimeStr;
    }

}
