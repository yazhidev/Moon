package com.yazhi1992.moon.ui.home.home;

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
import com.yazhi1992.moon.databinding.FragmentHomeBinding;

/**
 * Created by zengyazhi on 2018/1/23.
 */

public class HomeFragment extends Fragment {

    private FragmentHomeBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String URL = "http://upload-images.jianshu.io/upload_images/1929170-6a96a2a204b50559.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1080/q/50";
        Glide.with(view.getContext()).load(URL)
                .into(mBinding.ig);

        mBinding.llHopeDayList.setOnClickListener(v -> {
            ActivityRouter.gotoHopeList();
        });

        mBinding.llMemorialDayList.setOnClickListener(v -> ActivityRouter.gotoMemorialList());
    }

}
