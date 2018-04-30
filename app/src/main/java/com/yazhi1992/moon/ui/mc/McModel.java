package com.yazhi1992.moon.ui.mc;

import android.databinding.ObservableField;

import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.widget.calendarview.DateBean;

/**
 * Created by zengyazhi on 2018/4/7.
 */

public class McModel {
    public ObservableField<Integer> mGender = new ObservableField<>(TypeConstant.MEN);
    public ObservableField<DateBean> data = new ObservableField<>();
}
