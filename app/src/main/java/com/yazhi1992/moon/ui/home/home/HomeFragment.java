package com.yazhi1992.moon.ui.home.home;

import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.databinding.FragmentHomeBinding;
import com.yazhi1992.moon.event.AddHomeImg;
import com.yazhi1992.yazhilib.utils.LibUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zengyazhi on 2018/1/23.
 */

public class HomeFragment extends Fragment {

    private FragmentHomeBinding mBinding;
    private HomeFragmentPresenter mPresenter = new HomeFragmentPresenter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter.getImgUrl(new DataCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if(LibUtils.notNullNorEmpty(data)) {
                    Glide.with(view.getContext()).load(data)
                            .into(mBinding.igHome);
                }
            }

            @Override
            public void onFailed(int code, String msg) {

            }
        });

        mBinding.igHome.setOnClickListener(v -> {
            Matisse.from(getActivity())
                    .choose(MimeType.ofImage())
                    .showSingleMediaType(true)
                    .countable(true)
                    .maxSelectable(1)
                    .setForceRatio(1, 1)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .forResult(10);
        });

        mBinding.llHopeDayList.setOnClickListener(v -> ActivityRouter.gotoHopeList());

        mBinding.llMemorialDayList.setOnClickListener(v -> ActivityRouter.gotoMemorialList());
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadImg(AddHomeImg bean) {
        Glide.with(getActivity()).load(bean.getUrl())
                .into(mBinding.igHome);
    }

}
