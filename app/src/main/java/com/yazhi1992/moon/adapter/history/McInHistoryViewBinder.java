package com.yazhi1992.moon.adapter.history;

import android.support.annotation.NonNull;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.base.HistoryWithCommentViewBinder;
import com.yazhi1992.moon.viewmodel.McBeanWrapper;

/**
 * Created by zengyazhi on 2018/2/12.
 */
public class McInHistoryViewBinder extends HistoryWithCommentViewBinder<McBeanWrapper> {

    public McInHistoryViewBinder() {
        super(R.layout.item_mc_in_history);
    }

    @Override
    protected void BindViewHolder(@NonNull HistoryWithCommentViewHolder holder, @NonNull McBeanWrapper historyBean) {
        super.BindViewHolder(holder, historyBean);
    }
}
