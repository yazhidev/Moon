package com.yazhi1992.moon.ui.hopedetail;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.databinding.ActivityHopeDetailBinding;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.util.EditDataHelper;
import com.yazhi1992.moon.util.TipDialogHelper;
import com.yazhi1992.moon.viewmodel.HopeItemDataBean;
import com.yazhi1992.yazhilib.utils.LibUtils;

@Route(path = ActivityRouter.HOPE_DETAIL)
public class HopeDetailActivity extends BaseActivity {

    private ActivityHopeDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_hope_detail);

        initToolBar(mBinding.toolbar);

        HopeItemDataBean dataBean = (HopeItemDataBean) EditDataHelper.getInstance().getData();
        if (dataBean != null) {
            mBinding.setItem(dataBean);
        }

        mBinding.igLink.setOnClickListener(v -> {
            if (dataBean.getLink().startsWith("http")) {
                LibUtils.chooseBrowserOpenLink(HopeDetailActivity.this, dataBean.getLink());
            } else {
                String TAOBAO_PACKAGE_NAME = "com.taobao.taobao";
                if (LibUtils.isAppInstall(TAOBAO_PACKAGE_NAME, this)) {
                    TipDialogHelper.getInstance().showDialog(this, getString(R.string.goto_taobao), new TipDialogHelper.OnComfirmListener() {
                        @Override
                        public void comfirm() {
                            LibUtils.copyToClipboard(HopeDetailActivity.this, dataBean.getLink());
                            LibUtils.gotoApp(TAOBAO_PACKAGE_NAME, HopeDetailActivity.this);
                        }
                    });
                } else {
                    LibUtils.showToast(this, getString(R.string.taobao_not_install));
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (EditDataHelper.getInstance().getData() == null) {
            //数据已删除
            finish();
        } else {
            HopeItemDataBean dataBean = (HopeItemDataBean) EditDataHelper.getInstance().getData();
            if (dataBean.getStatus() == 1) {
                //已完成，隐藏编辑按钮
                MenuItem item = mBinding.toolbar.getMenu().findItem(R.id.toolbar_edit);
                if(item != null) {
                    item.setVisible(false);
                }
            }
        }
    }

    //添加右上角加编辑按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        HopeItemDataBean dataBean = (HopeItemDataBean) EditDataHelper.getInstance().getData();
        if (dataBean.getStatus() == 0) {
            //未完成
            getMenuInflater().inflate(R.menu.edit, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_edit:
                //编辑
                ActivityRouter.gotoAddHope(false);
                break;
            default:
                break;
        }
        return true;
    }

}
