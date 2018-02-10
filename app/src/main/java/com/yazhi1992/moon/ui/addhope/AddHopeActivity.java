package com.yazhi1992.moon.ui.addhope;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.databinding.ActivityAddHopeBinding;
import com.yazhi1992.moon.dialog.FinishHopeDialog;
import com.yazhi1992.moon.event.AddDataEvent;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.util.EditDataHelper;
import com.yazhi1992.moon.util.PushManager;
import com.yazhi1992.moon.util.TipDialogHelper;
import com.yazhi1992.moon.viewmodel.HopeItemDataBean;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;

@Route(path = ActivityRouter.ADD_HOPE)
public class AddHopeActivity extends BaseActivity {

    private ActivityAddHopeBinding mBinding;
    private AddHopePresenter mPresenter = new AddHopePresenter();
    @Autowired(name = ActivityRouter.KeyName.EDIT_MODE)
    boolean mIsEditMode;
    private HopeItemDataBean mEditData;
    private FinishHopeDialog mFinishHopeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_hope);

        initToolBar(mBinding.toolbar);

        //显示软键盘
        LibUtils.showKeyoard(this, mBinding.etTitle);

        if (mIsEditMode) {
            //编辑模式
            if (EditDataHelper.getInstance().getData() instanceof HopeItemDataBean) {
                mEditData = (HopeItemDataBean) EditDataHelper.getInstance().getData();
            }
            mBinding.btnComfirm.setText(getString(R.string.save_text));
            if (mEditData != null) {
                mBinding.etTitle.setText(mEditData.getTitle());
                mBinding.etTitle.setSelection(mBinding.etTitle.getText().toString().length());
                mBinding.ratingbar.setCountSelected(mEditData.getLevel());
                mBinding.etLink.setText(mEditData.getLink());
                mBinding.etLink.setSelection(mBinding.etLink.getText().toString().length());
                mBinding.btnDelete.setVisibility(View.VISIBLE);
                mBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TipDialogHelper.getInstance().showDialog(AddHopeActivity.this, getString(R.string.delete_data), new TipDialogHelper.OnComfirmListener() {
                            @Override
                            public void comfirm() {
                                mBinding.btnDelete.setLoading(true);
                                //删除
                                mPresenter.delete(mEditData.getObjectId(), new DataCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean data) {
                                        EventBus.getDefault().post(new AddDataEvent(ActionConstant.ADD_HOPE));
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
        }
        mBinding.btnComfirm.setOnClickListener(v -> {
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
            String link = mBinding.etLink.getText().toString();
            if (mIsEditMode) {
                if (mEditData != null) {
                    if (title.equals(mEditData.getTitle())
                            && level == mEditData.getLevel()
                            && mEditData.getLink().equals(link)) {
                        finish();
                        return;
                    }
                    mBinding.btnComfirm.setLoading(true);
                    mPresenter.edit(mEditData.getObjectId(), title, level, link, new DataCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean data) {
                            mEditData.setTitle(title);
                            mEditData.setLevel(level);
                            mEditData.setLink(link);

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
                mPresenter.addHope(title, level, mBinding.etLink.getText().toString(), new DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        EventBus.getDefault().post(new AddDataEvent(ActionConstant.ADD_HOPE));
                        LibUtils.hideKeyboard(mBinding.etTitle);
                        mBinding.btnComfirm.setLoading(false);
                        finish();
                        PushManager.getInstance().pushAction(ActionConstant.ADD_HOPE);
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        mBinding.btnComfirm.setLoading(false);
                    }
                });
            }
        });
    }

    //添加右上角加完成按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mIsEditMode) {
            getMenuInflater().inflate(R.menu.finish_hope, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_finish_hope:
                if(mFinishHopeDialog == null) {
                    mFinishHopeDialog = new FinishHopeDialog();
                    mFinishHopeDialog.setOnFinishListener(new FinishHopeDialog.OnFinishListener() {
                        @Override
                        public void onFinish(String content) {
                            mEditData.setFinishContent(content);
                            mEditData.setStatus(1);
                            finish();
                        }
                    });
                }
                mFinishHopeDialog.showDialog(getFragmentManager(), mEditData.getObjectId());
                break;
            default:
                break;
        }
        return true;
    }
}
