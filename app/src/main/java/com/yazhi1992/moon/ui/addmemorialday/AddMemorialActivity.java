package com.yazhi1992.moon.ui.addmemorialday;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.databinding.ActivityAddMemorialBinding;
import com.yazhi1992.moon.dialog.DatePickerDialog;
import com.yazhi1992.moon.event.AddHistoryDataEvent;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.util.AppUtils;
import com.yazhi1992.moon.util.PushManager;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.util.Date;

@Route(path = ActivityRouter.ADD_MEMORIAL)
public class AddMemorialActivity extends BaseActivity {

    Date chooseDate = new Date();
    private ActivityAddMemorialBinding mBinding;
    private AddMemorialDayPresenter mPresenter = new AddMemorialDayPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memorial);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_memorial);

        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBinding.tvDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog();
            dialog.show(getFragmentManager());
            dialog.setComfirmlistener(new DatePickerDialog.Comfirmlistener() {
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
        });

        //显示软键盘
        LibUtils.showKeyoard(this, mBinding.etTitle);

        mBinding.tvDate.setText(AppUtils.getTimeStrForMemorialDay(new Date()));

        mBinding.btnComfirm.setOnClickListener(v -> {
            String title = mBinding.etTitle.getText().toString();
            if(title.isEmpty()) {
                mBinding.titleLayout.setError(getString(R.string.add_memorial_empty_title));
                return;
            }
            mBinding.btnComfirm.setLoading(true);
            mPresenter.addMemorialDay(title, chooseDate.getTime(), new DataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    EventBus.getDefault().post(new AddHistoryDataEvent(ActionConstant.ADD_MEMORIAL));
                    LibUtils.hideKeyboard(mBinding.etTitle);
                    mBinding.btnComfirm.setLoading(false);
                    finish();
                    PushManager.getInstance().pushAction(ActionConstant.ADD_MEMORIAL);
                }

                @Override
                public void onFailed(int code, String msg) {
                    LibUtils.showToast(AddMemorialActivity.this, msg);
                    mBinding.btnComfirm.setLoading(false);
                }
            });
        });
    }
}
