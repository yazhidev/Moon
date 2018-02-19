package com.yazhi1992.moon.ui.setgender;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.data.CheckUserDataChain;
import com.yazhi1992.moon.databinding.ActivitySetGenderBinding;
import com.yazhi1992.moon.event.ChangeUserInfo;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.ui.BaseActivity;

import org.greenrobot.eventbus.EventBus;

@Route(path = ActivityRouter.SET_GENDER)
public class SetGenderActivity extends BaseActivity {

    private ActivitySetGenderBinding mBinding;
    private SetGenderPresenter mPresenter = new SetGenderPresenter();
    private SetGenderViewModel mViewModel = new SetGenderViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_set_gender);
        mBinding.setItem(mViewModel);
        initToolBar(mBinding.toolbar);

        User userDao = new UserDaoUtil().getUserDao();
        if(userDao != null && userDao.getGender() != 0) {
            mViewModel.mGender.set(userDao.getGender());
        }

        mBinding.rlMan.setOnClickListener(v -> mViewModel.mGender.set(TypeConstant.MEN));
        mBinding.rlWomen.setOnClickListener(v -> mViewModel.mGender.set(TypeConstant.WOMEN));

        mBinding.btnComfirm.setOnClickListener(v -> {
            mBinding.btnComfirm.setLoading(true);
            mPresenter.setGender(mViewModel.mGender.get(), new DataCallback<Integer>() {
                @Override
                public void onSuccess(Integer data) {
                    mBinding.btnComfirm.setLoading(false);
                    //修改本地data
                    new UserDaoUtil().updateGender(data);
                    if(isTaskRoot()) {
                        CheckUserDataChain.getInstance().processChain();
                    } else {
                        new UserDaoUtil().updateGender(data);
                        ChangeUserInfo changeUserInfo = new ChangeUserInfo();
                        changeUserInfo.setGender(data);
                        EventBus.getDefault().post(changeUserInfo);
                        finish();
                    }
                }

                @Override
                public void onFailed(int code, String msg) {
                    mBinding.btnComfirm.setLoading(false);
                }
            });
        });
    }
}
