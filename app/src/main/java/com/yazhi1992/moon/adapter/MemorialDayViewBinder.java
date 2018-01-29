package com.yazhi1992.moon.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.ui.ViewBindingUtils;
import com.yazhi1992.moon.viewmodel.MemorialDayBean;

import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by zengyazhi on 2018/1/23.
 */
public class MemorialDayViewBinder extends ItemViewBinder<MemorialDayBean, MemorialDayViewBinder.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_memorial_day, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull MemorialDayBean memorialDayBean2) {
        holder.mTvTitle.setText(memorialDayBean2.getTitle());
        holder.mTvName.setText(memorialDayBean2.getUserName());
        ViewBindingUtils.imgUrl(holder.mIgUser, memorialDayBean2.getUserHeadUrl());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;
        private final TextView mTvName;
        private final ImageView mIgUser;

        ViewHolder(View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_memorial_day_title);
            mTvName = itemView.findViewById(R.id.tv_name);
            mIgUser = itemView.findViewById(R.id.ig_user);
        }
    }
}
