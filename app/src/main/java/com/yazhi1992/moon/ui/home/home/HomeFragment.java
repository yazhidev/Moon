package com.yazhi1992.moon.ui.home.home;

import android.animation.ValueAnimator;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.Api;
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
    private ValueAnimator mValueAnimator;

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
                    initLoadingView();
                    Glide.with(view.getContext()).load(data)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    Glide.with(view.getContext()).load(R.mipmap.bg_home)
                                            .into(mBinding.igHome);
                                    hideLoadingView();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    hideLoadingView();
                                    return false;
                                }
                            })
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

//        mBinding.rlMcComming.setOnClickListener(v -> ActivityRouter.gotoMcDetail());
                mBinding.rlMcComming.setOnClickListener(v -> Api.getInstance().updateHistory());
    }

    private void initLoadingView() {
        mValueAnimator = ValueAnimator.ofFloat(360);
        mValueAnimator.setDuration(3000);
        mValueAnimator.setStartDelay(250);
        mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBinding.igLoading.setPivotX(mBinding.igLoading.getWidth() / 2);
                mBinding.igLoading.setPivotY(mBinding.igLoading.getHeight() / 2);
                mBinding.igLoading.setRotation((Float) animation.getAnimatedValue());
            }
        });
        mValueAnimator.start();
    }

    private void hideLoadingView() {
        if (mValueAnimator != null) {
            mValueAnimator.end();
        }
        mBinding.igLoading.setVisibility(View.GONE);
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
