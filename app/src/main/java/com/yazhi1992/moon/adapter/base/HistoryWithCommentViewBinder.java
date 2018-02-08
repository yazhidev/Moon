package com.yazhi1992.moon.adapter.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yazhi1992.moon.BR;
import com.yazhi1992.moon.adapter.history.CommentInHistoryViewBinder;
import com.yazhi1992.moon.viewmodel.CommentBean;
import com.yazhi1992.moon.viewmodel.IHistoryBean;

import java.util.List;

import me.drakeet.multitype.Items;

/**
 * Created by zengyazhi on 2018/2/8.
 *
 * 布局中 data 默认名为 item
 * 默认初始化了关于评论列表的逻辑
 */

public class HistoryWithCommentViewBinder<T extends IHistoryBean> extends WithClicklistenerItemViewBinder<T, HistoryWithCommentViewBinder.HistoryWithCommentViewHolder> {

    private int mLayoutId;
    private CustomMultitypeAdapter mCommentAdapter;
    private Items mItems;

    public HistoryWithCommentViewBinder(int layoutId) {
        mLayoutId = layoutId;
    }

    // TODO: 2018/2/8 添加/删除评论的动画卡顿

    @Override
    protected void BindViewHolder(@NonNull HistoryWithCommentViewHolder holder, @NonNull T historyBean) {
        holder.binding.setVariable(BR.item, historyBean);

        //评论有数据，加载评论列表
        if (holder instanceof CommentViewHolder) {
            CommentViewHolder holder1 = (CommentViewHolder) holder;
            if (historyBean.getCommentDatas() != null && !historyBean.getCommentDatas().isEmpty()) {
                mCommentAdapter = new CustomMultitypeAdapter();
                mCommentAdapter.register(CommentBean.class, new CommentInHistoryViewBinder(new CommentInHistoryViewBinder.OnCommentDeleteListener() {
                    @Override
                    public void onDelete(long id) {
                        for (int i = 0; i < historyBean.getCommentDatas().size(); i++) {
                            List<CommentBean> commentDatas = historyBean.getCommentDatas();
                            CommentBean commentBean = commentDatas.get(i);
                            if(commentBean.getId() == id) {
                                historyBean.getCommentDatas().remove(i);
                            }
                        }
                        getAdapter().notifyItemChanged(getPosition(holder));
                    }

                    @Override
                    public void onReply(String peerName, String peerId) {
                        //点击回复对方
                        if(mOnItemClickCommentListener != null) {
                            mOnItemClickCommentListener.onReplyComment(getPosition(holder), peerName, peerId);
                        }
                    }
                }));
                mItems = new Items();
                mItems.addAll(historyBean.getCommentDatas());
                mCommentAdapter.setItems(mItems);
                holder1.mRyComment.setVisibility(View.VISIBLE);
                holder1.mRyComment.setAdapter(mCommentAdapter);
                holder1.mRyComment.setLayoutManager(new LinearLayoutManager(holder1.mRyComment.getContext()));
            } else {
                holder1.mRyComment.setVisibility(View.GONE);
            }
        }
    }

    @NonNull
    @Override
    protected HistoryWithCommentViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, mLayoutId, parent, false);
        return new HistoryWithCommentViewHolder(binding);
    }

    public static class HistoryWithCommentViewHolder<T extends ViewDataBinding> extends CommentViewHolder {

        private T binding;

        public HistoryWithCommentViewHolder(T binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public T getBinding() {
            return binding;
        }
    }
}
