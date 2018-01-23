package com.yazhi1992.moon.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yazhi1992.moon.R;
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
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull MemorialDayBean memorialDayBean) {
        holder.mTv.setText(memorialDayBean.getTitle());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTv;

        ViewHolder(View itemView) {
            super(itemView);
            mTv = itemView.findViewById(R.id.tv_memorial_day_title);
        }
    }
}
