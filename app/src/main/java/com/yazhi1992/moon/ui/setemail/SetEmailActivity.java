package com.yazhi1992.moon.ui.setemail;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.avos.avoscloud.AVUser;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.data.CheckUserDataChain;
import com.yazhi1992.moon.databinding.ActivitySetEmailBinding;
import com.yazhi1992.moon.dialog.LoadingHelper;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.util.PushManager;
import com.yazhi1992.yazhilib.utils.LibUtils;

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
        String email1 = currentUser.getEmail();
        if(LibUtils.notNullNorEmpty(email1)) {
            mBinding.tvEmail.setText(email1);
        }

        mBinding.btnComfirm.setOnClickListener(v -> {

            if(getString(R.string.already_check_email).equals(mBinding.btnComfirm.getText())) {
                //已发送过。检验是否验证通过
                mPresenter.checkEmailStatus(new DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        if(data) {
                            //验证通过
                            finish();
                            LibUtils.showToast(SetEmailActivity.this, getString(R.string.check_suc));
                            // TODO: 2018/2/20 修改个人信息页状态
                        } else{
                           LibUtils.showToast(SetEmailActivity.this, getString(R.string.check_failed));
                        }
                    }

                    @Override
                    public void onFailed(int code, String msg) {

                    }
                });
            } else {
                if(LibUtils.isNullOrEmpty(email1)) return;
                mBinding.btnComfirm.setLoading(true);
                mPresenter.checkEmail(email1, new DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        //设置邮箱并成功发送验证邮件
                        mBinding.btnComfirm.setLoading(false);
                        mBinding.btnComfirm.setText(getString(R.string.already_check_email));
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
            mBinding.tvLater.setVisibility(View.VISIBLE);
            mBinding.tvLater.setOnClickListener(v -> {
                //继续检查下一个
                CheckUserDataChain.getInstance().processChain();
            });
        }
    }
}
