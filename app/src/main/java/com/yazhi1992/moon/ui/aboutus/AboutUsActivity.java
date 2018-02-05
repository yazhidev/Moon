package com.yazhi1992.moon.ui.aboutus;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.BuildConfig;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.databinding.ActivityAboutUsBinding;
import com.yazhi1992.moon.ui.BaseActivity;

@Route(path = ActivityRouter.ABOUT_US)
public class AboutUsActivity extends BaseActivity {

    private ActivityAboutUsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_about_us);
        initToolBar(mBinding.toolbar);

        mBinding.tvVersion.setText(String.format(getString(R.string.version_name), BuildConfig.VERSION_NAME));
    }
}
