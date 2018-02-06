package com.yazhi1992.moon.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yazhi1992.moon.BaseApplication;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.base.CommentViewHolder;
import com.yazhi1992.moon.adapter.base.CustomItemViewBinder;
import com.yazhi1992.moon.ui.ViewBindingUtils;
import com.yazhi1992.moon.util.AppUtils;
import com.yazhi1992.moon.viewmodel.MemorialBeanWrapper;
import com.yazhi1992.moon.viewmodel.MemorialDayBean;
import com.yazhi1992.yazhilib.utils.LibTimeUtils;
import com.yazhi1992.yazhilib.widget.RoundView.RoundTextView;

import java.util.Date;

/**
 * Created by zengyazhi on 2018/1/23.
 */
public class MemorialDayInHistoryViewBinder extends CustomItemViewBinder<MemorialBeanWrapper, MemorialDayInHistoryViewBinder.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_memorial_day_in_history, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void BindViewHolder(@NonNull ViewHolder holder, @NonNull MemorialBeanWrapper historyBean) {
        MemorialDayBean memorialBean = historyBean.getData();
        holder.mTvName.setText(historyBean.getUserName());
        ViewBindingUtils.imgUrl(holder.mIgUser, historyBean.getUserHeadUrl());
        holder.mTvTime.setText(AppUtils.getTimeForHistory(historyBean.getCreateTime()));

        int gapBetweenTwoDay = LibTimeUtils.getGapBetweenTwoDay(new Date(), new Date(memorialBean.getTime()));
        String title = memorialBean.getTitle();
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

    static class ViewHolder extends CommentViewHolder {

        private final TextView mTvName;
        private final ImageView mIgUser;
        private TextView mTvTime;
        private TextView mTvTitle;
        private RoundTextView mTvDayNum;
        private RoundTextView mTvDay;

        ViewHolder(View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mIgUser = itemView.findViewById(R.id.ig_user);
            mTvTime = itemView.findViewById(R.id.tv_time);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvDayNum = itemView.findViewById(R.id.tv_day_num);
            mTvDay = itemView.findViewById(R.id.tv_day_str);
        }
    }
}
