package com.yazhi1992.moon.adapter.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.yazhi1992.moon.BR;

/**
 * Created by zengyazhi on 2018/2/8.
 *
 * 布局中 data 默认名为 item
 */

public class DataBindingViewBinder<T> extends WithClicklistenerItemViewBinder<T, DataBindingViewBinder.DatabindingViewHolder> {

    private int mLayoutId;

    public DataBindingViewBinder(int layoutId) {
        mLayoutId = layoutId;
    }

    @Override
    protected void BindViewHolder(@NonNull DatabindingViewHolder holder, @NonNull T item) {
        holder.binding.setVariable(BR.item, item);
    }

    @NonNull
    @Override
    protected DatabindingViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, mLayoutId, parent, false);
        return new DatabindingViewHolder(binding);
    }

    public static class DatabindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

        private T binding;

        public DatabindingViewHolder(T binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public T getBinding() {
            return binding;
        }
    }
}
