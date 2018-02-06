package com.yazhi1992.moon.adapter.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import me.drakeet.multitype.ItemViewBinder;

/**
 * Created by zengyazhi on 2018/2/5.
 * <p>
 * 增加了点击监听
 */

public abstract class CustomItemViewBinder<T, VH extends RecyclerView.ViewHolder> extends ItemViewBinder<T, VH> {

    private OnItemClickListener mOnClickListener;
    private OnItemLongClickListener mOnLongClickListener;
    protected OnItemClickCommentListener mOnItemClickCommentListener;

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public interface OnItemClickCommentListener {
        void onClick(int position);

        void onReplyComment(int position, String peerName, String peerId);
    }

    public interface OnItemLongClickListener {
        void onLongClick(int position);
    }

    public void setOnClickListener(OnItemClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public void setOnLongClickListener(OnItemLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
    }

    public void setOnItemClickCommentListener(OnItemClickCommentListener onItemClickCommentListener) {
        mOnItemClickCommentListener = onItemClickCommentListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull VH holder, @NonNull T item) {
        if (mOnClickListener != null) {
            holder.itemView.setOnClickListener(v -> mOnClickListener.onClick(getPosition(holder)));
        }
        if (mOnLongClickListener != null) {
            holder.itemView.setOnLongClickListener(v -> {
                mOnLongClickListener.onLongClick(getPosition(holder));
                //返回true，事件消费完成，不再继续传递onClick
                return false;
            });
        }
        if (mOnLongClickListener != null) {
            if (holder instanceof CommentViewHolder) {
                CommentViewHolder holder1 = (CommentViewHolder) holder;
                holder1.mCommentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickCommentListener.onClick(getPosition(holder));
                    }
                });
            }
        }
        BindViewHolder(holder, item);
    }

    protected abstract void BindViewHolder(@NonNull VH holder, @NonNull T item);
}
