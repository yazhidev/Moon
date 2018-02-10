package com.yazhi1992.moon.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.base.DataBindingViewBinder;
import com.yazhi1992.moon.databinding.ItemHopeInListBinding;
import com.yazhi1992.moon.viewmodel.HopeItemDataBean;

/**
 * Created by zengyazhi on 2018/1/23.
 *
 * 心愿列表 viewBinder
 */
public class HopeListViewBinder extends DataBindingViewBinder<HopeItemDataBean> {

    private OnFinishHopeListener mListener;

    public OnFinishHopeListener getListener() {
        return mListener;
    }

    public void setOnFinishListener(OnFinishHopeListener listener) {
        mListener = listener;
    }

    public interface OnFinishHopeListener {
        void finish(int position, String id);
    }

    public HopeListViewBinder() {
        super(R.layout.item_hope_in_list);
    }

    @Override
    protected void BindViewHolder(@NonNull DatabindingViewHolder holder, @NonNull HopeItemDataBean item) {
        super.BindViewHolder(holder, item);
        ItemHopeInListBinding binding = (ItemHopeInListBinding) holder.getBinding();
        binding.btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null) {
                    mListener.finish(getPosition(holder), item.getObjectId());
                }
            }
        });
    }
}
