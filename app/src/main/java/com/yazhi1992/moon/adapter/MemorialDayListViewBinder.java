package com.yazhi1992.moon.adapter;

import android.support.annotation.NonNull;

import com.yazhi1992.moon.BaseApplication;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.base.DataBindingViewBinder;
import com.yazhi1992.moon.databinding.ItemMemorialDayListBinding;
import com.yazhi1992.moon.viewmodel.MemorialDayBean;
import com.yazhi1992.yazhilib.utils.LibTimeUtils;

import java.util.Date;

/**
 * Created by zengyazhi on 2018/1/30.
 */
public class MemorialDayListViewBinder extends DataBindingViewBinder<MemorialDayBean> {

    public MemorialDayListViewBinder() {
        super(R.layout.item_memorial_day_list);
    }

    @Override
    protected void BindViewHolder(@NonNull DatabindingViewHolder holder, @NonNull MemorialDayBean memorialBean) {
        super.BindViewHolder(holder, memorialBean);
        ItemMemorialDayListBinding binding = (ItemMemorialDayListBinding) holder.getBinding();
        int gapBetweenTwoDay = LibTimeUtils.getGapBetweenTwoDay(new Date(), new Date(memorialBean.getTime()));
        String finalTitle = memorialBean.getTitle();
        if (gapBetweenTwoDay > 0) {
            finalTitle = (String.format(BaseApplication.getInstance().getString(R.string.memorial_after), finalTitle));
            binding.tvDayNum.getDelegate().setBackgroundColor(binding.tvDayNum.getContext().getResources().getColor(R.color.after_day_color));
            binding.tvDayStr.getDelegate().setBackgroundColor(binding.tvDayStr.getContext().getResources().getColor(R.color.after_day_color_deep));
        } else {
            finalTitle = String.format(BaseApplication.getInstance().getString(R.string.memorial_belong), finalTitle);
            binding.tvDayNum.getDelegate().setBackgroundColor(binding.tvDayNum.getContext().getResources().getColor(R.color.belong_day_color));
            binding.tvDayStr.getDelegate().setBackgroundColor(binding.tvDayStr.getContext().getResources().getColor(R.color.belong_day_color_deep));
        }
        memorialBean.setFinalTitle(finalTitle);
    }

}
