package com.yazhi1992.moon.ui.configuration;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.widget.CompoundButton;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.constant.SPKeyConstant;
import com.yazhi1992.moon.databinding.ActivityConfigurationBinding;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.yazhilib.utils.LibSPUtils;

@Route(path = ActivityRouter.CONFIGURATION)
public class ConfigurationActivity extends BaseActivity {

    private ActivityConfigurationBinding mBinding;
    private ConfigurationViewModel mViewModel = new ConfigurationViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_configuration);

        initToolBar(mBinding.toolbar);

        mBinding.setItem(mViewModel);
        mViewModel.mcEnable.set(LibSPUtils.getBoolean(SPKeyConstant.MC_ENABLE, true));
        mViewModel.mcTipSwitchBtnEnable.set(mViewModel.mcEnable.get());
        mViewModel.mcTipEnable.set(LibSPUtils.getBoolean(SPKeyConstant.TIP_BAD_MOOD_ENABLE, true));

        mBinding.switchMcEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mViewModel.setMcEnable(isChecked);
            }
        });

        mBinding.switchMcTipEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mViewModel.setTipEnable(isChecked);
            }
        });

        mBinding.rlMcEnable.setOnClickListener(v -> {
            mBinding.switchMcEnable.setChecked(!mBinding.switchMcEnable.isChecked());
        });

        mBinding.rlMcTipEnable.setOnClickListener(v -> {
            mBinding.switchMcTipEnable.setChecked(!mBinding.switchMcTipEnable.isChecked());
        });
    }
}
