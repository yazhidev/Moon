package com.yazhi1992.moon.dialog;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.DeleteItemViewBinder;
import com.yazhi1992.moon.adapter.base.CustomMultitypeAdapter;
import com.yazhi1992.moon.databinding.DialogDeleteBinding;
import com.yazhi1992.moon.viewmodel.DeleteItemBean;

import me.drakeet.multitype.Items;

/**
 * Created by zengyazhi on 2018/1/26.
 */

public class DeleteDialog extends DialogFragment {
    DialogDeleteBinding mBinding;
    CustomMultitypeAdapter mAdapter;
    private Items mItems;
    private OnClickDeleteListener mOnClickDeleteListener;

    public void setOnClickDeleteListener(OnClickDeleteListener onClickDeleteListener) {
        mOnClickDeleteListener = onClickDeleteListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_delete, null, false);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new CustomMultitypeAdapter();
        DeleteItemViewBinder deketeItemViewBinder = new DeleteItemViewBinder();
        deketeItemViewBinder.setOnClickListener(position -> {
            //删除
            DeleteItemBean bean = (DeleteItemBean) mItems.get(position);
            handleAction(bean.getAcction());
        });
        mAdapter.register(DeleteItemBean.class, deketeItemViewBinder);
        mItems = new Items();
        mItems.add(new DeleteItemBean(DeleteItemBean.DELETE, getString(R.string.delete_dialog)));
        mItems.add(new DeleteItemBean(DeleteItemBean.CANCEL, getString(R.string.delete_dialog_cancel)));
        mAdapter.setItems(mItems);
        mBinding.ry.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mBinding.ry.setAdapter(mAdapter);
        mBinding.ry.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    public interface OnClickDeleteListener {
        void onDelete();
    }

    private void handleAction(String action) {
        switch (action) {
            case DeleteItemBean.DELETE:
                if(mOnClickDeleteListener != null) {
                    mOnClickDeleteListener.onDelete();
                }
                dismiss();
                break;
            case DeleteItemBean.CANCEL:
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置 dialog 的背景为 null
        getDialog().getWindow().setBackgroundDrawable(null);
        getDialog().setCanceledOnTouchOutside(true);
    }

    public void show(FragmentManager manager) {
        manager.executePendingTransactions();
        if (!isAdded()) {
            show(manager, AddDialog.class.getName());
        }
    }
}
