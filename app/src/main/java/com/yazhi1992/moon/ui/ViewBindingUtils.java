package com.yazhi1992.moon.ui;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

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
}
