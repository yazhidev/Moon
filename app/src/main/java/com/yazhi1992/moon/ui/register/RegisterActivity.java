package com.yazhi1992.moon.ui.register;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.avos.avoscloud.AVUser;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.SPKeyConstant;
import com.yazhi1992.moon.constant.TableConstant;
import com.yazhi1992.moon.databinding.ActivityRegisterBinding;
import com.yazhi1992.moon.event.RegisterEvent;
import com.yazhi1992.moon.sql.IDaoOperationListener;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.util.PushManager;
import com.yazhi1992.moon.util.TipDialogHelper;
import com.yazhi1992.yazhilib.utils.LibSPUtils;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 第一步，输入账号密码并注册
 */

@Route(path = ActivityRouter.REGISTER_STEP_ONE)
public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding mBinding;
    private RegisterPresenter mPresenter = new RegisterPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        LibUtils.showKeyoard(this, mBinding.etAccount);

        mBinding.btnComfirm.setOnClickListener(v -> {
            String accout = mBinding.etAccount.getText().toString();
            if (accout.isEmpty() || !accout.contains("@")) {
                LibUtils.showToast(this, getString(R.string.error_email));
                return;
            }
            String pwd = mBinding.etPwd.getText().toString();
            if (pwd.isEmpty()) {
                LibUtils.showToast(this, getString(R.string.empty_pwd));
                return;
            }
            String nickname = mBinding.etNickname.getText().toString();
            if (nickname.isEmpty()) {
                LibUtils.showToast(this, getString(R.string.empty_nickname));
                return;
            }
            TipDialogHelper.getInstance().showDialog(this, String.format(getString(R.string.comfirm_email), accout), new TipDialogHelper.OnComfirmListener() {
                @Override
                public void comfirm() {
                    mBinding.btnComfirm.setLoading(true);
                    mPresenter.register(accout, pwd, nickname, new DataCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean data) {
                            AVUser avUser = AVUser.getCurrentUser();
                            //插入数据库
                            UserDaoUtil userDaoUtil = new UserDaoUtil();
                            User user = new User();
                            user.setName(avUser.getString(TableConstant.AVUserClass.NICK_NAME));
                            user.setInviteNumber(avUser.getString(TableConstant.AVUserClass.INVITE_NUMBER));
                            user.setObjectId(avUser.getObjectId());
                            user.setEmail(accout);
                            if (LibUtils.notNullNorEmpty(avUser.getString(TableConstant.AVUserClass.LOVER_ID))) {
                                user.setHaveLover(true);
                                user.setLoverId(avUser.getString(TableConstant.AVUserClass.LOVER_ID));
                            }
                            userDaoUtil.insert(user, null);
                            PushManager.getInstance().register();
                            //前往验证邮箱
                            ActivityRouter.gotoSetEmail(true);
                            mBinding.btnComfirm.setLoading(false);
                            //通知登录页更新记住的账号
                            LibSPUtils.setString(SPKeyConstant.LOGIN_ACCOUNT, accout);
                            LibSPUtils.setString(SPKeyConstant.LOGIN_PWD, "");
                            EventBus.getDefault().post(new RegisterEvent());
                        }

                        @Override
                        public void onFailed(int code, String msg) {
                            mBinding.btnComfirm.setLoading(false);
                        }
                    });
                }
            });
        });
    }
}
