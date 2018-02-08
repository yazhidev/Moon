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
import com.yazhi1992.moon.adapter.base.CustomMultitypeAdapter;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.databinding.DialogAddBinding;
import com.yazhi1992.moon.viewmodel.AddItemBean;

import me.drakeet.multitype.Items;

/**
 * Created by zengyazhi on 2018/1/26.
 */

public class AddDialog extends DialogFragment {
    DialogAddBinding mBinding;
    CustomMultitypeAdapter mAdapter;
    private Items mItems;

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
        AddItemViewBinder addItemViewBinder = new AddItemViewBinder();
        addItemViewBinder.setOnItemClickListener(position -> {
            AddItemBean bean = (AddItemBean) mItems.get(position);
            handleAction(bean.getAction());
        });
        mAdapter.register(AddItemBean.class, addItemViewBinder);
        mItems = new Items();
        mItems.add(new AddItemBean(ActionConstant.ADD_TEXT, getString(R.string.home_add_item_title_text)));
        mItems.add(new AddItemBean(ActionConstant.ADD_MEMORIAL, getString(R.string.home_add_item_title_memorial)));
        mItems.add(new AddItemBean(ActionConstant.ADD_HOPE, getString(R.string.home_add_item_title_hope)));
        mAdapter.setItems(mItems);
        mBinding.ry.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mBinding.ry.setAdapter(mAdapter);
        mBinding.ry.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    private void handleAction(@ActionConstant.AddAction String action) {
        switch (action) {
            case ActionConstant.ADD_MEMORIAL:
                ActivityRouter.gotoAddMemorial(true);
                dismiss();
                break;
            case ActionConstant.ADD_HOPE:
                ActivityRouter.gotoAddHope();
                dismiss();
                break;
            case ActionConstant.ADD_TEXT:
                ActivityRouter.gotoAddText();
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
