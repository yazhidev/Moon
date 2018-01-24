package com.yazhi1992.moon.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.adapter.MemorialDayViewBinder;
import com.yazhi1992.moon.databinding.FragmentHistoryBinding;
import com.yazhi1992.moon.viewmodel.MemorialDayBean;
import com.yazhi1992.moon.widget.PageRouter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
