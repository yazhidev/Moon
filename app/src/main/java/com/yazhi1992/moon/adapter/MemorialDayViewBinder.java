package com.yazhi1992.moon.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.ui.ViewBindingUtils;
import com.yazhi1992.moon.util.MyLog;
import com.yazhi1992.moon.viewmodel.MemorialBeanWrapper;
import com.yazhi1992.moon.viewmodel.MemorialDayBean;

import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by zengyazhi on 2018/1/23.
 */
public class MemorialDayViewBinder extends ItemViewBinder<MemorialBeanWrapper, MemorialDayViewBinder.ViewHolder> {

    private MemorialDayViewListener mOnClickListener;

    public interface MemorialDayViewListener {
        void onClick(int id, int position);
    }

    public MemorialDayViewBinder(MemorialDayViewListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_memorial_day, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull MemorialBeanWrapper historyBean) {
        MemorialDayBean memorialBean = historyBean.getData();
        holder.mTvName.setText(historyBean.getUserName());
        ViewBindingUtils.imgUrl(holder.mIgUser, historyBean.getUserHeadUrl());
        holder.mTvTitle.setText(memorialBean.getTitle());

        holder.mIgDelete.setOnClickListener(v -> {
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            MyLog.log(location[0] + " - " + location[1]);
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.history_memorial_day, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.delete) {
                        if(mOnClickListener != null) {
                            mOnClickListener.onClick(R.id.delete, getPosition(holder));
                        }
                    }
                    return false;
                }
            });
            popupMenu.show();
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;
        private final TextView mTvName;
        private final ImageView mIgUser;
        private ImageView mIgDelete;

        ViewHolder(View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_memorial_day_title);
            mTvName = itemView.findViewById(R.id.tv_name);
            mIgUser = itemView.findViewById(R.id.ig_user);
            mIgDelete = itemView.findViewById(R.id.ig_delete);
        }
    }
}
