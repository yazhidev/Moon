package com.yazhi1992.moon.ui.addhope;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.databinding.ActivityAddHopeBinding;
import com.yazhi1992.moon.event.AddHistoryDataEvent;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.util.PushManager;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;

@Route(path = ActivityRouter.ADD_HOPE)
public class AddHopeActivity extends BaseActivity {

    private ActivityAddHopeBinding mBinding;
    private AddHopePresenter mPresenter = new AddHopePresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_hope);

        initToolBar(mBinding.toolbar);

        //显示软键盘
        LibUtils.showKeyoard(this, mBinding.etTitle);

        mBinding.btnAdd.setOnClickListener(v -> {
            String title = mBinding.etTitle.getText().toString();
            if(title.isEmpty()) {
                mBinding.titleLayout.setError(getString(R.string.add_hope_empty_title));
                return;
            } else {
                mBinding.titleLayout.setError("");
            }
            int level = mBinding.ratingbar.getCountSelected();
            if(level == 0) {
                LibUtils.showToast(this, getString(R.string.add_tip_choose_level));
                return;
            }
            mBinding.btnAdd.setLoading(true);
            mPresenter.addHope(title, level, new DataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    EventBus.getDefault().post(new AddHistoryDataEvent(ActionConstant.ADD_HOPE));
                    LibUtils.hideKeyboard(mBinding.etTitle);
                    mBinding.btnAdd.setLoading(false);
                    finish();
                    PushManager.getInstance().pushAction(ActionConstant.ADD_HOPE);
                }

                @Override
                public void onFailed(int code, String msg) {
                    mBinding.btnAdd.setLoading(false);
                }
            });
        });
    }
}
