package com.yazhi1992.moon.ui.mc;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.databinding.ActivityMcDetailBinding;
import com.yazhi1992.moon.dialog.DatePickerDialog;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.util.AppUtils;
import com.yazhi1992.moon.util.PushManager;
import com.yazhi1992.moon.viewmodel.McBean;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.util.Date;

@Route(path = ActivityRouter.MC_DETAIL)
public class McDetailActivity extends BaseActivity {

    // TODO: 2018/2/12 全局配置表（是都显示mc，mc结束后25天是否提示）
    Date chooseDate = new Date();
    private ActivityMcDetailBinding mBinding;
    private DatePickerDialog mDialog;
    private McDetailPresenter mPresenter = new McDetailPresenter();
    private McDetailViewModel mModel = new McDetailViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_mc_detail);

        initToolBar(mBinding.toolbar);
        mModel.mGender.set(new UserDaoUtil().getUserDao().getGender());
        mBinding.setItem(mModel);

        mBinding.tvDate.setText(AppUtils.getTimeStrForMemorialDay(chooseDate));

        mBinding.tvDate.setOnClickListener(v -> {
            if (mDialog == null) {
                mDialog = new DatePickerDialog();
                mDialog.setComfirmlistener(new DatePickerDialog.Comfirmlistener() {
                    @Override
                    public void comfirm(String timeStr) {
                        try {
                            chooseDate = AppUtils.memorialDayYmdFormat.parse(timeStr);
                            mBinding.tvDate.setText(AppUtils.getTimeStrForMemorialDay(chooseDate));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            mDialog.show(getFragmentManager());
        });

        mPresenter.getData(new DataCallback<McData>() {
            @Override
            public void onSuccess(McData data) {
                mModel.mFetching.set(false);
                if(data != null) {
                    mModel.mStatus.set(data.getStatus());
                    mModel.mGapDayNumStr.set(data.getGapDayNumStr());
                    mModel.mTimeStr.set(data.getTimeStr());
                } else {
                    mModel.mStatus.set(0);
                    mModel.mEmpty.set(true);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                // TODO: 2018/2/16 错误重试视图
            }
        });

        mBinding.btnSave.setOnClickListener(v -> {
            if(mModel != null) {
                int setStatus = 1 - mModel.mStatus.get();
                mBinding.btnSave.setLoading(true);
                mPresenter.updateMcStatus(setStatus, chooseDate.getTime(), new DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        mBinding.btnSave.setLoading(false);
                        LibUtils.showToast(McDetailActivity.this, setStatus == 0 ? "撒花~愉快地玩耍吧~" : "要注意休息，保重身体哦~");
                        finish();
                        PushManager.getInstance().pushAction(ActionConstant.UPDATE_MC);
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        mBinding.btnSave.setLoading(false);
                    }
                });
            }
        });
    }
}
