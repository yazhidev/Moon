package com.yazhi1992.moon.adapter.history;

import android.support.annotation.NonNull;

import com.yazhi1992.moon.BaseApplication;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.base.HistoryWithCommentViewBinder;
import com.yazhi1992.moon.databinding.ItemMemorialDayInHistoryBinding;
import com.yazhi1992.moon.viewmodel.MemorialBeanWrapper;
import com.yazhi1992.moon.viewmodel.MemorialDayBean;
import com.yazhi1992.yazhilib.utils.LibTimeUtils;

import java.util.Date;

/**
 * Created by zengyazhi on 2018/1/23.
 */
public class MemorialDayInHistoryViewBinder extends HistoryWithCommentViewBinder<MemorialBeanWrapper> {

    public MemorialDayInHistoryViewBinder() {
        super(R.layout.item_memorial_day_in_history);
    }

    @Override
    protected void BindViewHolder(@NonNull HistoryWithCommentViewHolder holder, @NonNull MemorialBeanWrapper historyBean) {
        super.BindViewHolder(holder, historyBean);
        ItemMemorialDayInHistoryBinding binding = (ItemMemorialDayInHistoryBinding) holder.getBinding();
        MemorialDayBean memorialBean = historyBean.getData();
        if (historyBean.getData().getGapBetweenTwoDay() > 0) {
            binding.tvDayNum.getDelegate().setBackgroundColor(binding.tvDayNum.getContext().getResources().getColor(R.color.after_day_color));
            binding.tvDayStr.getDelegate().setBackgroundColor(binding.tvDayStr.getContext().getResources().getColor(R.color.after_day_color_deep));
        } else {
            binding.tvDayNum.getDelegate().setBackgroundColor(binding.tvDayNum.getContext().getResources().getColor(R.color.belong_day_color));
            binding.tvDayStr.getDelegate().setBackgroundColor(binding.tvDayStr.getContext().getResources().getColor(R.color.belong_day_color_deep));
        }
    }
}
