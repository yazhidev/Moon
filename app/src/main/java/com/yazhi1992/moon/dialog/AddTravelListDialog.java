package com.yazhi1992.moon.dialog;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.databinding.DialogAddTravelListBinding;
import com.yazhi1992.yazhilib.utils.LibUtils;

/**
 * Created by zengyazhi on 2018/1/26.
 */

public class AddTravelListDialog extends DialogFragment {

    DialogAddTravelListBinding mBinding;
    private OnFinishListener mOnFinishListener;
    private String mText;
    private String mObjId;

    public static AddTravelListDialog getInstance(String text, String objId) {
        AddTravelListDialog addTravelListDialog = new AddTravelListDialog();
        addTravelListDialog.setText(text);
        addTravelListDialog.setObjId(objId);
        return addTravelListDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_add_travel_list, null, false);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(!TextUtils.isEmpty(mText)) {
            mBinding.etInput.setText(mText);
            mBinding.btnFinish.setText("提交修改");
        }

        //显示软键盘
        LibUtils.showKeyoard(view.getContext(), mBinding.etInput);

        mBinding.btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mBinding.etInput.getText().toString();
                if(content.isEmpty()) {
                    LibUtils.showToast(view.getContext(), getString(R.string.finish_hope_hint_empty));
                    return;
                }
                if(content.equals(mText)) {
                    dismiss();
                    return;
                }
                if(TextUtils.isEmpty(mObjId)) {
                    //新增
                    mBinding.btnFinish.setLoading(true);
                    Api.getInstance().addTravelList(content, new DataCallback<String>() {
                        @Override
                        public void onSuccess(String id) {
                            mBinding.btnFinish.setLoading(false);
                            if(mOnFinishListener != null) {
                                mOnFinishListener.onFinish(content, id);
                            }
                            dismiss();
                        }

                        @Override
                        public void onFailed(int code, String msg) {
                            LibUtils.showToast(msg);
                            mBinding.btnFinish.setLoading(false);
                        }
                    });
                } else{
                    //修改
                    mBinding.btnFinish.setLoading(true);
                    Api.getInstance().editTravelList(content, mObjId, new DataCallback<String>() {
                        @Override
                        public void onSuccess(String newContent) {
                            mBinding.btnFinish.setLoading(false);
                            if(mOnFinishListener != null) {
                                mOnFinishListener.onFinish(newContent, mObjId);
                            }
                            dismiss();
                        }

                        @Override
                        public void onFailed(int code, String msg) {
                            LibUtils.showToast(msg);
                            mBinding.btnFinish.setLoading(false);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mBinding.etInput.setText("");
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置 dialog 的背景为 null
        getDialog().getWindow().setBackgroundDrawable(null);
        getDialog().setCanceledOnTouchOutside(false);
    }

    public void setText(String text) {
        mText = text;
    }

    public void setObjId(String objId) {
        mObjId = objId;
    }

    public interface OnFinishListener {
        void onFinish(String content, String id);
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        mOnFinishListener = onFinishListener;
    }

    public void showDialog(FragmentManager manager) {
        manager.executePendingTransactions();
        if (!isAdded()) {
            show(manager, AddTravelListDialog.class.getName());
        }
    }
}
