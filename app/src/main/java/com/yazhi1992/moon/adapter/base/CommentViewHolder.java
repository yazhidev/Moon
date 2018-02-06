package com.yazhi1992.moon.adapter.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yazhi1992.moon.R;

/**
 * Created by zengyazhi on 2018/2/6.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {

    protected View mCommentView;

    public CommentViewHolder(View itemView) {
        super(itemView);
        mCommentView = itemView.findViewById(R.id.ig_comment);
    }
}
