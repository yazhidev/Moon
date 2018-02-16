package com.yazhi1992.moon.viewmodel;

import android.databinding.ObservableField;

import com.yazhi1992.moon.util.AppUtils;
import com.yazhi1992.yazhilib.utils.LibTimeUtils;

import java.util.Date;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class McBean extends IDataBean{

    public ObservableField<Integer> mStatus = new ObservableField<>();
    public ObservableField<String> mGapDayNumStr = new ObservableField<>(); //最近一次 mc 记录的距离天数（字符串类型）
    public ObservableField<String> mTimeStr = new ObservableField<>(); //最近一次 mc 记录的时间格式化后字符串

    public McBean(int status) {
        mStatus.set(status);
    }

    public void setTime(long time) {
        Date date = new Date(time);
        this.mTimeStr.set(AppUtils.getTimeStrForMemorialDay(date));
        mGapDayNumStr.set(String.valueOf(Math.abs(LibTimeUtils.getGapBetweenTwoDay(new Date(), date))));
    }

    public int getStatus() {
        return mStatus.get();
    }

    public void setStatus(int status) {
        this.mStatus.set(status);
    }

    public void setTimeStr(String timeStr) {
        mTimeStr.set(timeStr);
    }
}
