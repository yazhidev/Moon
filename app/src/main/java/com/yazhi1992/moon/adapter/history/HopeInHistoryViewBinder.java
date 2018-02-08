package com.yazhi1992.moon.adapter.history;

import android.support.annotation.NonNull;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.base.HistoryWithCommentViewBinder;
import com.yazhi1992.moon.databinding.ItemHopeInHistoryBinding;
import com.yazhi1992.moon.viewmodel.HopeItemDataWrapper;

/**
 * Created by zengyazhi on 2018/1/23.
 */
public class HopeInHistoryViewBinder extends HistoryWithCommentViewBinder<HopeItemDataWrapper> {

    public HopeInHistoryViewBinder() {
        super(R.layout.item_hope_in_history);
    }

    @Override
    protected void BindViewHolder(@NonNull HistoryWithCommentViewHolder holder, @NonNull HopeItemDataWrapper historyBean) {
        super.BindViewHolder(holder, historyBean);
        ItemHopeInHistoryBinding binding = (ItemHopeInHistoryBinding) holder.getBinding();
        binding.ratingbar.setCountSelected(historyBean.getData().getLevel());
    }
}
