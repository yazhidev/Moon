package com.yazhi1992.moon.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by zengyazhi on 2018/1/23.
 */

public abstract class DatabindingBinder<T extends ViewDataBinding> extends ItemViewBinder<TestBean, DatabindingBinder.BindingViewHolder<T>> {

    private int mLayoutId;
    private int mVariableId;

    public DatabindingBinder(int layoutId, int variableId) {
        mLayoutId = layoutId;
        mVariableId = variableId;
    }

    @NonNull
    @Override
    protected BindingViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new BindingViewHolder(DataBindingUtil.inflate(inflater, mLayoutId, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull BindingViewHolder holder, @NonNull TestBean item) {
        holder.mBinding.setVariable(mVariableId, item);
    }

    static class BindingViewHolder<T extends ViewDataBinding>  extends RecyclerView.ViewHolder {

        T mBinding;

        BindingViewHolder(@NonNull T binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
