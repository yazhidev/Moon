package com.yazhi1992.moon.adapter.history;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.base.WithClicklistenerItemViewBinder;
import com.yazhi1992.moon.adapter.base.CustomMultitypeAdapter;
import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.util.TipDialogHelper;
import com.yazhi1992.moon.viewmodel.CommentBean;
import com.yazhi1992.yazhilib.utils.LibUtils;

/**
 * Created by zengyazhi on 2018/2/6
 */
public class CommentInHistoryViewBinder extends WithClicklistenerItemViewBinder<CommentBean, CommentInHistoryViewBinder.ViewHolder> {

    private OnCommentDeleteListener mOnCommentDeleteListener;
    private String mUserId;

    public CommentInHistoryViewBinder(OnCommentDeleteListener onCommentDeleteListener) {
        mOnCommentDeleteListener = onCommentDeleteListener;
    }

    public interface OnCommentDeleteListener {
        void onDelete(long id);

        void onReply(String peerName, String peerId);
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_comment_in_history, parent, false);
        return new ViewHolder(root);
    }

    @Override
    protected void BindViewHolder(@NonNull ViewHolder holder, @NonNull CommentBean bean) {
//        SpannableString spannableString = new SpannableString("默认颜色红颜色");
//        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 4,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tv.setText(spannableString);
        if (LibUtils.notNullNorEmpty(bean.getReplyName()) && !bean.getReplyName().equals("null")) {
            holder.mTvTitle.setText(bean.getUserName() + "回复" + bean.getReplyName() + "：" + bean.getContent());
        } else {
            holder.mTvTitle.setText(bean.getUserName() + "：" + bean.getContent());
        }

        //删除评论
        holder.mTvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUserId == null) {
                    mUserId = new UserDaoUtil().getUserDao().getObjectId();
                }
                if (bean.getUserId().equals(mUserId)) {
                    //自己点击评论，删除
                    TipDialogHelper.getInstance().showDialog(holder.mTvTitle.getContext()
                            , holder.mTvTitle.getContext().getString(R.string.delete_comment)
                            , new TipDialogHelper.OnComfirmListener() {
                                @Override
                                public void comfirm() {
                                    Api.getInstance().deleteComment(bean.getParentId(), bean.getId(), new DataCallback<Boolean>() {
                                        @Override
                                        public void onSuccess(Boolean data) {
                                            int position = getPosition(holder);
                                            CustomMultitypeAdapter adapter = (CustomMultitypeAdapter) getAdapter();
                                            if (mOnCommentDeleteListener != null) {
                                                mOnCommentDeleteListener.onDelete(bean.getId());
                                            }
                                        }

                                        @Override
                                        public void onFailed(int code, String msg) {
                                            LibUtils.showToast(holder.mTvTitle.getContext(), code + msg);
                                        }
                                    });
                                }
                            });
                } else {
                    //点击对方评论，回复
                    if (mOnCommentDeleteListener != null) {
                        mOnCommentDeleteListener.onReply(bean.getUserName(), bean.getUserId());
                    }
                }

            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvTitle;

        ViewHolder(View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}
