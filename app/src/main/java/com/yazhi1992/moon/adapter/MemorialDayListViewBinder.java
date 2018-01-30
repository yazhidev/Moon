package com.yazhi1992.moon.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.viewmodel.MemorialDayBean;
import com.yazhi1992.yazhilib.utils.LibTimeUtils;

import java.util.Date;

import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by zengyazhi on 2018/1/30.
 */
public class MemorialDayListViewBinder extends ItemViewBinder<MemorialDayBean, MemorialDayListViewBinder.ViewHolder> {

    private MemorialDayViewListener mOnClickListener;

    public interface MemorialDayViewListener {
        void onClick(int id, int position);
    }

    public MemorialDayListViewBinder(MemorialDayViewListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_memorial_day_list, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull MemorialDayBean bean) {
        int gapBetweenTwoDay = LibTimeUtils.getGapBetweenTwoDay(new Date(), new Date(bean.getTime()));
        String title = bean.getTitle();
        if (gapBetweenTwoDay > 0) {
            title += (" 已经 " + gapBetweenTwoDay + " 天");
        } else if (gapBetweenTwoDay < 0) {
            title = ("距离 " + title + " 还有 " + gapBetweenTwoDay + " 天");
        } else {
            title = title + " 今天";
        }
        holder.mTvTitle.setText(title);

        holder.mRootView.setOnClickListener(v -> {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(R.id.root, getPosition(holder));
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;
        private RelativeLayout mRootView;

        ViewHolder(View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mRootView = itemView.findViewById(R.id.root);
        }
    }
}
