package com.yazhi1992.moon.ui.home.history;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVObject;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.HistoryMultitypeAdapter;
import com.yazhi1992.moon.adapter.MemorialDayViewBinder;
import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.NameContant;
import com.yazhi1992.moon.databinding.FragmentHistoryBinding;
import com.yazhi1992.moon.event.AddHistoryData;
import com.yazhi1992.moon.viewmodel.IHistoryBean;
import com.yazhi1992.moon.viewmodel.MemorialBeanWrapper;
import com.yazhi1992.moon.widget.PageRouter;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * Created by zengyazhi on 2018/1/23.
 */

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding mBinding;
    private HistoryMultitypeAdapter mMultiTypeAdapter;
    private Items mItems;
    private final int SIZE = 20;
    private int lastItemId = -1;
    private HistoryPresenter mPresenter = new HistoryPresenter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMultiTypeAdapter = new HistoryMultitypeAdapter();
        //纪念日
        mMultiTypeAdapter.register(MemorialBeanWrapper.class, new MemorialDayViewBinder(new MemorialDayViewBinder.MemorialDayViewListener() {
            @Override
            public void onClick(int id, int position) {
                if(id == R.id.delete) {
                    //删除
                    MemorialBeanWrapper data = (MemorialBeanWrapper) mItems.get(position);
                    mPresenter.delete(data.getObjectId(), data.getData().getObjId(), new DataCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean data) {
                            mMultiTypeAdapter.remove(position);
                        }

                        @Override
                        public void onFailed(int code, String msg) {
                            LibUtils.showToast(getActivity(), msg);
                        }
                    });
                }
            }
        }));
        //愿望清单

        mBinding.smartRefresh.setOnRefreshListener(refreshlayout -> getDatas(false));
        mBinding.smartRefresh.setOnLoadmoreListener(refreshlayout -> getDatas(true));

        mItems = new Items();
        mMultiTypeAdapter.setItems(mItems);
        mBinding.ryHistory.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mBinding.ryHistory.setAdapter(mMultiTypeAdapter);
        mBinding.ryHistory.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mBinding.ryHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scroll Down
                    if (mBinding.fab.isShown()) {
                        mBinding.fab.hide();
                    }
                } else if (dy < 0) {
                    // Scroll Up
                    if (!mBinding.fab.isShown()) {
                        mBinding.fab.show();
                    }
                }
            }
        });

        mBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageRouter.gotoAddMemorial();

                sendMsg();
            }
        });
        mBinding.smartRefresh.autoRefresh();

    }

    private Object transformData(int type, AVObject itemData) {
        Object data = null;
        switch (type) {
            case NameContant.LoveHistory.TYPE_MEMORIAL_DAY:
                data = new MemorialBeanWrapper(itemData);
                break;
            default:
                break;
        }
        return data;
    }

    // TODO: 2018/1/25 使用 dagger 分层
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
        Api.getInstance().getLoveHistory(lastItemId, SIZE, new DataCallback<List<AVObject>>() {
            @Override
            public void onSuccess(List<AVObject> data) {
                if(loadMore) {
                    mBinding.smartRefresh.finishLoadmore();
                } else {
                    mItems.clear();
                    mBinding.smartRefresh.finishRefresh();
                }
                if (data.size() > 0) {
                    for (AVObject loveHistoryItemData : data) {
                        mItems.add(transformData(loveHistoryItemData.getInt(NameContant.LoveHistory.TYPE), loveHistoryItemData));
                    }
                    mMultiTypeAdapter.notifyDataSetChanged();
                }
                mBinding.smartRefresh.setEnableLoadmore(data != null && data.size() == SIZE);
            }

            @Override
            public void onFailed(int code, String msg) {

            }
        });
    }

    private void sendMsg() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void addMemorial(AddHistoryData bean) {
        // TODO: 2018/1/29 传递过来丢失父类属性 userName userHeadUrl
        mBinding.smartRefresh.autoRefresh();
    }
}
