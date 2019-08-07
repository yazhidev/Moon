package com.yazhi1992.moon.ui.base;

import android.app.Fragment;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.base.CustomMultitypeAdapter;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.databinding.FragmentBaseListBinding;
import com.yazhi1992.moon.event.AddDataEvent;
import com.yazhi1992.moon.viewmodel.IHistoryBean;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by zengyazhi on 2018/2/9.
 * <p>
 * 可复用的列表页
 */

public abstract class BaseListFragment<T> extends Fragment {

    protected FragmentBaseListBinding mBinding;
    private CustomMultitypeAdapter mAdapter;
    private Items mItems;
    private int lastItemId = -1;
    private final int SIZE = 20;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_base_list, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new CustomMultitypeAdapter();

        adapterRegister(mAdapter);

        mItems = new Items();
        mAdapter.setItems(mItems);
        if (needAddItemDecoration()) {
            mBinding.ry.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        }
        mBinding.ry.setAdapter(mAdapter);
        mBinding.ry.setLayoutManager(new LinearLayoutManager(getActivity()));

        mBinding.smartRefresh.setOnRefreshListener(refreshlayout -> getDatas(false));
        mBinding.smartRefresh.setOnLoadMoreListener(refreshlayout -> getDatas(true));

        Observable.timer(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        mBinding.smartRefresh.autoRefresh();

                    }
                });
    }

    public boolean needAddItemDecoration() {
        return true;
    }

    private String getDataType() {
        return autoRefreshWhenReceiveEvent();
    }

    public abstract void adapterRegister(MultiTypeAdapter adapter);

    public abstract @ActionConstant.AddAction
    String autoRefreshWhenReceiveEvent();

    public abstract void getData(int lastItemId, int size, DataCallback<List<T>> callback);

    private void getDatas(final boolean loadMore) {
        if (loadMore) {
            //获取末尾id
            if (mItems.size() > 0) {
                Object item = mItems.get(mItems.size() - 1);
                if (item != null && item instanceof IHistoryBean) {
                    lastItemId = ((IHistoryBean) item).getId();
                }
            }
        } else {
            lastItemId = -1;
        }

        getData(lastItemId, SIZE, new DataCallback<List<T>>() {
            @Override
            public void onSuccess(List<T> data) {
                if (loadMore) {
                    mBinding.smartRefresh.finishLoadMore();
                } else {
                    mItems.clear();
                    mBinding.smartRefresh.finishRefresh();

                    if (data.size() == 0) {
                        mBinding.multiView.showEmpty();
                    }
                }
                if (data.size() > 0) {
                    mItems.addAll(data);
                    mAdapter.notifyDataSetChanged();
                }
                mBinding.smartRefresh.setEnableLoadMore(data.size() == SIZE);
            }

            @Override
            public void onFailed(int code, String msg) {
                LibUtils.showToast(msg);
                if (loadMore) {
                    mBinding.smartRefresh.finishLoadMore();
                } else {
                    mBinding.smartRefresh.finishRefresh();

                    if (mItems.size() == 0) {
                        mBinding.multiView.showNetErr();
                    }
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addData(AddDataEvent bean) {
        if (getDataType().equals(bean.getAction())) {
            mBinding.smartRefresh.autoRefresh();
        }
    }

}
