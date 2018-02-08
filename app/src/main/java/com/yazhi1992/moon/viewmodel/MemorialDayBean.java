package com.yazhi1992.moon.viewmodel;

import android.databinding.ObservableField;

import com.yazhi1992.moon.BaseApplication;
import com.yazhi1992.moon.R;
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
    public ObservableField<Integer> mGapBetweenTwoDay = new ObservableField<>();
    public ObservableField<String> mTimeStr = new ObservableField<>(); //纪念日时间格式化后字符串

    public MemorialDayBean(String title, long time) {
        this.mTitle = title;
        setTime(time);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTime(long time) {
        this.mTime.set(time);
        Date date = new Date(time);
        this.mTimeStr.set(AppUtils.getTimeStrForMemorialDay(date));
        mGapBetweenTwoDay.set(LibTimeUtils.getGapBetweenTwoDay(new Date(), date));
        mGapDayNum.set(String.valueOf(Math.abs(mGapBetweenTwoDay.get())));
        setFinalTitle(getTitle());
    }

    public void setTitle(String title) {
        this.mTitle = title;
        setFinalTitle(title);
    }

    private void setFinalTitle(String title) {
        if (mGapBetweenTwoDay.get() > 0) {
            mFinalTitle.set(String.format(BaseApplication.getInstance().getString(R.string.memorial_after), title));
        } else {
            mFinalTitle.set(String.format(BaseApplication.getInstance().getString(R.string.memorial_belong), title));
        }
    }

    public int getGapBetweenTwoDay() {
        return mGapBetweenTwoDay.get();
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

    public String getTimeStr() {
        return mTimeStr.get();
    }

}
