package com.yazhi1992.moon.ui.base;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.base.CustomMultitypeAdapter;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.databinding.ActivityBaseListBinding;
import com.yazhi1992.moon.event.AddDataEvent;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.viewmodel.IHistoryBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by zengyazhi on 2018/2/5.
 *
 * 可复用的列表页
 */

public abstract class BaseListActivity<T> extends BaseActivity{

    protected ActivityBaseListBinding mBinding;
    private CustomMultitypeAdapter mAdapter;
    private Items mItems;
    private int lastItemId = -1;
    private final int SIZE = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_base_list);
        mBinding.toolbar.setTitle(setToolbarTitle());
        initToolBar(mBinding.toolbar);

        mAdapter = new CustomMultitypeAdapter();

        adapterRegister(mAdapter);

        mItems = new Items();
        mAdapter.setItems(mItems);
        if(needAddItemDecoration()) {
            mBinding.ry.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        }
        mBinding.ry.setAdapter(mAdapter);
        mBinding.ry.setLayoutManager(new LinearLayoutManager(this));

        mBinding.smartRefresh.setOnRefreshListener(refreshlayout -> getDatas(false));
        mBinding.smartRefresh.setOnLoadmoreListener(refreshlayout -> getDatas(true));

        mBinding.smartRefresh.autoRefresh();
    }

    public boolean needAddItemDecoration() {
        return true;
    }

    private String getDataType() {
        return autoRefreshWhenReceiveEvent();
    }

    public abstract void adapterRegister(MultiTypeAdapter adapter);

    public abstract void onClickAddData();

    public abstract @ActionConstant.AddAction String autoRefreshWhenReceiveEvent();

    public abstract void getData(int lastItemId, int size, DataCallback<List<T>> callback);

    public abstract String setToolbarTitle();

    private void getDatas(final boolean loadMore) {
        if (loadMore) {
            //获取末尾id
            if(mItems.size() > 0) {
                Object item = mItems.get(mItems.size() - 1);
                if(item != null && item instanceof IHistoryBean) {
                    lastItemId = ((IHistoryBean) item).getId();
                }
            }
        } else {
            lastItemId = -1;
        }

        getData(lastItemId, SIZE, new DataCallback<List<T>>() {
            @Override
            public void onSuccess(List<T> data) {
                if(loadMore) {
                    mBinding.smartRefresh.finishLoadmore();
                } else {
                    mItems.clear();
                    mBinding.smartRefresh.finishRefresh();
                }
                if (data.size() > 0) {
                    mItems.addAll(data);
                    mAdapter.notifyDataSetChanged();
                }
                mBinding.smartRefresh.setEnableLoadmore(data.size() == SIZE);
            }

            @Override
            public void onFailed(int code, String msg) {
                if(loadMore) {
                    mBinding.smartRefresh.finishLoadmore();
                } else {
                    mBinding.smartRefresh.finishRefresh();
                }
            }
        });
    }

    //添加右上角加号按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                onClickAddData();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addData(AddDataEvent bean) {
        if(getDataType().equals(bean.getAction())) {
            mBinding.smartRefresh.autoRefresh();
        }
    }

}
