package com.yazhi1992.moon.ui.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.sns.SNS;
import com.avos.sns.SNSBase;
import com.avos.sns.SNSCallback;
import com.avos.sns.SNSException;
import com.avos.sns.SNSType;
import com.yazhi1992.moon.BuildConfig;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.activity.AbsUpgrateActivity;
import com.yazhi1992.moon.databinding.ActivityLoginBinding;
import com.yazhi1992.moon.sql.DatabaseManager;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.widget.PageRouter;

import org.json.JSONException;
import org.json.JSONObject;

@Route(path = PageRouter.LOGIN)
public class LoginActivity extends AbsUpgrateActivity {

    private ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        mBinding.igQqLogin.setOnClickListener(v -> loginWithQQ());

        mBinding.btn.setOnClickListener(v -> {
        });
    }

    private SNSType ThirdPartyType;
    final SNSCallback myCallback = new SNSCallback() {
        @Override
        public void done(SNSBase object, SNSException e) {
            if (e == null) {
                JSONObject jsonObject = object.authorizedData();
                if(jsonObject != null) {
                    try {
                        String nickname = object.authorizedData().getString("nickname");
                        SNS.loginWithAuthData(object.userInfo(), new LogInCallback<AVUser>() {
                            @Override
                            public void done(AVUser avUser, AVException e) {
                                //关联成功，已在 _User 表新增一条用户数据
                                //插入数据库
                                User user = new User();
                                user.setName(nickname);
                                user.setObjectId(avUser.getObjectId());
                                DatabaseManager.getInstance().getDaoSession().getUserDao().insert(user);

                                avUser.setUsername(nickname);
                                avUser.saveInBackground();

                                PageRouter.gotoHomePage(LoginActivity.this);
                                finish();
                            }
                        });
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }
            } else {
                e.printStackTrace();
            }
        }
    };

    private void loginWithQQ() {
        try {
            ThirdPartyType = SNSType.AVOSCloudSNSQQ;
            SNS.logout(this, SNSType.AVOSCloudSNSQQ);
            SNS.setupPlatform(this, SNSType.AVOSCloudSNSQQ, BuildConfig.QQ_ID, BuildConfig.QQ_KEY, "");
            SNS.loginWithCallback(this, SNSType.AVOSCloudSNSQQ, myCallback);
        } catch (AVException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 4、在页面 activity 回调里填写 ThirdPartyType
        if (resultCode == RESULT_OK) {
            SNS.onActivityResult(requestCode, resultCode, data, ThirdPartyType);
        }
    }
}
