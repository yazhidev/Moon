package com.yazhi1992.moon.ui.imgpreview;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.databinding.ActivityImgPreviewBinding;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.ui.ViewBindingUtils;
import com.yazhi1992.moon.util.EditDataHelper;
import com.yazhi1992.moon.viewmodel.TextBean;
import com.yazhi1992.yazhilib.utils.LibStatusBarUtils;
import com.zhihu.matisse.internal.entity.SelectionSpec;
import com.zhihu.matisse.internal.ui.BasePreviewActivity;
import com.zhihu.matisse.internal.utils.PhotoMetadataUtils;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

@Route(path = ActivityRouter.IMG_PREVIEW)
public class ImgPreviewActivity extends BaseActivity {

    private ActivityImgPreviewBinding mBinding;
    private TextBean mEditData;

    @Override
    protected void initStatusBar() {
        LibStatusBarUtils.with(this)
                .init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_img_preview);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mBinding.toolbar.getLayoutParams();
        layoutParams.setMargins(0, LibStatusBarUtils.getStatusBarHeight(this), 0, 0);
        // 获取Drawable对象
        Drawable mDrawable = ContextCompat.getDrawable(this, R.drawable.bad);
        // 设置Drawable的透明度
        mDrawable.setAlpha(0);
        // 给Toolbar设置背景图
        mBinding.toolbar.setBackgroundDrawable(mDrawable);
        initToolBar(mBinding.toolbar);

        mBinding.imageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        if(EditDataHelper.getInstance().getData() instanceof TextBean) {
            mEditData = (TextBean) EditDataHelper.getInstance().getData();
            ViewBindingUtils.imgUrl(mBinding.imageView, mEditData.mImgUrl.get());
        }
    }
}
