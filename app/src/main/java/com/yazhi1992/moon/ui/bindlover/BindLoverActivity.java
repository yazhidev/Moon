package com.yazhi1992.moon.ui.bindlover;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.avos.avoscloud.AVUser;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.activity.AbsUpgrateActivity;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.api.bean.BindLoverBean;
import com.yazhi1992.moon.databinding.ActivityBindLoverBinding;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.yazhilib.utils.LibUtils;

/**
 * Created by zengyazhi on 2018/1/26.
 */

@Route(path = ActivityRouter.BIND_LOVER)
public class BindLoverActivity extends AbsUpgrateActivity {

    private ActivityBindLoverBinding mBinding;
    private BindLoverPresenter mPresenter = new BindLoverPresenter();
    private UserDaoUtil mUserDaoUtil;
    private String myInviteNum;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bind_lover, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bind_lover);

        setSupportActionBar(mBinding.toolbar);
        mBinding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.log_out) {
                    new UserDaoUtil().clear();
                    ActivityRouter.gotoLogin();
                    finish();
                } else if(item.getItemId() == R.id.about_us) {
                    ActivityRouter.gotoAboutUs();
                }
                return true;
            }
        });

        mPresenter.getInviteNum(new DataCallback<String>() {
            @Override
            public void onSuccess(String data) {
                mBinding.tvInviteNum.setText(data);
                myInviteNum = data;
            }

            @Override
            public void onFailed(int code, String msg) {

            }
        });

        mBinding.tvBind.setOnClickListener(v -> {
            String inviteNum = mBinding.etInput.getText().toString();
            if (LibUtils.isNullOrEmpty(inviteNum) || inviteNum.length() != 6) {
                mBinding.inputLayout.setError(getString(R.string.bind_error_code));
                return;
            }
            if(LibUtils.notNullNorEmpty(myInviteNum) && inviteNum.equals(myInviteNum)) {
                mBinding.inputLayout.setError(getString(R.string.bind_error_code_peer));
                return;
            }
            mBinding.tvBind.setLoading(true);
            mPresenter.invite(inviteNum, AVUser.getCurrentUser().getObjectId(), new DataCallback<BindLoverBean>() {

                @Override
                public void onSuccess(BindLoverBean data) {
                    if (data.isBindComplete()) {
                        mBinding.tvState.setText(getString(R.string.bind_state_suc));
                        //更新本地数据
                        mUserDaoUtil = new UserDaoUtil();
                        mUserDaoUtil.updateLoveInfo(data.getLoverId()
                                , data.getLoverName()
                                , data.getLoverHeadUrl());
                        //绑定成功，跳转
                        ActivityRouter.gotoHomePage();
                        finish();
                    } else {
                        mBinding.tvState.setText(getString(R.string.bind_state_single));
                    }
                    LibUtils.showLongToast(BindLoverActivity.this, getString(R.string.bind_state_finish));
                    mBinding.tvBind.setLoading(false);
                }

                @Override
                public void onFailed(int code, String msg) {
                    mBinding.tvBind.setLoading(false);
                    LibUtils.showToast(BindLoverActivity.this, msg);
                }
            });
        });

        checkBindState(false);

        mBinding.btnCheckState.setOnClickListener(v -> {
                    mBinding.btnCheckState.setLoading(true);
                    checkBindState(true);
                }
        );
    }

    /**
     * 检测绑定状态
     */
    public void checkBindState(boolean showToast) {
        mPresenter.checkState(new DataCallback<Integer>() {
            @Override
            public void onSuccess(Integer data) {
                switch (data) {
                    case 0:
                        mBinding.tvState.setText(getString(R.string.bind_state_suc));
                        //绑定成功，跳转
                        ActivityRouter.gotoHomePage();
                        finish();
                        break;
                    case 1:
                        mBinding.tvState.setText(getString(R.string.bind_state_single));
                        break;
                    case 2:
                        mBinding.tvState.setText(getString(R.string.bind_state_original));
                        break;
                    default:
                        break;
                }
                mBinding.btnCheckState.setLoading(false);
                if (showToast) {
                    LibUtils.showToast(BindLoverActivity.this, getString(R.string.bind_state_refresh));
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                mBinding.btnCheckState.setLoading(false);
                LibUtils.showToast(BindLoverActivity.this, msg);
            }
        });
    }
}
