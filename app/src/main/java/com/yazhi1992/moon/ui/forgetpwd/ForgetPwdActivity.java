package com.yazhi1992.moon.ui.forgetpwd;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import cn.leancloud.AVUser;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.data.CheckUserDataChain;
import com.yazhi1992.moon.databinding.ActivityFindPwdBinding;
import com.yazhi1992.moon.databinding.ActivitySetEmailBinding;
import com.yazhi1992.moon.event.ChangePwd;
import com.yazhi1992.moon.event.ChangeUserInfo;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;

@Route(path = ActivityRouter.FORGET_PWD)
public class ForgetPwdActivity extends BaseActivity {

    private ActivityFindPwdBinding mBinding;
    private ForgetPwdPresenter mPresenter = new ForgetPwdPresenter();
    @Autowired(name = ActivityRouter.KeyName.EMAIL)
    String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_find_pwd);

        initToolBar(mBinding.toolbar);

        mBinding.etEmail.setText(email);
        mBinding.etEmail.setSelection(email.length());

        mBinding.btnComfirm.setOnClickListener(v -> {
            if (getString(R.string.already_change_pwd).equals(mBinding.btnComfirm.getText())) {
                //已发送过。检验是否验证通过
                EventBus.getDefault().post(new ChangePwd(email));
                finish();
            } else {
                email = mBinding.etEmail.getText().toString();
                if (LibUtils.isNullOrEmpty(email)) return;
                mBinding.btnComfirm.setLoading(true);
                mPresenter.findPwd(email, new DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        //设置邮箱并成功发送验证邮件
                        mBinding.btnComfirm.setLoading(false);
                        mBinding.btnComfirm.setText(getString(R.string.already_change_pwd));
                        LibUtils.showToast(ForgetPwdActivity.this, getString(R.string.already_send_email));
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        mBinding.btnComfirm.setLoading(false);
                    }
                });
            }
        });

    }
}
