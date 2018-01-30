package com.yazhi1992.moon.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.yazhi1992.moon.R;
import com.yazhi1992.yazhilib.utils.StatusBarUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Created by zengyazhi on 2018/1/30.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.with(this)
                .setColor(getResources().getColor(R.color.colorAccent))
                .init();
    }

    protected void initToolBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable.timer(500, TimeUnit.MILLISECONDS)
                        .subscribe(aLong -> finish());
            }
        });
    }
}
