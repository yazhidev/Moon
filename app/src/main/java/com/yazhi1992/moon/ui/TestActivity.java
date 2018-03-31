package com.yazhi1992.moon.ui;

import android.animation.ObjectAnimator;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.databinding.ActivityTestBinding;
import com.yazhi1992.moon.ui.mc.McDetailPresenter;
import com.yazhi1992.moon.widget.calendarview.CalendarUtil;
import com.yazhi1992.moon.widget.calendarview.Calendarview;
import com.yazhi1992.moon.widget.calendarview.DateBean;
import com.yazhi1992.moon.widget.calendarview.InitCallback;
import com.yazhi1992.moon.widget.calendarview.OnPagerChangeListener;
import com.yazhi1992.moon.widget.calendarview.OnSingleChooseListener;

import java.util.Calendar;
import java.util.Date;

@Route(path = ActivityRouter.NEW_MC_DETAIL)
public class TestActivity extends AppCompatActivity {

    private TextView mViewById1;
    private ActivityTestBinding mBinding;
    private DateBean mDateBean;
    private Calendarview mCalendarView;

    private McDetailPresenter mPresenter = new McDetailPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test);

        mCalendarView = findViewById(R.id.calendar);
        mCalendarView.setInitCallback(new InitCallback() {
            @Override
            public void onInit(int[] date, int movePx) {
                mBinding.tv.setText(date[0] + "-" + date[1]);
                anim(movePx);
            }
        });

        mCalendarView.setPagerChangeListener(new OnPagerChangeListener() {
            @Override
            public void onPagerChanged(int[] date) {
                mBinding.tv.setText(date[0] + "-" + date[1]);
            }

            @Override
            public void onPageScrollStateChanged(int state, int movePx) {
                anim(movePx);
            }

        });

        mBinding.rbCome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateBean.setMcType(DateBean.MC_COME);
                mCalendarView.refresh(mDateBean);
            }
        });

        mBinding.rbGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateBean.setMcType(DateBean.MC_GO);
                mCalendarView.refresh(mDateBean);
            }
        });

        mBinding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addMcAction(TypeConstant.MC_COME
                        , mDateBean.getDate()[0]
                        , mDateBean.getDate()[1]
                        , mDateBean.getDate()[2]
                        , mDateBean.getTime(), new DataCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean data) {

                            }

                            @Override
                            public void onFailed(int code, String msg) {

                            }
                        });
            }
        });

        mCalendarView.setSingleChooseListener(new OnSingleChooseListener() {
            @Override
            public void onSingleChoose(View view, DateBean date, int position) {

                mDateBean = date;

                switch (date.getMcType()) {
                    case DateBean.MC_GO:
                        mBinding.btnUpdate.setText("取消已走");
                        mBinding.rbGo.setChecked(true);
                        mBinding.rbGo.setText("已走");
                        break;
                    case DateBean.MC_COME:
                        mBinding.btnUpdate.setText("取消已来");
                        mBinding.rbCome.setText("已来");
                        mBinding.rbCome.setChecked(true);
                        break;
                    case DateBean.MC_MIDDLE:
                        mBinding.btnUpdate.setText("不可操作");
                        mBinding.rbGo.setText("走");
                        mBinding.rbCome.setText("来");

                        mBinding.rbGo.setEnabled(false);
                        mBinding.rbCome.setEnabled(false);
                        break;
                    default:
                        mBinding.btnUpdate.setText("空");

                        mBinding.rbGo.setText("走");
                        mBinding.rbCome.setText("来");

                        mBinding.rbCome.setChecked(false);
                        mBinding.rbGo.setChecked(false);
                        break;
                }
            }
        });
    }

    private void anim(int movePx) {
        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(mBinding.rl, "translationY", movePx);
        valueAnimator.setDuration(500);
        valueAnimator.start();
    }
}
