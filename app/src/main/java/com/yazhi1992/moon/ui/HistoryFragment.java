package com.yazhi1992.moon.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.MemorialDayViewBinder;
import com.yazhi1992.moon.constant.NameContant;
import com.yazhi1992.moon.databinding.FragmentHistoryBinding;
import com.yazhi1992.moon.viewmodel.MemorialDayBean;
import com.yazhi1992.moon.widget.PageRouter;

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
    private MultiTypeAdapter mMultiTypeAdapter;
    private Items mItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMultiTypeAdapter = new MultiTypeAdapter();
        mMultiTypeAdapter.register(MemorialDayBean.class, new MemorialDayViewBinder());
//        mItems.add();

        mBinding.smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getDatas(false);
            }
        });

        mItems = new Items();
        mMultiTypeAdapter.setItems(mItems);
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

    private void getDatas(boolean loadMore) {
        if(loadMore) {

        } else {
            AVQuery<AVObject> query = new AVQuery<>(NameContant.LoveHistory.CLAZZ_NAME);
            query.include(NameContant.MemorialDay.CLAZZ_NAME);
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    Log.e("zyz", "");
                    mBinding.smartRefresh.finishRefresh();

                    mItems.clear();
                    transformeData(list);
                }
            });
        }
    }

    private void transformeData(List<AVObject> list) {
        if(list.size() > 0) {
            for(AVObject data:list) {
                int anInt = data.getInt(NameContant.LoveHistory.TYPE);
                if(anInt == NameContant.LoveHistory.TYPE_MEMORIAL_DAY) {
                    AVObject avObject = data.getAVObject(NameContant.LoveHistory.MEMORIAL_DAY);
                    MemorialDayBean memorialDayBean = new MemorialDayBean(avObject.getString(NameContant.MemorialDay.TITLE), avObject.getLong(NameContant.MemorialDay.TIME));
                    mItems.add(memorialDayBean);
                }
            }
            mMultiTypeAdapter.notifyDataSetChanged();
        }
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
    public void addMemorial(MemorialDayBean bean) {
        mItems.add(0, bean);
        mMultiTypeAdapter.notifyItemInserted(0);
        mBinding.ryHistory.smoothScrollToPosition(0);
    }
}
