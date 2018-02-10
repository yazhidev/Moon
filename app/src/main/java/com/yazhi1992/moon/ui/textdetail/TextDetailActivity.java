package com.yazhi1992.moon.ui.textdetail;

import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.databinding.ActivityTextDetailBinding;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.util.EditDataHelper;
import com.yazhi1992.moon.viewmodel.TextBean;
import com.yazhi1992.yazhilib.utils.LibStatusBarUtils;

@Route(path = ActivityRouter.TEXT_DETAIL)
public class TextDetailActivity extends BaseActivity {

    ActivityTextDetailBinding mBinding;

    @Override
    protected void initStatusBar() {
        LibStatusBarUtils.with(this)
                .setIsDarkStatusBar(true)
                .init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_text_detail);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mBinding.toolbar.getLayoutParams();
        layoutParams.setMargins(0, LibStatusBarUtils.getStatusBarHeight(this), 0, 0);
        // 获取Drawable对象
        Drawable mDrawable = ContextCompat.getDrawable(this, R.drawable.bad);
        // 设置Drawable的透明度
        mDrawable.setAlpha(0);
        // 给Toolbar设置背景图
        mBinding.toolbar.setBackgroundDrawable(mDrawable);

        initToolBar(mBinding.toolbar);

        Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        if(EditDataHelper.getInstance().getData() instanceof TextBean) {
            TextBean editData = (TextBean) EditDataHelper.getInstance().getData();
            mBinding.setItem(editData);
        }
    }

    //添加右上角加编辑按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_edit:
                //编辑
                ActivityRouter.gotoAddText(false);
                break;
            default:
                break;
        }
        return true;
    }
}
