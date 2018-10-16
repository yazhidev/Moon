package com.yazhi1992.moon.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.databinding.LayoutMultiStatusBinding;

/**
 * Created by zengyazhi on 2018/3/8.
 */

public class MultiStatusView extends RelativeLayout{

    private MultiStatusViewModel mViewModel = new MultiStatusViewModel();
    private LayoutMultiStatusBinding mBinding;

    public MultiStatusView(Context context) {
        this(context, null);
    }

    public MultiStatusView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_multi_status, this, true);
        mBinding.setData(mViewModel);
    }

    public MultiStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void showEmpty() {
        setVisibility(VISIBLE);
        mViewModel.status.set(MultiStatusViewModel.EMPTY);
    }

    public void showNetErr() {
        setVisibility(VISIBLE);
        mViewModel.status.set(MultiStatusViewModel.NET_ERR);
    }

    public void showNormal() {
        setVisibility(GONE);
    }

}
