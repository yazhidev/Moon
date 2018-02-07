package com.yazhi1992.moon.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.base.CommentViewHolder;
import com.yazhi1992.moon.adapter.base.CustomItemViewBinder;
import com.yazhi1992.moon.ui.ViewBindingUtils;
import com.yazhi1992.moon.util.AppUtils;
import com.yazhi1992.moon.viewmodel.HopeItemDataBean;
import com.yazhi1992.moon.viewmodel.HopeItemDataWrapper;

/**
 * Created by zengyazhi on 2018/1/23.
 */
public class FinishedHopeInHistoryViewBinder extends CustomItemViewBinder<HopeItemDataWrapper, FinishedHopeInHistoryViewBinder.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_finish_hope_in_history, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void BindViewHolder(@NonNull ViewHolder holder, @NonNull HopeItemDataWrapper historyBean) {
        HopeItemDataBean hopeBean = historyBean.getData();
        holder.mTvName.setText(historyBean.getUserName());
        ViewBindingUtils.imgUrl(holder.mIgUser, historyBean.getUserHeadUrl());
        holder.mTvTime.setText(AppUtils.getTimeForHistory(historyBean.getCreateTime()));
        holder.mTvTitle.setText(hopeBean.getTitle());
    }

    static class ViewHolder extends CommentViewHolder {

        private final TextView mTvName;
        private final ImageView mIgUser;
        private TextView mTvTime;
        private TextView mTvTitle;

        ViewHolder(View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mIgUser = itemView.findViewById(R.id.ig_user);
            mTvTime = itemView.findViewById(R.id.tv_time);
            mTvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}