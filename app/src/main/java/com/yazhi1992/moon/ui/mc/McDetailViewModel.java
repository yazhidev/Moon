package com.yazhi1992.moon.ui.mc;

import android.databinding.ObservableField;

import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.viewmodel.McBean;

/**
 * Created by zengyazhi on 2018/2/16.
 */

public class McDetailViewModel {

    public ObservableField<Integer> mGender = new ObservableField<>(TypeConstant.MEN);
    public ObservableField<Integer> mStatus = new ObservableField<>(0);
    public ObservableField<Boolean> mFetching = new ObservableField<>(true); //是否正在获取数据
    public ObservableField<Boolean> mEmpty = new ObservableField<>(); //数据是否为空
    public ObservableField<String> mGapDayNumStr = new ObservableField<>(); //最近一次 mc 记录的距离天数（字符串类型）
    public ObservableField<String> mTimeStr = new ObservableField<>(); //最近一次 mc 记录的时间格式化后字符串

}
