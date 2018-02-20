package com.yazhi1992.moon.ui.newlogin;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.SPKeyConstant;
import com.yazhi1992.moon.constant.TableConstant;
import com.yazhi1992.moon.data.CheckUserDataChain;
import com.yazhi1992.moon.databinding.ActivityNewLoginBinding;
import com.yazhi1992.moon.dialog.LoadingHelper;
import com.yazhi1992.moon.event.ChangePwd;
import com.yazhi1992.moon.event.ChangeUserInfo;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.util.PushManager;
import com.yazhi1992.yazhilib.utils.LibSPUtils;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@Route(path = ActivityRouter.NEW_LOGIN)
public class NewLoginActivity extends BaseActivity {

    private ActivityNewLoginBinding mBinding;
    private LoginPresenter mPresenter = new LoginPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_login);
        initToolBar(mBinding.toolbar);

        String oldPwd = LibSPUtils.getString(SPKeyConstant.LOGIN_PWD, "");
        if(LibUtils.notNullNorEmpty(oldPwd)) {
            mBinding.etPwd.setText(oldPwd);
            mBinding.etPwd.setSelection(oldPwd.length());
        } else {
            LibUtils.showKeyoard(this, mBinding.etPwd);
        }

        String oldAccount = LibSPUtils.getString(SPKeyConstant.LOGIN_ACCOUNT, "");
        if(LibUtils.notNullNorEmpty(oldAccount)) {
            mBinding.etAccount.setText(oldAccount);
            mBinding.etAccount.setSelection(oldAccount.length());
        } else {
            LibUtils.showKeyoard(this, mBinding.etAccount);
        }

        mBinding.cbSavePwd.setChecked(LibSPUtils.getBoolean(SPKeyConstant.LOGIN_REMEMBER_PWD_ENABLE, true));
        mBinding.cbSavePwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    //取消记住密码
                    LibSPUtils.setString(SPKeyConstant.LOGIN_PWD, "");
                }
                LibSPUtils.setBoolean(SPKeyConstant.LOGIN_REMEMBER_PWD_ENABLE, isChecked);
            }
        });

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
            LibSPUtils.setString(SPKeyConstant.LOGIN_ACCOUNT, accout);
            if(mBinding.cbSavePwd.isChecked()) {
                //记住账号密码
                LibSPUtils.setString(SPKeyConstant.LOGIN_PWD, pwd);
            }
            mPresenter.login(accout, pwd, new DataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    AVUser avUser = AVUser.getCurrentUser();
                    //插入数据库
                    UserDaoUtil userDaoUtil = new UserDaoUtil();
                    User user = new User();
                    user.setName(avUser.getString(TableConstant.AVUserClass.NICK_NAME));
                    AVFile avFile = avUser.getAVFile(TableConstant.AVUserClass.HEAD_IMG_FILE);
                    if(avFile != null) {
                        user.setHeadUrl(avFile.getUrl());
                    }
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
        mBinding.tvForgetPwd.setOnClickListener(v -> ActivityRouter.gotoFindPwd(mBinding.etAccount.getText().toString()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeUserName(ChangePwd bean) {
        mBinding.etAccount.setText(bean.getEmail());
        mBinding.etPwd.setText("");
        mBinding.etPwd.setSelection(0);
        LibUtils.showKeyoard(this, mBinding.etPwd);
    }

}
