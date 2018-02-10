package com.yazhi1992.moon.ui.home;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.activity.AbsUpgrateActivity;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.databinding.ActivityHomeBinding;
import com.yazhi1992.moon.ui.home.history.HistoryFragment;
import com.yazhi1992.moon.ui.home.home.HomeFragment;
import com.yazhi1992.moon.ui.home.set.SetFragment;
import com.yazhi1992.yazhilib.utils.LibUtils;
import com.yazhi1992.yazhilib.utils.LibStatusBarUtils;

import java.util.ArrayList;
import java.util.List;

@Route(path = ActivityRouter.HOME_PAGE)
public class HomeActivity extends AbsUpgrateActivity {

    private ActivityHomeBinding mBinding;
    private List<Fragment> mFragments = new ArrayList<>();
    private HomeAdapter mHomeAdapter;
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LibStatusBarUtils.with(this).init();

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        mHomeAdapter = new HomeAdapter(getSupportFragmentManager());

        mFragments.add(new HomeFragment());
        mFragments.add(new HistoryFragment());
        mFragments.add(new SetFragment());

        mBinding.viewPager.setAdapter(mHomeAdapter);

        mBinding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_main:
                        mBinding.viewPager.setCurrentItem(0, false);
                        break;
                    case R.id.item_history:
                        mBinding.viewPager.setCurrentItem(1, false);
                        break;
                    case R.id.item_more:
                        mBinding.viewPager.setCurrentItem(2, false);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        Intent intent = getIntent();
        if(intent != null) {
            String action = intent.getStringExtra(ActionConstant.Notification.ACTION_KEY);
            if(LibUtils.notNullNorEmpty(action)) {
                if(action.equals(ActionConstant.Notification.ACTION_VALUE_HISTORY)) {
                    //跳转到回忆页
                    mBinding.viewPager.setCurrentItem(1);
                    mBinding.bottomNavigation.setSelectedItemId(R.id.item_history);
                }
            }
        }
    }

    //双击退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            // 判断是否在两秒之内连续点击返回键，是则退出，否则不退出
            if (System.currentTimeMillis() - mExitTime > 2000) {
                LibUtils.showToast(this, getString(R.string.finish_app));
                // 将系统当前的时间赋值给exitTime
                mExitTime = System.currentTimeMillis();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    class HomeAdapter extends FragmentPagerAdapter {

        public HomeAdapter(FragmentManager fm) {
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
