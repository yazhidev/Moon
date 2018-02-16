package com.yazhi1992.moon.ui.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.BuildConfig;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.activity.AbsUpgrateActivity;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.data.CheckUserDataChain;
import com.yazhi1992.moon.databinding.ActivityLoginBinding;
import com.yazhi1992.moon.dialog.LoadingDialog;
import com.yazhi1992.moon.dialog.LoadingHelper;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.util.PushManager;

@Route(path = ActivityRouter.LOGIN)
public class LoginActivity extends AbsUpgrateActivity {

    private ActivityLoginBinding mBinding;
    private LoginPresenter mPresenter = new LoginPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        mBinding.igQqLogin.setOnClickListener(v -> mPresenter.loginWithQQ(this, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean haveLover) {
                PushManager.getInstance().register();
                LoadingHelper.getInstance().closeLoading();
                //继续检查下一个
                CheckUserDataChain.getInstance().processChain();
            }

            @Override
            public void onFailed(int code, String msg) {

            }
        }));

        mBinding.btn.setOnClickListener(v -> {
            new LoadingDialog().show(getFragmentManager());
        });

        mPresenter.init(this);

        mBinding.tvVersion.setText(String.format(getString(R.string.version_name), BuildConfig.VERSION_NAME));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mPresenter.onActivityResult(requestCode, resultCode, data);
        } else {
            LoadingHelper.getInstance().closeLoading();
        }
    }
}
