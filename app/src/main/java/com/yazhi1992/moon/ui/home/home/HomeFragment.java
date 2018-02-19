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
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.CodeConstant;
import com.yazhi1992.moon.constant.SPKeyConstant;
import com.yazhi1992.moon.databinding.FragmentHomeBinding;
import com.yazhi1992.moon.event.AddHomeImg;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.util.UploadPhotoHelper;
import com.yazhi1992.yazhilib.utils.LibSPUtils;
import com.yazhi1992.yazhilib.utils.LibUtils;

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
                if (LibUtils.notNullNorEmpty(data)) {
                    Glide.with(view.getContext()).load(data)
                            .into(mBinding.igHome);
                } else {
                    Glide.with(view.getContext()).load(R.mipmap.bg_home)
                            .into(mBinding.igHome);
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                Glide.with(view.getContext()).load(R.mipmap.bg_home)
                        .into(mBinding.igHome);
            }
        });

        HomeViewModel homeViewModel = new HomeViewModel();
        homeViewModel.mGender.set(new UserDaoUtil().getUserDao().getGender());
        mBinding.setItem(homeViewModel);

        mBinding.igHome.setOnClickListener(v -> {
            takePic();
        });

        mBinding.rlHopeDayList.setOnClickListener(v -> ActivityRouter.gotoHopeList());

        mBinding.rlMemorialDayList.setOnClickListener(v -> ActivityRouter.gotoMemorialList());

        mBinding.rlMcComming.setOnClickListener(v -> ActivityRouter.gotoMcDetail());

    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.rlMcComming.setVisibility(LibSPUtils.getBoolean(SPKeyConstant.MC_ENABLE, true) ? View.VISIBLE : View.GONE);
    }

    void takePic() {
        UploadPhotoHelper.pickPhoto(getActivity(), CodeConstant.PICK_PHOTO_FOR_HOME);
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
