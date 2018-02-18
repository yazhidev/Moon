package com.yazhi1992.moon.ui.configuration;

import android.databinding.ObservableField;

/**
 * Created by zengyazhi on 2018/2/18.
 */

public class ConfigurationViewModel {

    public ObservableField<Boolean> mcEnable = new ObservableField<>(false);
    public ObservableField<Boolean> mcTipEnable = new ObservableField<>(false);
}
