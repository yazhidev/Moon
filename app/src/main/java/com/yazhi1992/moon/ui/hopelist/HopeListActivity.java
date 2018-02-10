package com.yazhi1992.moon.ui.hopelist;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.databinding.ActivityHopeTabBinding;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.yazhilib.utils.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

@Route(path = ActivityRouter.HOPE_TAB_LIST)
public class HopeListActivity extends BaseActivity {

    private ActivityHopeTabBinding mBinding;
    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_hope_tab);

        initToolBar(mBinding.toolbar);

        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab());
        mBinding.tabLayout.addTab(mBinding.tabLayout.newTab());

        mAdapter = new FragmentPagerAdapter(getFragmentManager(), mFragments);

        mFragments.add(HopeTabFragment.newInstance(TypeConstant.HOPE_UNFINISH));
        mFragments.add(HopeTabFragment.newInstance(TypeConstant.HOPE_DONE));

        mBinding.viewPager.setAdapter(mAdapter);

        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
        mBinding.tabLayout.getTabAt(0).setText("念念不忘");
        mBinding.tabLayout.getTabAt(1).setText("求仁得仁");
    }


    //添加右上角加编辑按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                //编辑
                ActivityRouter.gotoAddHope(true);
                break;
            default:
                break;
        }
        return true;
    }
}
