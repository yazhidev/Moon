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

import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.AddItemViewBinder;
import com.yazhi1992.moon.adapter.ItemDialogViewBinder;
import com.yazhi1992.moon.adapter.base.CustomMultitypeAdapter;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.databinding.DialogAddBinding;
import com.yazhi1992.moon.viewmodel.AddItemBean;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.multitype.Items;

/**
 * Created by zengyazhi on 2018/4/25.
 */

public class ItemsDialog extends DialogFragment {

    private static final String ARG = "arg";

    DialogAddBinding mBinding;
    CustomMultitypeAdapter mAdapter;
    private Items mItems;
    private OnClickItemListener mOnClickItemListener;

    public interface OnClickItemListener {
        void onClick(int position);
    }

    public static ItemsDialog getInstance(ArrayList<String> showItems, OnClickItemListener listener) {
        ItemsDialog itemsDialog = new ItemsDialog();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ARG, showItems);
        itemsDialog.setArguments(bundle);
        itemsDialog.setOnClickItemListener(listener);
        return itemsDialog;
    }

    private void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        mOnClickItemListener = onClickItemListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_add, null, false);
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
        ItemDialogViewBinder itemViewBinder = new ItemDialogViewBinder();
        itemViewBinder.setOnItemClickListener(position -> {
            if(mOnClickItemListener != null) {
                mOnClickItemListener.onClick(position);
                dismiss();
            }
        });
        ArrayList<String> stringArrayList = getArguments().getStringArrayList(ARG);
        mAdapter.register(String.class, itemViewBinder);
        mItems = new Items();
        mItems.addAll(stringArrayList);
        mAdapter.setItems(mItems);
        mBinding.ry.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mBinding.ry.setAdapter(mAdapter);
        mBinding.ry.setLayoutManager(new LinearLayoutManager(view.getContext()));
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
            show(manager, ItemsDialog.class.getName());
        }
    }
}
