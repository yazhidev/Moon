package com.yazhi1992.moon.ui;

import android.animation.ObjectAnimator;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.databinding.ActivityTestBinding;
import com.yazhi1992.moon.widget.calendarview.Calendarview;
import com.yazhi1992.moon.widget.calendarview.DateBean;
import com.yazhi1992.moon.widget.calendarview.OnPagerChangeListener;
import com.yazhi1992.moon.widget.calendarview.OnSingleChooseListener;

public class TestActivity extends AppCompatActivity {

    private TextView mViewById1;
    private ActivityTestBinding mBinding;
    private DateBean mDateBean;
    private Calendarview mCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test);

        mCalendarView = findViewById(R.id.calendar);
        mCalendarView.setPagerChangeListener(new OnPagerChangeListener() {
            @Override
            public void onPagerChanged(int[] date) {
                mBinding.tv.setText(date[0] + "-" + date[1]);
            }

            @Override
            public void onPageScrollStateChanged(int state, int movePx) {
                ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(mBinding.rl, "translationY", movePx);
                valueAnimator.setDuration(500);
                valueAnimator.start();
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

        mCalendarView.setSingleChooseListener(new OnSingleChooseListener() {
            @Override
            public void onSingleChoose(View view, DateBean date, int position) {

                mDateBean = date;

                switch (date.getMcType()) {
                    case DateBean.MC_GO:
                        mBinding.rbGo.setChecked(true);
                        mBinding.rbGo.setText("已走");
                        break;
                    case DateBean.MC_COME:
                        mBinding.rbCome.setText("已来");
                        mBinding.rbCome.setChecked(true);
                        break;
                    case DateBean.MC_MIDDLE:
                        mBinding.rbGo.setText("走");
                        mBinding.rbCome.setText("来");

                        mBinding.rbGo.setEnabled(false);
                        mBinding.rbCome.setEnabled(false);
                        break;
                    default:
                        mBinding.rbGo.setText("走");
                        mBinding.rbCome.setText("来");

                        mBinding.rbCome.setChecked(false);
                        mBinding.rbGo.setChecked(false);
                        break;
                }
            }
        });
    }
}
