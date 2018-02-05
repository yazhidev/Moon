package com.yazhi1992.moon.adapter.base;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by zengyazhi on 2018/2/5.
 *
 * 最后一行不需要分隔线
 */

public class CustomDividerItemDecoration extends DividerItemDecoration {
    /**
     * Creates a divider {@link RecyclerView.ItemDecoration} that can be used with a
     * {@link LinearLayoutManager}.
     *
     * @param context     Current context, it will be used to access resources.
     * @param orientation Divider orientation. Should be {@link #HORIZONTAL} or {@link #VERTICAL}.
     */
    public CustomDividerItemDecoration(Context context, int orientation) {
        super(context, orientation);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(!isLastRow(view, parent)) {
            super.getItemOffsets(outRect, view, parent, state);
        }
    }

    // 如果是最后一行
    private boolean isLastRow(View view, RecyclerView parent) {
        int position = parent.getChildAdapterPosition(view);
        int count = parent.getAdapter().getItemCount();
        return position == count - 1;
    }
}
