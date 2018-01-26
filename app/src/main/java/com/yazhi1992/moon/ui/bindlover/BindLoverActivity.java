package com.yazhi1992.moon.ui.bindlover;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.databinding.ActivityBindLoverBinding;
import com.yazhi1992.moon.widget.PageRouter;

/**
 * Created by zengyazhi on 2018/1/26.
 */

@Route(path = PageRouter.BIND_LOVER)
public class BindLoverActivity extends AppCompatActivity {

    private ActivityBindLoverBinding mBinding;
    private BindLoverPresenter mPresenter = new BindLoverPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_bind_lover);

        mBinding.tvInviteNum.setText(mPresenter.getInviteNum());


    }
}
