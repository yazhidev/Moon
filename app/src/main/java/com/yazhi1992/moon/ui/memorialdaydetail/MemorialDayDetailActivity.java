package com.yazhi1992.moon.ui.memorialdaydetail;

import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.databinding.ActivityMemorialDayDetailBinding;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.util.EditDataHelper;
import com.yazhi1992.moon.viewmodel.MemorialDayBean;
import com.yazhi1992.yazhilib.utils.LibStatusBarUtils;

@Route(path = ActivityRouter.MEMORIAL_DAY_DETAIL)
public class MemorialDayDetailActivity extends BaseActivity {

    private ActivityMemorialDayDetailBinding mBinding;

    @Override
    protected void initStatusBar() {
        LibStatusBarUtils.with(this)
                .init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ARouter.getInstance().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_memorial_day_detail);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mBinding.toolbar.getLayoutParams();
        layoutParams.setMargins(0, LibStatusBarUtils.getStatusBarHeight(this), 0, 0);
        mBinding.toolbar.setTitle(getString(R.string.memorial_day_detail_title));
        // 获取Drawable对象
        Drawable mDrawable = ContextCompat.getDrawable(this, R.drawable.bad);
        // 设置Drawable的透明度
        mDrawable.setAlpha(0);
        // 给Toolbar设置背景图
        mBinding.toolbar.setBackgroundDrawable(mDrawable);
        initToolBar(mBinding.toolbar);

        MemorialDayBean data = null;
        if (EditDataHelper.getInstance().getData() instanceof MemorialDayBean) {
            data = (MemorialDayBean) EditDataHelper.getInstance().getData();
        }

        if (data != null) {
            mBinding.setItem(data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(EditDataHelper.getInstance().getData() == null) {
            //数据已删除
            finish();
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
                ActivityRouter.gotoAddMemorial(false);
                break;
            default:
                break;
        }
        return true;
    }
}
