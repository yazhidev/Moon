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
            holder.mTvTitle.setText(title);
            holder.mTvDayNum.setText(String.valueOf(gapBetweenTwoDay));
            holder.mLlBelong.setVisibility(View.VISIBLE);
            holder.mLlAfter.setVisibility(View.GONE);
        } else {
            title = String.format(BaseApplication.getInstance().getString(R.string.memorial_belong), title);
            holder.mTvTitle2.setText(title);
            holder.mTvDayNum2.setText(String.valueOf(gapBetweenTwoDay));
            holder.mLlBelong.setVisibility(View.GONE);
            holder.mLlAfter.setVisibility(View.VISIBLE);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;
        private TextView mTvDayNum;
        private TextView mTvTitle2;
        private TextView mTvDayNum2;
        private View mLlBelong;
        private View mLlAfter;

        ViewHolder(View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mTvDayNum = itemView.findViewById(R.id.tv_day_num);
            mTvTitle2 = itemView.findViewById(R.id.tv_title2);
            mTvDayNum2 = itemView.findViewById(R.id.tv_day_num2);
            mLlBelong = itemView.findViewById(R.id.ll_belong);
            mLlAfter = itemView.findViewById(R.id.ll_after);
        }
    }
}
