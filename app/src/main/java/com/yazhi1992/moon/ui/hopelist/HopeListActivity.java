package com.yazhi1992.moon.ui.hopelist;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.HopeListViewBinder;
import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.ui.base.BaseListActivity;
import com.yazhi1992.moon.viewmodel.HopeItemDataBean;

import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

@Route(path = ActivityRouter.HOPE_LIST)
public class HopeListActivity extends BaseListActivity<HopeItemDataBean> {

    @Override
    public void adapterRegister(MultiTypeAdapter adapter) {
        HopeListViewBinder hopeListViewBinder = new HopeListViewBinder();
        adapter.register(HopeItemDataBean.class, hopeListViewBinder);
    }

    @Override
    public void onClickAddData() {
        ActivityRouter.gotoAddHope();
    }

    @Override
    public String autoRefreshWhenReceiveEvent() {
        return ActionConstant.ADD_HOPE;
    }

    @Override
    public void getData(int lastItemId, int size, DataCallback<List<HopeItemDataBean>> callback) {
        Api.getInstance().getHopeList(lastItemId, size, callback);
    }

    @Override
    public String setToolbarTitle() {
        return getString(R.string.hope_list_title);

    }
}
