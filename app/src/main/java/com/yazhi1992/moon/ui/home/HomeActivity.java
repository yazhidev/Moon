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
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.constant.CodeConstant;
import com.yazhi1992.moon.constant.SPKeyConstant;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.databinding.ActivityHomeBinding;
import com.yazhi1992.moon.dialog.LoadingHelper;
import com.yazhi1992.moon.event.AddHomeImg;
import com.yazhi1992.moon.event.ChangeUserInfo;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.ui.home.history.HistoryFragment;
import com.yazhi1992.moon.ui.home.home.HomeFragment;
import com.yazhi1992.moon.ui.home.set.SetFragment;
import com.yazhi1992.moon.ui.mc.McDetailPresenter;
import com.yazhi1992.moon.util.AppUtils;
import com.yazhi1992.moon.util.IUploader;
import com.yazhi1992.moon.util.StorageUtil;
import com.yazhi1992.moon.util.TipDialogHelper;
import com.yazhi1992.moon.util.UploadPhotoHelper;
import com.yazhi1992.moon.viewmodel.McBean;
import com.yazhi1992.yazhilib.utils.LibFileUtils;
import com.yazhi1992.yazhilib.utils.LibSPUtils;
import com.yazhi1992.yazhilib.utils.LibStatusBarUtils;
import com.yazhi1992.yazhilib.utils.LibUtils;
import com.zhihu.matisse.Matisse;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

@Route(path = ActivityRouter.HOME_PAGE)
public class HomeActivity extends AbsUpgrateActivity {

    private ActivityHomeBinding mBinding;
    private List<Fragment> mFragments = new ArrayList<>();
    private HomeAdapter mHomeAdapter;
    private HomePresenter mPresenter = new HomePresenter();

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
        mBinding.viewPager.setOffscreenPageLimit(mFragments.size());

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
        if (intent != null) {
            String action = intent.getStringExtra(ActionConstant.Notification.ACTION_KEY);
            if (LibUtils.notNullNorEmpty(action)) {
                if (action.equals(ActionConstant.Notification.ACTION_VALUE_HISTORY)) {
                    //跳转到回忆页
                    mBinding.viewPager.setCurrentItem(1);
                    mBinding.bottomNavigation.setSelectedItemId(R.id.item_history);
                }
            }
        }

        if (LibSPUtils.getBoolean(SPKeyConstant.TIP_BAD_MOOD_ENABLE, true)) {
            String lastTipTime = LibSPUtils.getString(SPKeyConstant.TIP_BAD_MOOD_TIME, "");
            String todayTime = AppUtils.memorialDayYmdFormat.format(new Date());
            if (!lastTipTime.equals(todayTime)) {
                //今天未提醒
                new McDetailPresenter().getLastMcRecord(new DataCallback<McBean>() {
                    @Override
                    public void onSuccess(McBean data) {
                        if (data != null) {
                            LibSPUtils.setString(SPKeyConstant.TIP_BAD_MOOD_TIME, todayTime);
                            Integer gapDayNum = Integer.valueOf(data.mGapDayNumStr.get());
                            if ((gapDayNum < 37 && gapDayNum > 25) || gapDayNum < 3) {
                                //即将来和刚来mc时，提示对方可能心情烦躁
                                String tipStr;
                                int gender = new UserDaoUtil().getUserDao().getGender();
                                if (gender == TypeConstant.MEN) {
                                    tipStr = getString(R.string.mc_tip_for_man);
                                } else {
                                    tipStr = getString(R.string.mc_tip_for_woman);
                                }
                                TipDialogHelper.getInstance().showOneBtnDialog(HomeActivity.this, tipStr, new TipDialogHelper.OnComfirmListener() {
                                    @Override
                                    public void comfirm() {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailed(int code, String msg) {

                    }
                });
            }
        }
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
        if (requestCode == CodeConstant.PICK_PHOTO_FOR_HOME && resultCode == RESULT_OK) {
            List<String> uris = Matisse.obtainPathResult(data);
            String path = uris.get(0);
            //开始上传
            if (LibUtils.notNullNorEmpty(path)) {
                UploadPhotoHelper.uploadPhoto(this, path, new IUploader() {
                    @Override
                    public void upload(String filePath, DataCallback<String> callback) {
                        mPresenter.uploadHomeImg(filePath, new DataCallback<String>() {
                            @Override
                            public void onSuccess(String data) {
                                callback.onSuccess(data);
                            }

                            @Override
                            public void onFailed(int code, String msg) {
                                callback.onFailed(code, msg);
                            }
                        });
                    }
                }, new UploadPhotoHelper.onUploadPhotoListener() {
                    @Override
                    public void onStart() {
                        LoadingHelper.getInstance().showLoading(HomeActivity.this);
                    }

                    @Override
                    public void onSuc(String remoteImgUrl) {
                        EventBus.getDefault().post(new AddHomeImg(remoteImgUrl));
                        LoadingHelper.getInstance().closeLoading();
                    }

                    @Override
                    public void onError(String msg) {
                        LibUtils.showToast(HomeActivity.this, msg);
                        LoadingHelper.getInstance().closeLoading();
                    }
                });
            }
        }
    }
}
