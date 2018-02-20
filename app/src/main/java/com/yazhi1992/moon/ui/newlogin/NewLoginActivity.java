package com.yazhi1992.moon.ui.newlogin;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.avos.avoscloud.AVUser;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.TableConstant;
import com.yazhi1992.moon.data.CheckUserDataChain;
import com.yazhi1992.moon.databinding.ActivityNewLoginBinding;
import com.yazhi1992.moon.dialog.LoadingHelper;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.util.PushManager;
import com.yazhi1992.yazhilib.utils.LibUtils;

@Route(path = ActivityRouter.NEW_LOGIN)
public class NewLoginActivity extends BaseActivity {

    private ActivityNewLoginBinding mBinding;
    private LoginPresenter mPresenter = new LoginPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_login);
        initToolBar(mBinding.toolbar);

        LibUtils.showKeyoard(this, mBinding.etAccount);

        mBinding.etAccount.setText("yazhiwork@163.com");

        mBinding.btnComfirm.setOnClickListener(v -> {
            String accout = mBinding.etAccount.getText().toString();
            if(accout.isEmpty() || !accout.contains("@")) {
                LibUtils.showToast(this, getString(R.string.error_email));
                return;
            }
            String pwd = mBinding.etPwd.getText().toString();
            if(pwd.isEmpty()) {
                LibUtils.showToast(this, getString(R.string.empty_pwd));
                return;
            }
            mBinding.btnComfirm.setLoading(true);
            mPresenter.login(accout, pwd, new DataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    AVUser avUser = AVUser.getCurrentUser();
                    //插入数据库
                    UserDaoUtil userDaoUtil = new UserDaoUtil();
                    User user = new User();
                    user.setName(avUser.getUsername());
                    user.setHeadUrl(avUser.getString(TableConstant.AVUserClass.HEAD_URL));
                    user.setInviteNumber(avUser.getString(TableConstant.AVUserClass.INVITE_NUMBER));
                    user.setObjectId(avUser.getObjectId());
                    user.setEmail(accout);
                    user.setEmailVerified(avUser.getBoolean(TableConstant.AVUserClass.EMAIL_VERIFIED));
                    if (LibUtils.notNullNorEmpty(avUser.getString(TableConstant.AVUserClass.LOVER_ID))) {
                        user.setHaveLover(true);
                        user.setLoverId(avUser.getString(TableConstant.AVUserClass.LOVER_ID));
                    }
                    userDaoUtil.insert(user, null);
                    PushManager.getInstance().register();
                    //继续检查下一个
                    CheckUserDataChain.getInstance().processChain();
                    mBinding.btnComfirm.setLoading(false);
                }

                @Override
                public void onFailed(int code, String msg) {
                    mBinding.btnComfirm.setLoading(false);
                }
            });
        });

        mBinding.tvRegister.setOnClickListener(v -> ActivityRouter.gotRegister1());
    }
}
