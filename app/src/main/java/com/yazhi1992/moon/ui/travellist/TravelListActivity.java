package com.yazhi1992.moon.ui.travellist;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.databinding.ActivityTravelListBinding;
import com.yazhi1992.moon.ui.BaseActivity;

@Route(path = ActivityRouter.TRAVEL_LIST)
public class TravelListActivity extends BaseActivity {

    ActivityTravelListBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_travel_list);
        initToolBar(mBinding.toolbar);


    }
}
