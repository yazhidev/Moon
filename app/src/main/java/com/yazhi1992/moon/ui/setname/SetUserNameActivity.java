package com.yazhi1992.moon.ui.setname;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.databinding.ActivitySetUserNameBinding;
import com.yazhi1992.moon.event.ChangeUserInfo;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;

@Route(path = ActivityRouter.SET_USER_NAME)
public class SetUserNameActivity extends BaseActivity {

    private ActivitySetUserNameBinding mBinding;
    @Autowired(name = ActivityRouter.KeyName.USER_NAME)
    String username;
    private SetUserNamePresenter mPresenter = new SetUserNamePresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_set_user_name);

        initToolBar(mBinding.toolbar);

        //显示软键盘
        LibUtils.showKeyoard(this, mBinding.etInput);

        mBinding.etInput.setText(username);
        mBinding.etInput.setSelection(mBinding.etInput.getText().toString().length());

        mBinding.btnComfirm.setOnClickListener(v -> {
            String name = mBinding.etInput.getText().toString();
            if(name.isEmpty()) {
                LibUtils.showToast(this, getString(R.string.add_text_empty));
                return;
            }
            mBinding.btnComfirm.setLoading(true);
            mPresenter.setUserName(name, new DataCallback<String>() {
                @Override
                public void onSuccess(String userName) {
                    finish();
                    new UserDaoUtil().updateUserName(userName);
                    ChangeUserInfo changeUserInfo = new ChangeUserInfo();
                    changeUserInfo.setName(userName);
                    EventBus.getDefault().post(changeUserInfo);
                    mBinding.btnComfirm.setLoading(false);
                }

                @Override
                public void onFailed(int code, String msg) {
                    mBinding.btnComfirm.setLoading(false);
                }
            });
        });
    }
}
