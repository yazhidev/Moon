package com.yazhi1992.moon.dialog;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.databinding.DialogFinishHopeBinding;
import com.yazhi1992.moon.event.AddDataEvent;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zengyazhi on 2018/1/26.
 */

public class FinishHopeDialog extends DialogFragment {

    DialogFinishHopeBinding mBinding;
    private String mHopeId;
    private OnFinishListener mOnFinishListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_finish_hope, null, false);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mBinding.etInput.getText().toString();
                if(content.isEmpty()) {
                    LibUtils.showToast(view.getContext(), getString(R.string.finish_hope_hint_empty));
                    return;
                }
                mBinding.btnFinish.setLoading(true);
                Api.getInstance().finishHope(mHopeId, content, new DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        mBinding.btnFinish.setLoading(false);
                        //通知刷新
                        EventBus.getDefault().post(new AddDataEvent(ActionConstant.ADD_HOPE));
                        if(mOnFinishListener != null) {
                            mOnFinishListener.onFinish(content);
                        }
                        dismiss();
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        mBinding.btnFinish.setLoading(false);
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置 dialog 的背景为 null
        getDialog().getWindow().setBackgroundDrawable(null);
        getDialog().setCanceledOnTouchOutside(false);
    }

    public interface OnFinishListener {
        void onFinish(String content);
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        mOnFinishListener = onFinishListener;
    }

    public void showDialog(FragmentManager manager, String hopeId) {
        mHopeId = hopeId;
        manager.executePendingTransactions();
        if (!isAdded()) {
            show(manager, FinishHopeDialog.class.getName());
        }
    }
}
