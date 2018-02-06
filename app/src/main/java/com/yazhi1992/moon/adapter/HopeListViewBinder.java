package com.yazhi1992.moon.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.base.CustomItemViewBinder;
import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.ui.ViewBindingUtils;
import com.yazhi1992.moon.util.AppUtils;
import com.yazhi1992.moon.util.TipDialogHelper;
import com.yazhi1992.moon.viewmodel.HopeItemDataBean;
import com.yazhi1992.yazhilib.widget.RoundView.RoundLoadingView;
import com.yazhi1992.yazhilib.widget.YZRatingBar;

/**
 * Created by zengyazhi on 2018/1/23.
 *
 * 心愿列表 viewBinder
 */
public class HopeListViewBinder extends CustomItemViewBinder<HopeItemDataBean, HopeListViewBinder.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_hope_in_list, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void BindViewHolder(@NonNull ViewHolder holder, @NonNull HopeItemDataBean data) {
        holder.mTvName.setText(data.getUserName());
        ViewBindingUtils.imgUrl(holder.mIgUser, data.getUserHeadUrl());
        holder.mRatingbar.setCountSelected(data.getLevel());
        holder.mRatingbar.setCountSelected(data.getLevel());
        holder.mTvTime.setText(AppUtils.getTimeForHistory(data.getUpdateTime()));
        holder.mTvTitle.setText(data.getTitle());

        if(data.getStatus() == 0) {
            //未完成
            holder.mIgFinish.setVisibility(View.GONE);
            holder.mRatingbar.setVisibility(View.VISIBLE);
            holder.mBtnFinish.setVisibility(View.VISIBLE);
        } else {
            holder.mIgFinish.setVisibility(View.VISIBLE);
            holder.mRatingbar.setVisibility(View.GONE);
            holder.mBtnFinish.setVisibility(View.GONE);
        }

        holder.mBtnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TipDialogHelper.getInstance().showDialog(holder.mBtnFinish.getContext(), holder.mBtnFinish.getContext().getString(R.string.finish_hope), new TipDialogHelper.OnComfirmListener() {
                    @Override
                    public void comfirm() {
                        holder.mBtnFinish.setLoading(true);
                        //标记为已完成
                        Api.getInstance().finishHope(data.getObjectId(), new DataCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean data) {
                                holder.mBtnFinish.setLoading(false);
                                holder.mBtnFinish.setVisibility(View.GONE);
                                holder.mIgFinish.setVisibility(View.VISIBLE);
                                holder.mRatingbar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFailed(int code, String msg) {
                                holder.mBtnFinish.setLoading(false);
                            }
                        });
                    }
                });
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private YZRatingBar mRatingbar;
        private final TextView mTvName;
        private final ImageView mIgUser;
        private final ImageView mIgFinish;
        private TextView mTvTime;
        private TextView mTvTitle;
        private RoundLoadingView mBtnFinish;

        ViewHolder(View itemView) {
            super(itemView);
            mRatingbar = itemView.findViewById(R.id.ratingbar);
            mTvName = itemView.findViewById(R.id.tv_name);
            mIgUser = itemView.findViewById(R.id.ig_user);
            mTvTime = itemView.findViewById(R.id.tv_time);
            mTvTitle = itemView.findViewById(R.id.tv_title);
            mBtnFinish = itemView.findViewById(R.id.btn_finish);
            mIgFinish = itemView.findViewById(R.id.ig_finish);
        }
    }
}
