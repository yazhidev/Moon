package com.yazhi1992.moon.ui.startup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.yazhi1992.moon.PageRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.constant.NameContant;
import com.yazhi1992.moon.sql.UserDaoUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Observable.timer(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        start();
                    }
                });
    }

    private void start() {
        UserDaoUtil userDaoUtil = new UserDaoUtil();
        if(userDaoUtil.getUserDao() == null) {
            //未登录
            PageRouter.gotoLogin();
            finish();
        } else {
            if(userDaoUtil.getUserDao().getHaveLover()) {
                //已绑定
                PageRouter.gotoHomePage();
                finish();
            } else {
                AVUser.getCurrentUser().fetchInBackground(new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject object, AVException e) {
                        if(e == null) {
                            if(object.getBoolean(NameContant.AVUserClass.HAVE_LOVER)) {
                                userDaoUtil.updateLoveInfo(object.getString(NameContant.AVUserClass.LOVER_ID)
                                        , object.getString(NameContant.AVUserClass.LOVER_NAME)
                                        , object.getString(NameContant.AVUserClass.LOVER_HEAD_URL));
                                //已绑定
                                PageRouter.gotoHomePage();
                            } else {
                                //未绑定
                                PageRouter.gotoBindLover();
                            }
                        } else {
                            //异常
                            PageRouter.gotoLogin();
                        }
                        finish();
                    }
                });
            }
        }
    }
}
