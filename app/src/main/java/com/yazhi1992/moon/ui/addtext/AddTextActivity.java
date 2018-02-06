package com.yazhi1992.moon.ui.addtext;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.databinding.ActivityAddTextBinding;
import com.yazhi1992.moon.event.AddHistoryDataEvent;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.util.PushManager;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;

@Route(path = ActivityRouter.ADD_TEXT)
public class AddTextActivity extends BaseActivity {

    private ActivityAddTextBinding mBinding;
    private AddTextPresenter mPresenter = new AddTextPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_text);

        initToolBar(mBinding.toolbar);

        //显示软键盘
        LibUtils.showKeyoard(this, mBinding.etTitle);

        mBinding.btnAdd.setOnClickListener(v -> {
            String title = mBinding.etTitle.getText().toString();
            if(title.isEmpty()) {
                LibUtils.showToast(this, getString(R.string.add_text_empty));
                return;
            }
            mBinding.btnAdd.setLoading(true);
            mPresenter.addText(title, new DataCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    EventBus.getDefault().post(new AddHistoryDataEvent(ActionConstant.ADD_TEXT));
                    LibUtils.hideKeyboard(mBinding.etTitle);
                    mBinding.btnAdd.setLoading(false);
                    finish();
                    PushManager.getInstance().pushAction(ActionConstant.ADD_TEXT);
                }

                @Override
                public void onFailed(int code, String msg) {
                    mBinding.btnAdd.setLoading(false);
                }
            });
        });
    }
}
