package com.yazhi1992.moon.ui.configuration;

import android.databinding.ObservableField;

import com.yazhi1992.moon.constant.SPKeyConstant;
import com.yazhi1992.yazhilib.utils.LibSPUtils;

/**
 * Created by zengyazhi on 2018/2/18.
 */

public class ConfigurationViewModel {

    public ObservableField<Boolean> mcEnable = new ObservableField<>(false);
    public ObservableField<Boolean> mcTipEnable = new ObservableField<>(false);
    public ObservableField<Boolean> mcTipSwitchBtnEnable = new ObservableField<>(false);

    public void setMcEnable(boolean enable) {
        mcEnable.set(enable);
        LibSPUtils.setBoolean(SPKeyConstant.MC_ENABLE, enable);
        setTipEnable(enable);
        //关闭mc，则关闭mc提醒，且mc提醒不可操作
        mcTipSwitchBtnEnable.set(enable);
    }

    public void setTipEnable(boolean enable) {
        mcTipEnable.set(enable);
        LibSPUtils.setBoolean(SPKeyConstant.TIP_BAD_MOOD_ENABLE, enable);
    }
}
