package com.yazhi1992.moon.ui.startup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.constant.TableConstant;
import com.yazhi1992.moon.data.CheckIsBindLoverFilter;
import com.yazhi1992.moon.data.CheckIsLoginFilter;
import com.yazhi1992.moon.data.CheckIsSetGenderFilter;
import com.yazhi1992.moon.data.CheckUserDataChain;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.util.PushManager;

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
        CheckUserDataChain.getInstance().reset();
        CheckUserDataChain.getInstance().add(new CheckIsLoginFilter());
        CheckUserDataChain.getInstance().add(new CheckIsBindLoverFilter());
        CheckUserDataChain.getInstance().add(new CheckIsSetGenderFilter());
        CheckUserDataChain.getInstance().processChain();
    }
}
