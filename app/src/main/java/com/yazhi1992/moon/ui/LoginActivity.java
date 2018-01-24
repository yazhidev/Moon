package com.yazhi1992.moon.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.sns.SNS;
import com.avos.sns.SNSBase;
import com.avos.sns.SNSCallback;
import com.avos.sns.SNSException;
import com.avos.sns.SNSType;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        mBinding.igQqLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithQQ();
            }
        });
    }

    // 1、定义一个 ThirdPartyType 变量
    private SNSType ThirdPartyType;
    // 2、定义一个 callback，用来接收授权后的数据
    final SNSCallback myCallback = new SNSCallback() {
        @Override
        public void done(SNSBase object, SNSException e) {
            if (e == null) {
                SNS.loginWithAuthData(object.userInfo(), new LogInCallback<AVUser>() {
                    @Override
                    public void done(AVUser avUser, AVException e) {
                        // 5、关联成功，已在 _User 表新增一条用户数据
                        Log.e("zyz", "done");
                    }
                });
            } else {
                e.printStackTrace();
            }
        }
    };

    private void loginWithQQ() {
        try {
            ThirdPartyType = SNSType.AVOSCloudSNSQQ;
            SNS.setupPlatform(this, SNSType.AVOSCloudSNSQQ, "1106624697", "IeOM0d7nzg2uIAps", "https://leancloud.cn/1.1/sns/callback/ypt2m1s8xla33l8o");
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
