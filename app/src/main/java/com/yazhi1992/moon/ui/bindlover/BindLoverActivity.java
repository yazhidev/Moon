package com.yazhi1992.moon.ui.bindlover;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.avos.avoscloud.AVUser;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.api.bean.BindLoverBean;
import com.yazhi1992.moon.databinding.ActivityBindLoverBinding;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.widget.PageRouter;
import com.yazhi1992.yazhilib.utils.LibUtils;

/**
 * Created by zengyazhi on 2018/1/26.
 */

@Route(path = PageRouter.BIND_LOVER)
public class BindLoverActivity extends AppCompatActivity {

    private ActivityBindLoverBinding mBinding;
    private BindLoverPresenter mPresenter = new BindLoverPresenter();
    private UserDaoUtil mUserDaoUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bind_lover);

        setSupportActionBar(mBinding.toolbar);

        mPresenter.getInviteNum(new DataCallback<String>() {
            @Override
            public void onSuccess(String data) {
                mBinding.tvInviteNum.setText(data);
            }

            @Override
            public void onFailed(int code, String msg) {

            }
        });

        mBinding.tvBind.setOnClickListener(v -> {
            String inviteNum = mBinding.etInput.getText().toString();
            if (LibUtils.isNullOrEmpty(inviteNum) || inviteNum.length() != 6) {
                mBinding.inputLayout.setError("请输入正确的邀请码");
                return;
            }
            mBinding.tvBind.setLoading(true);
            mPresenter.invite(inviteNum, AVUser.getCurrentUser().getObjectId(), new DataCallback<BindLoverBean>() {

                @Override
                public void onSuccess(BindLoverBean data) {
                    if (data.isBindComplete()) {
                        mBinding.tvState.setText("有情人终成眷属");
                        //更新本地数据
                        mUserDaoUtil = new UserDaoUtil();
                        mUserDaoUtil.updateLoveInfo(data.getLoverId()
                                , data.getLoverName()
                                , data.getLoverHeadUrl());
                        //绑定成功，跳转
                        PageRouter.gotoHomePage();
                        finish();
                    } else {
                        mBinding.tvState.setText("已有心上人，尚在单相思");
                    }
                    LibUtils.showLongToast(BindLoverActivity.this, "来日方长，请多关照~");
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
                        mBinding.tvState.setText("有情人终成眷属");
                        //绑定成功，跳转
                        PageRouter.gotoHomePage();
                        finish();
                        break;
                    case 1:
                        mBinding.tvState.setText("已有心上人，尚在单相思");
                        break;
                    case 2:
                        mBinding.tvState.setText("还未添加心上人\n马上拨打电话订购吧！");
                        break;
                    default:
                        break;
                }
                mBinding.btnCheckState.setLoading(false);
                if(showToast) {
                    LibUtils.showToast(BindLoverActivity.this, "状态已更新");
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
