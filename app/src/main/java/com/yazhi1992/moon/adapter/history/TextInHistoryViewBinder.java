package com.yazhi1992.moon.adapter.history;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.base.HistoryWithCommentViewBinder;
import com.yazhi1992.moon.databinding.ItemTextInHistoryBinding;
import com.yazhi1992.moon.viewmodel.TextBeanWrapper;
import com.yazhi1992.yazhilib.utils.LibCalcUtil;

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
        if (!TextUtils.isEmpty(historyBean.getData().mImgUrl.get())) {
            String url = historyBean.getData().mImgUrl.get();
            //有图片，根据图片比例缩放图片容器
            ItemTextInHistoryBinding textBinding = (ItemTextInHistoryBinding) holder.getBinding();
            Context context = textBinding.getRoot().getContext();
            //获取图片真正的宽高
            Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            int width = resource.getWidth();
                            int height = resource.getHeight();
                            float value = LibCalcUtil.dp2px(context, 200);
                            float realWidth = value;
                            float realHeight = value;
                            if (width > height) {
                                realHeight = value * height * 1f / width;
                            }
                            if (height > width) {
                                realWidth = value * width * 1f / height;
                            }
                            float finalRealWidth = realWidth;
                            float finalRealHeight = realHeight;
                            textBinding.ig.post(new Runnable() {
                                @Override
                                public void run() {
                                    ViewGroup.LayoutParams layoutParams = textBinding.ig.getLayoutParams();
                                    layoutParams.width = (int) finalRealWidth;
                                    layoutParams.height = (int) finalRealHeight;
                                    textBinding.ig.setLayoutParams(layoutParams);
                                    textBinding.ig.setImageBitmap(resource);
                                }
                            });
                        }
                    });
        }
    }
}
