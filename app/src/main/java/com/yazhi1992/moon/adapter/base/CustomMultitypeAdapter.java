package com.yazhi1992.moon.adapter.base;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by zengyazhi on 2018/1/30.
 *
 * 添加了移除方法
 */

public class CustomMultitypeAdapter extends MultiTypeAdapter {

    public void remove(int position) {
        getItems().remove(position);
        notifyItemRemoved(position);
        if(position != getItemCount()) {
            notifyItemRangeChanged(position, getItemCount() - position);
        }
    }
}
