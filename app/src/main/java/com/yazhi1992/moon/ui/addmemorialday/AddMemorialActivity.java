package com.yazhi1992.moon.ui.addmemorialday;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.databinding.ActivityAddMemorialBinding;
import com.yazhi1992.moon.dialog.DatePickerDialog;
import com.yazhi1992.moon.event.AddDataEvent;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.util.AppUtils;
import com.yazhi1992.moon.util.EditDataHelper;
import com.yazhi1992.moon.util.PushManager;
import com.yazhi1992.moon.util.TipDialogHelper;
import com.yazhi1992.moon.viewmodel.MemorialDayBean;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.util.Date;

@Route(path = ActivityRouter.ADD_MEMORIAL)
public class AddMemorialActivity extends BaseActivity {

    Date chooseDate = new Date();
    private ActivityAddMemorialBinding mBinding;
    private AddMemorialDayPresenter mPresenter = new AddMemorialDayPresenter();
    @Autowired(name = ActivityRouter.KeyName.EDIT_MODE)
    boolean mIsEditMode;
    private MemorialDayBean mEditData;
    private DatePickerDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memorial);
        ARouter.getInstance().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_memorial);

        initToolBar(mBinding.toolbar);

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
                if (mIsEditMode) {
                    mDialog.show(getFragmentManager(), mEditData.getTime());
                    return;
                }
            }
            mDialog.show(getFragmentManager());
        });

        //显示软键盘
        LibUtils.showKeyoard(this, mBinding.etTitle);

        if (mIsEditMode) {
            //编辑模式
            if (EditDataHelper.getInstance().getData() instanceof MemorialDayBean) {
                mEditData = (MemorialDayBean) EditDataHelper.getInstance().getData();
            }
            mBinding.btnComfirm.setText(getString(R.string.save_text));
            if (mEditData != null) {
                mBinding.etTitle.setText(mEditData.getTitle());
                mBinding.etTitle.setSelection(mBinding.etTitle.getText().toString().length());
                chooseDate = new Date(mEditData.getTime());
                mBinding.tvDate.setText(mEditData.getTimeStr());
                mBinding.btnDelete.setVisibility(View.VISIBLE);
                mBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TipDialogHelper.getInstance().showDialog(AddMemorialActivity.this, getString(R.string.delete_data), new TipDialogHelper.OnComfirmListener() {
                            @Override
                            public void comfirm() {
                                mBinding.btnDelete.setLoading(true);
                                //删除倒数日
                                mPresenter.delete(mEditData.getObjectId(), new DataCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean data) {
                                        EventBus.getDefault().post(new AddDataEvent(ActionConstant.ADD_MEMORIAL));
                                        mBinding.btnDelete.setLoading(false);
                                        EditDataHelper.getInstance().saveData(null);
                                        finish();
                                    }

                                    @Override
                                    public void onFailed(int code, String msg) {
                                        mBinding.btnDelete.setLoading(false);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        } else {
            mBinding.tvDate.setText(AppUtils.getTimeStrForMemorialDay(new Date()));
        }

        mBinding.btnComfirm.setOnClickListener(v -> {
            String title = mBinding.etTitle.getText().toString();
            if (title.isEmpty()) {
                mBinding.titleLayout.setError(getString(R.string.add_memorial_empty_title));
                return;
            }
            if (mIsEditMode) {
                if (mEditData != null) {
                    if (title.equals(mEditData.getTitle()) && chooseDate.getTime() == mEditData.getTime()) {
                        finish();
                        return;
                    }
                    mBinding.btnComfirm.setLoading(true);
                    mPresenter.edit(mEditData.getObjectId(), title, chooseDate.getTime(), new DataCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean data) {
                            mEditData.setTitle(title);
                            mEditData.setTime(chooseDate.getTime());

                            LibUtils.hideKeyboard(mBinding.etTitle);
                            mBinding.btnComfirm.setLoading(false);
                            finish();
                        }

                        @Override
                        public void onFailed(int code, String msg) {
                            mBinding.btnComfirm.setLoading(false);
                        }
                    });

                }
            } else {
                mBinding.btnComfirm.setLoading(true);
                mPresenter.addMemorialDay(title, chooseDate.getTime(), new DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        EventBus.getDefault().post(new AddDataEvent(ActionConstant.ADD_MEMORIAL));
                        LibUtils.hideKeyboard(mBinding.etTitle);
                        mBinding.btnComfirm.setLoading(false);
                        finish();
                        PushManager.getInstance().pushAction(ActionConstant.ADD_MEMORIAL);
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        mBinding.btnComfirm.setLoading(false);
                    }
                });
            }
        });
    }
}
