package com.yazhi1992.moon.ui.memorialdaylist;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.MemorialDayListViewBinder;
import com.yazhi1992.moon.adapter.base.CustomItemViewBinder;
import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.ui.base.BaseListActivity;
import com.yazhi1992.moon.viewmodel.MemorialDayBean;
import com.yazhi1992.yazhilib.utils.LibCalcUtil;

import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

@Route(path = ActivityRouter.MEMORIAL_LIST)
public class MemorialDayListActivity extends BaseListActivity<MemorialDayBean> {

    @Override
    public void adapterRegister(MultiTypeAdapter adapter) {
        mBinding.ry.setPadding(0, (int) LibCalcUtil.dp2px(this, 20), 0, 0);
        MemorialDayListViewBinder memorialDayListViewBinder = new MemorialDayListViewBinder();
        // TODO: 2018/2/8 点击时背景颜色异常
        memorialDayListViewBinder.setOnClickListener(new CustomItemViewBinder.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                MemorialDayBean bean = (MemorialDayBean) adapter.getItems().get(position);
                ActivityRouter.gotoMemorialDayDetail(bean.getTitle(), bean.getTime());
            }
        });
        adapter.register(MemorialDayBean.class, memorialDayListViewBinder);
    }

    @Override
    public void onClickAddData() {
        ActivityRouter.gotoAddMemorial();
    }

    @Override
    public String autoRefreshWhenReceiveEvent() {
        return ActionConstant.ADD_MEMORIAL;
    }

    @Override
    public void getData(int lastItemId, int size, DataCallback<List<MemorialDayBean>> callback) {
        Api.getInstance().getMemorialDayList(lastItemId, size, callback);
    }

    @Override
    public String setToolbarTitle() {
        return getString(R.string.memorial_day_list_title);
    }

    @Override
    public boolean needAddItemDecoration() {
        return false;
    }
}
