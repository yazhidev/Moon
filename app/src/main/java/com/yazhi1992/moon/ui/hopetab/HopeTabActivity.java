package com.yazhi1992.moon.ui.hopetab;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.databinding.ActivityHopeTabBinding;
import com.yazhi1992.moon.ui.BaseActivity;

import java.util.ArrayList;
import java.util.List;

@Route(path = ActivityRouter.HOPE_TAB_LIST)
public class HopeTabActivity extends BaseActivity {

    private ActivityHopeTabBinding mBinding;
    private List<Fragment> mFragments = new ArrayList<>();
    private HopeTabAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_hope_tab);

        initToolBar(mBinding.toolbar);

        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab());
        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab());

        mAdapter = new HopeTabAdapter(getSupportFragmentManager());

        mFragments.add(HopeTabFragment.newInstance(TypeConstant.HOPE_UNFINISH));
        mFragments.add(HopeTabFragment.newInstance(TypeConstant.HOPE_DONE));

        mBinding.viewPager.setAdapter(mAdapter);

        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
        mBinding.tabLayout.getTabAt(0).setText("念念不忘");
        mBinding.tabLayout.getTabAt(1).setText("心想事成");
    }

    class HopeTabAdapter extends FragmentPagerAdapter {

        public HopeTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
