package com.yazhi1992.moon.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yazhi1992.moon.BaseApplication;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.base.CustomItemViewBinder;
import com.yazhi1992.moon.viewmodel.MemorialDayBean;
import com.yazhi1992.yazhilib.utils.LibTimeUtils;
import com.yazhi1992.yazhilib.widget.RoundView.RoundTextView;

import java.util.Date;

/**
 * Created by zengyazhi on 2018/1/30.
 */
public class MemorialDayListViewBinder extends CustomItemViewBinder<MemorialDayBean, MemorialDayListViewBinder.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_memorial_day_list, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void BindViewHolder(@NonNull ViewHolder holder, @NonNull MemorialDayBean bean) {
        int gapBetweenTwoDay = LibTimeUtils.getGapBetweenTwoDay(new Date(), new Date(bean.getTime()));
        String title = bean.getTitle();
        if (gapBetweenTwoDay > 0) {
            title = (String.format(BaseApplication.getInstance().getString(R.string.memorial_after), title));
            holder.mTvDayNum.getDelegate().setBackgroundColor(holder.mTvDayNum.getContext().getResources().getColor(R.color.after_day_color));
            holder.mTvDay.getDelegate().setBackgroundColor(holder.mTvDayNum.getContext().getResources().getColor(R.color.after_day_color_deep));
        } else {
            title = String.format(BaseApplication.getInstance().getString(R.string.memorial_belong), title);
            holder.mTvDayNum.getDelegate().setBackgroundColor(holder.mTvDayNum.getContext().getResources().getColor(R.color.belong_day_color));
            holder.mTvDay.getDelegate().setBackgroundColor(holder.mTvDayNum.getContext().getResources().getColor(R.color.belong_day_color_deep));
        }
        holder.mTvTitle.setText(title);
        holder.mTvDayNum.setText(String.valueOf(Math.abs(gapBetweenTwoDay)));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;
        private RoundTextView mTvDayNum;
        private RoundTextView mTvDay;

        ViewHolder(View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvDayNum = itemView.findViewById(R.id.tv_day_num);
            mTvDay = itemView.findViewById(R.id.tv_day_str);
        }
    }
}
