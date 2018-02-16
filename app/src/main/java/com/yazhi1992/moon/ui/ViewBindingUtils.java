package com.yazhi1992.moon.ui;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yazhi1992.moon.BaseApplication;
import com.yazhi1992.moon.util.AppUtils;
import com.yazhi1992.yazhilib.widget.RoundView.RoundLoadingView;
import com.yazhi1992.yazhilib.widget.RoundView.RoundRelativeLayout;
import com.yazhi1992.yazhilib.widget.RoundView.RoundTextView;
import com.yazhi1992.yazhilib.widget.VerticalText;
import com.yazhi1992.yazhilib.widget.YZRatingBar;

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

    @BindingAdapter("rv_strokeColor")
    public static void rv_strokeColor(RoundRelativeLayout tv, int color) {
        tv.getDelegate().setStrokeColor(color);
    }

    @BindingAdapter("rv_text")
    public static void rv_text(RoundLoadingView loadingView, String text) {
        loadingView.setText(text);
    }

    @BindingAdapter("selectedNum")
    public static void selectedNum(YZRatingBar ratingbar, int selectNum) {
        ratingbar.setCountSelected(selectNum);
    }

    @BindingAdapter("vt_text")
    public static void vtText(VerticalText textview, String text) {
        textview.setText(text);
    }


    @BindingAdapter("srcCompat")
    public static void srcCompat(FloatingActionButton btn, Drawable imageDrawble) {
        btn.setImageDrawable(imageDrawble);
    }

//    public static void transformTimeForMemorialDayInHistory(TextView tv, long time) {

}
