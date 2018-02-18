package com.yazhi1992.moon.ui.mc;

import android.databinding.ObservableField;

import com.yazhi1992.moon.util.AppUtils;
import com.yazhi1992.yazhilib.utils.LibTimeUtils;

import java.util.Date;

/**
 * Created by zengyazhi on 2018/2/18.
 */

public class McData {

    private int mStatus;
    private String mGapDayNumStr;
    private String mTimeStr;

    public void setTime(long time) {
        Date date = new Date(time);
        mTimeStr = AppUtils.getTimeStrForMemorialDay(date);
        mGapDayNumStr = String.valueOf(Math.abs(LibTimeUtils.getGapBetweenTwoDay(new Date(), date)));
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public String getGapDayNumStr() {
        return mGapDayNumStr;
    }

    public void setGapDayNumStr(String gapDayNumStr) {
        mGapDayNumStr = gapDayNumStr;
    }

    public String getTimeStr() {
        return mTimeStr;
    }

    public void setTimeStr(String timeStr) {
        mTimeStr = timeStr;
    }
}
