package com.yazhi1992.moon.ui;

import android.databinding.BindingAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yazhi1992.moon.util.AppUtils;
import com.yazhi1992.yazhilib.widget.RoundView.RoundTextView;

import java.util.Date;

/**
 * Created by zengyazhi on 2018/1/26.
 */

public class ViewBindingUtils {

    @BindingAdapter("url")
    public static void imgUrl(ImageView img, String url) {
        Glide.with(img.getContext())
                .load(url)
                .into(img);
    }

    @BindingAdapter("history_time")
    public static void transformTimeForHistory(TextView tv, Date time) {
        tv.setText(AppUtils.getTimeForHistory(time));
    }

    @BindingAdapter("rv_backgroundColor")
    public static void rv_backgroundColor(RoundTextView tv, int color) {
        tv.getDelegate().setBackgroundColor(color);
    }

//    public static void transformTimeForMemorialDayInHistory(TextView tv, long time) {

}
