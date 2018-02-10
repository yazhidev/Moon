package com.yazhi1992.moon.ui.home;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVSaveOption;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.activity.AbsUpgrateActivity;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.constant.TableConstant;
import com.yazhi1992.moon.databinding.ActivityHomeBinding;
import com.yazhi1992.moon.event.AddHomeImg;
import com.yazhi1992.moon.ui.home.history.HistoryFragment;
import com.yazhi1992.moon.ui.home.home.HomeFragment;
import com.yazhi1992.moon.ui.home.set.SetFragment;
import com.yazhi1992.yazhilib.utils.LibUtils;
import com.yazhi1992.yazhilib.utils.LibStatusBarUtils;
import com.zhihu.matisse.Matisse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            List<Uri> uris = Matisse.obtainResult(data);
            Uri uri = uris.get(0);


            AVFile file = new AVFile("test.jpg", uri.getPath(), new HashMap<String, Object>());
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if(e == null) {
                        //保存到binder表
                        final AVQuery<AVObject> meQuery = new AVQuery<>(TableConstant.BindLover.CLAZZ_NAME);
                        meQuery.whereEqualTo(TableConstant.BindLover.USER_ID, AVUser.getCurrentUser().getObjectId());
                        meQuery.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                AVObject object = list.get(0);
                                object.put(TableConstant.BindLover.HOME_IMG, file.getUrl());
                                EventBus.getDefault().post(new AddHomeImg(file.getUrl()));
                            }
                        });

                    }
                }
            });
        }
    }
}
