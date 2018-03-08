package com.yazhi1992.moon.widget;

import android.databinding.ObservableField;

/**
 * Created by zengyazhi on 2018/3/8.
 */

public class MultiStatusViewModel {

    public static final int NORMAL = 0;
    public static final int EMPTY = 1;
    public static final int NET_ERR = 2;

    public ObservableField<Integer> status = new ObservableField<>(NORMAL);
}
