package com.yazhi1992.moon.ui.configuration;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
        mViewModel.mcTipEnable.set(LibSPUtils.getBoolean(SPKeyConstant.TIP_BAD_MOOD_ENABLE, true));
        mViewModel.mcEnable.set(LibSPUtils.getBoolean(SPKeyConstant.MC_ENABLE, true));

        mBinding.rlMcEnable.setOnClickListener(v -> {
            mViewModel.mcEnable.set(!mBinding.switchMcEnable.isChecked());
            LibSPUtils.setBoolean(SPKeyConstant.MC_ENABLE, mViewModel.mcEnable.get());
        });

        mBinding.rlMcTipEnable.setOnClickListener(v -> {
            mViewModel.mcTipEnable.set(!mBinding.switchMcTipEnable.isChecked());
            LibSPUtils.setBoolean(SPKeyConstant.TIP_BAD_MOOD_ENABLE, mViewModel.mcTipEnable.get());
        });
    }
}
