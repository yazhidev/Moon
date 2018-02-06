package com.yazhi1992.moon.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.base.CommentViewHolder;
import com.yazhi1992.moon.adapter.base.CustomItemViewBinder;
import com.yazhi1992.moon.adapter.base.CustomMultitypeAdapter;
import com.yazhi1992.moon.ui.ViewBindingUtils;
import com.yazhi1992.moon.util.AppUtils;
import com.yazhi1992.moon.viewmodel.CommentBean;
import com.yazhi1992.moon.viewmodel.TextBean;
import com.yazhi1992.moon.viewmodel.TextBeanWrapper;

import java.util.List;

import me.drakeet.multitype.Items;

/**
 * Created by zengyazhi on 2018/2/6.
 */
public class TextInHistoryViewBinder extends CustomItemViewBinder<TextBeanWrapper, TextInHistoryViewBinder.ViewHolder> {

    private CustomMultitypeAdapter mCommentAdapter;
    private Items mItems;

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_text_in_history, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void BindViewHolder(@NonNull ViewHolder holder, @NonNull TextBeanWrapper historyBean) {
        TextBean textBean = historyBean.getData();
        holder.mTvName.setText(historyBean.getUserName());
        ViewBindingUtils.imgUrl(holder.mIgUser, historyBean.getUserHeadUrl());
        holder.mTvTime.setText(AppUtils.getTimeForHistory(historyBean.getCreateTime()));
        holder.mTvContent.setText(textBean.getContent());

        List<CommentBean> commentDatas = historyBean.getCommentDatas();
        if (commentDatas != null && !commentDatas.isEmpty()) {
            //评论有数据，加载评论列表
            if (holder instanceof CommentViewHolder) {
                CommentViewHolder holder1 = (CommentViewHolder) holder;
                mCommentAdapter = new CustomMultitypeAdapter();
                mCommentAdapter.register(CommentBean.class, new CommentInHistoryViewBinder());
                mItems = new Items();
                mItems.addAll(commentDatas);
                mCommentAdapter.setItems(mItems);
                holder1.mRyComment.setVisibility(View.VISIBLE);
                holder1.mRyComment.setAdapter(mCommentAdapter);
                holder1.mRyComment.setLayoutManager(new LinearLayoutManager(holder1.mRyComment.getContext()));
            }
        }
    }

    static class ViewHolder extends CommentViewHolder {

        private final TextView mTvName;
        private final ImageView mIgUser;
        private TextView mTvContent;
        private TextView mTvTime;

        ViewHolder(View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mIgUser = itemView.findViewById(R.id.ig_user);
            mTvTime = itemView.findViewById(R.id.tv_time);
            mTvContent = itemView.findViewById(R.id.tv_content);
        }
    }
}
