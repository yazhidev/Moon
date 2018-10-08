package com.yazhi1992.moon.adapter;

import android.support.annotation.NonNull;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.base.DataBindingViewBinder;
import com.yazhi1992.moon.viewmodel.TravelListItemDataBean;

/**
 * Created by zengyazhi on 2018/1/30.
 */
public class TravelListViewBinder extends DataBindingViewBinder<TravelListItemDataBean> {

    public TravelListViewBinder() {
        super(R.layout.item_travel_list);
    }

    @Override
    protected void BindViewHolder(@NonNull DatabindingViewHolder holder, @NonNull TravelListItemDataBean bean) {
        super.BindViewHolder(holder, bean);
    }

}
