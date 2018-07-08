package com.yazhi1992.moon.ui.setemail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.avos.avoscloud.AVUser;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.data.CheckUserDataChain;
import com.yazhi1992.moon.databinding.ActivitySetEmailBinding;
import com.yazhi1992.moon.event.ChangeUserInfo;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;

@Route(path = ActivityRouter.SET_EMAIL)
public class SetEmailActivity extends BaseActivity {

    private ActivitySetEmailBinding mBinding;
    private SetEmailPresenter mPresenter = new SetEmailPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_set_email);

        initToolBar(mBinding.toolbar);

        //先查询是否已验证过邮箱
        AVUser currentUser = AVUser.getCurrentUser();
        String email = currentUser.getEmail();
        if(LibUtils.notNullNorEmpty(email)) {
            mBinding.tvEmail.setText(email);
        }

        mBinding.btnComfirm.setOnClickListener(v -> {
            if(getString(R.string.already_check_email).equals(mBinding.btnComfirm.getText())) {
                //已发送过。检验是否验证通过
                mPresenter.checkEmailStatus(new DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        if(data) {
                            //验证通过
                            finishCheck();
                        } else{
                           LibUtils.showToast(SetEmailActivity.this, getString(R.string.check_failed));
                        }
                    }

                    @Override
                    public void onFailed(int code, String msg) {

                    }
                });
            } else {
                if(LibUtils.isNullOrEmpty(email)) return;
                mBinding.btnComfirm.setLoading(true);
                mPresenter.checkEmail(email, new DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        //设置邮箱并成功发送验证邮件
                        mBinding.btnComfirm.setLoading(false);
                        mBinding.btnComfirm.setText(getString(R.string.already_check_email));
                        LibUtils.showToast(SetEmailActivity.this, getString(R.string.already_send_email));
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        mBinding.btnComfirm.setLoading(false);
                    }
                });
            }
        });

        if(isTaskRoot()) {
            //注册后进入
            LibUtils.showToast(SetEmailActivity.this, getString(R.string.already_send_check_email));
            mBinding.btnComfirm.setText(getString(R.string.already_check_email));
            mBinding.tvLater.setVisibility(View.VISIBLE);
            mBinding.tvLater.setOnClickListener(v -> {
                //继续检查下一个
                CheckUserDataChain.getInstance().processChain();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.checkEmailStatus(new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                if(data) {
                    finishCheck();
                }
            }

            @Override
            public void onFailed(int code, String msg) {

            }
        });
    }

    private void finishCheck() {
        ChangeUserInfo changeUserInfo = new ChangeUserInfo();
        changeUserInfo.setEmailValid(true);
        EventBus.getDefault().post(changeUserInfo);
        finish();
        LibUtils.showToast(SetEmailActivity.this, getString(R.string.check_suc));
    }
}
