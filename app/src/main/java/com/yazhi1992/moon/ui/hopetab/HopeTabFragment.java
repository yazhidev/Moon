package com.yazhi1992.moon.ui.hopetab;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yazhi1992.moon.adapter.HopeListViewBinder;
import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.ui.base.BaseListFragment;
import com.yazhi1992.moon.viewmodel.HopeItemDataBean;

import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by zengyazhi on 2018/2/9.
 */

public class HopeTabFragment extends BaseListFragment<HopeItemDataBean> {

    private @TypeConstant.HopeType int mType;
    public static final String TYPE = "type";

    public static HopeTabFragment newInstance(@TypeConstant.HopeType int type) {
        HopeTabFragment hopeTabFragment = new HopeTabFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, type);
        hopeTabFragment.setArguments(bundle);
        return hopeTabFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt(TYPE);
    }

    @Override
    public void adapterRegister(MultiTypeAdapter adapter) {
        HopeListViewBinder hopeListViewBinder = new HopeListViewBinder();
        adapter.register(HopeItemDataBean.class, hopeListViewBinder);
    }

    @Override
    public String autoRefreshWhenReceiveEvent() {
        return ActionConstant.ADD_HOPE;
    }

    @Override
    public void getData(int lastItemId, int size, DataCallback<List<HopeItemDataBean>> callback) {
        Api.getInstance().getHopeList(mType, lastItemId, size, callback);
    }
}
