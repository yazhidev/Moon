package com.yazhi1992.moon.ui.memoriallist;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.CustomMultitypeAdapter;
import com.yazhi1992.moon.adapter.MemorialDayListViewBinder;
import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.databinding.ActivityMemorialListBinding;
import com.yazhi1992.moon.event.AddHistoryData;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.viewmodel.IHistoryBean;
import com.yazhi1992.moon.viewmodel.MemorialDayBean;
import com.yazhi1992.moon.widget.PageRouter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import me.drakeet.multitype.Items;

@Route(path = PageRouter.MEMORIAL_LIST)
public class MemorialListActivity extends BaseActivity {

    private ActivityMemorialListBinding mBinding;
    private CustomMultitypeAdapter mAdapter;
    private Items mItems;
    private int lastItemId = -1;
    private final int SIZE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_memorial_list);
        initToolBar(mBinding.toolbar);

        mAdapter = new CustomMultitypeAdapter();
        mAdapter.register(MemorialDayBean.class, new MemorialDayListViewBinder(new MemorialDayListViewBinder.MemorialDayViewListener() {
            @Override
            public void onClick(int id, int position) {
                if (id == R.id.root) {
                    //前往详情页
                }
            }
        }));
        mItems = new Items();
        mAdapter.setItems(mItems);
        mBinding.ry.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mBinding.ry.setAdapter(mAdapter);
        mBinding.ry.setLayoutManager(new LinearLayoutManager(this));

        mBinding.smartRefresh.setOnRefreshListener(refreshlayout -> getDatas(false));
        mBinding.smartRefresh.setOnLoadmoreListener(refreshlayout -> getDatas(true));

        mBinding.smartRefresh.autoRefresh();
    }

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
        Api.getInstance().getMemorialDayList(lastItemId, SIZE, new DataCallback<List<MemorialDayBean>>() {
            @Override
            public void onSuccess(List<MemorialDayBean> data) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memorial_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_memorial:
                PageRouter.gotoAddMemorial();
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

    @Subscribe
    public void addMemorial(AddHistoryData bean) {
        mBinding.smartRefresh.autoRefresh();
    }

}
