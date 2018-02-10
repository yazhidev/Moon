package com.yazhi1992.moon.ui.hopelist;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.adapter.HopeListViewBinder;
import com.yazhi1992.moon.adapter.base.WithClicklistenerItemViewBinder;
import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.dialog.FinishHopeDialog;
import com.yazhi1992.moon.ui.base.BaseListFragment;
import com.yazhi1992.moon.util.EditDataHelper;
import com.yazhi1992.moon.viewmodel.HopeItemDataBean;

import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by zengyazhi on 2018/2/9.
 */

public class HopeTabFragment extends BaseListFragment<HopeItemDataBean> {

    private @TypeConstant.HopeType int mType;
    public static final String TYPE = "type";
    private FinishHopeDialog mFinishHopeDialog;

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
        hopeListViewBinder.setOnFinishListener(new HopeListViewBinder.OnFinishHopeListener() {

            @Override
            public void finish(int position, String id) {
                if(mFinishHopeDialog == null) {
                    mFinishHopeDialog = new FinishHopeDialog();
                }
                mFinishHopeDialog.showDialog(getActivity().getFragmentManager(), id);
            }
        });
        hopeListViewBinder.setOnItemClickListener(new WithClicklistenerItemViewBinder.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                //前往心愿详情页
                EditDataHelper.getInstance().saveData(adapter.getItems().get(position));
                ActivityRouter.gotoHopeDetail();
            }
        });
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
