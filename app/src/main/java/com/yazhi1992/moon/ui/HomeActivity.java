package com.yazhi1992.moon.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.databinding.ActivityHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding mBinding;
    private List<Fragment> mFragments = new ArrayList<>();
    private HomeAdapter mHomeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
