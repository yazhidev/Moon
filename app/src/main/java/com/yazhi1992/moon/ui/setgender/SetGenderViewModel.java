package com.yazhi1992.moon.ui.setgender;

import android.databinding.ObservableField;

import com.yazhi1992.moon.constant.TypeConstant;

/**
 * Created by zengyazhi on 2018/2/12.
 */

public class SetGenderViewModel {

    public ObservableField<Integer> mGender = new ObservableField<>(TypeConstant.MEN);
}
