package com.yazhi1992.moon.adapter.history;

import android.support.annotation.NonNull;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.base.HistoryWithCommentViewBinder;
import com.yazhi1992.moon.viewmodel.TextBeanWrapper;

/**
 * Created by zengyazhi on 2018/2/6.
 */
public class TextInHistoryViewBinder extends HistoryWithCommentViewBinder<TextBeanWrapper> {

    public TextInHistoryViewBinder() {
        super(R.layout.item_text_in_history);
    }

    @Override
    protected void BindViewHolder(@NonNull HistoryWithCommentViewHolder holder, @NonNull TextBeanWrapper historyBean) {
        super.BindViewHolder(holder, historyBean);
    }
}
