package com.yazhi1992.moon.ui.mc;

import android.animation.ObjectAnimator;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.databinding.ActivityTestBinding;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.widget.calendarview.Calendarview;
import com.yazhi1992.moon.widget.calendarview.DateBean;
import com.yazhi1992.moon.widget.calendarview.InitCallback;
import com.yazhi1992.moon.widget.calendarview.OnPagerChangeListener;
import com.yazhi1992.moon.widget.calendarview.OnSingleChooseListener;
import com.yazhi1992.yazhilib.utils.LibUtils;

import java.util.List;

@Route(path = ActivityRouter.NEW_MC_DETAIL)
public class McActivity extends AppCompatActivity {

    private ActivityTestBinding mBinding;
    private DateBean mDateBean;
    private Calendarview mCalendarView;
    private McDetailPresenter mPresenter = new McDetailPresenter();
    private McModel mModel = new McModel();
    private int mLastTime;
    private int size = 10;

    // TODO: 2018/4/6

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_test);
        mModel.mGender.set(new UserDaoUtil().getUserDao().getGender());
        mBinding.setItem(mModel);

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
//
//        mBinding.rbCome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDateBean.setMcType(TypeConstant.MC_COME);
//                mCalendarView.refreshViewByDateBean(mDateBean);
//            }
//        });
//
//        mBinding.rbGo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDateBean.setMcType(TypeConstant.MC_GO);
//                mCalendarView.refreshViewByDateBean(mDateBean);
//            }
//        });

        mBinding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBinding.btnUpdate.getText().toString().isEmpty()) return;
                DateBean dateBean = mModel.data.get();
                int type = dateBean.getMcType();
                if(type == TypeConstant.MC_COME || type == TypeConstant.MC_GO) {
                    //本来就是来或去状态，则点击按钮移除现有状态
                    mPresenter.removeMcAction(mDateBean.getDate()[0], mDateBean.getDate()[1], mDateBean.getDate()[2], new DataCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean data) {
                            LibUtils.showToast("remove suc");
                            mCalendarView.rebuildView();
                        }

                        @Override
                        public void onFailed(int code, String msg) {
                            LibUtils.showToast("remove onFailed");
                        }
                    });
                } else {
                    //根据radiobutton提交状态
                    mPresenter.addMcAction(mBinding.rbCome.isChecked() ? TypeConstant.MC_COME: TypeConstant.MC_GO
                            , mDateBean.getDate()[0]
                            , mDateBean.getDate()[1]
                            , mDateBean.getDate()[2]
                            , mDateBean.getTime(), new DataCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean data) {
                                    LibUtils.showToast("add suc");
                                    mCalendarView.rebuildView();
                                }

                                @Override
                                public void onFailed(int code, String msg) {
                                    LibUtils.showToast("add onFailed");
                                }
                            });
                }
            }
        });

        mCalendarView.setSingleChooseListener(new OnSingleChooseListener() {
            @Override
            public void onSingleChoose(View view, DateBean date, int position) {
                mDateBean = date;
                mModel.data.set(mDateBean);
            }
        });

        mPresenter.getMcRecord(mLastTime, size, new DataCallback<List<McDataFromApi>>() {
            @Override
            public void onSuccess(List<McDataFromApi> data) {

            }

            @Override
            public void onFailed(int code, String msg) {

            }
        });
    }

    private void anim(int movePx) {
        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(mBinding.rl, "translationY", movePx);
        valueAnimator.setDuration(500);
        valueAnimator.start();
    }
}