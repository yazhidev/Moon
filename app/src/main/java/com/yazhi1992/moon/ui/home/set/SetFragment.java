package com.yazhi1992.moon.ui.home.set;

import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.CodeConstant;
import com.yazhi1992.moon.data.CheckUserDataChain;
import com.yazhi1992.moon.databinding.FragmentSetBinding;
import com.yazhi1992.moon.event.ChangeUserInfo;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.util.UploadPhotoHelper;
import com.yazhi1992.moon.util.PushManager;
import com.yazhi1992.yazhilib.utils.LibStatusBarUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zengyazhi on 2018/1/23.
 */

public class SetFragment extends Fragment {

    private FragmentSetBinding mBinding;
    private UserDaoUtil mUserDaoUtil;
    private SetViewModel mViewModel = new SetViewModel();
    private SetPresenter mPresenter = new SetPresenter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_set, container, false);
        mBinding.setItem(mViewModel);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.rlTop.setPadding(0, LibStatusBarUtils.getStatusBarHeight(getActivity()), 0, 0);

        mUserDaoUtil = new UserDaoUtil();

        User user = mUserDaoUtil.getUserDao();
        if (user != null) {
            mViewModel.myName.set(user.getName());
            mViewModel.myHeadUrl.set(user.getHeadUrl());
        }

        //同步另一半最新的昵称与头像
        mPresenter.getLoverInfo(new DataCallback<LoverInfo>() {
            @Override
            public void onSuccess(LoverInfo data) {
                mViewModel.loverName.set(data.name);
                mViewModel.loverHeadUrl.set(data.imgurl);
            }

            @Override
            public void onFailed(int code, String msg) {

            }
        });

        mBinding.rlLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setMessage(getString(R.string.logout))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setPositiveButton(getString(R.string.comfirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPresenter.logout(new DataCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean data) {
                                    CheckUserDataChain.getInstance().resetChainIndex();
                                    ActivityRouter.gotoNewLogin();
                                    getActivity().finish();
                                }

                                @Override
                                public void onFailed(int code, String msg) {

                                }
                            });
                        }
                    })
                    .show();
        });

        mBinding.rlAboutUs.setOnClickListener(v -> {
            ActivityRouter.gotoAboutUs();
        });

        mBinding.rlConfiguration.setOnClickListener(v -> ActivityRouter.gotoConfiguration());

        mBinding.tvName.setOnClickListener(v -> {
            ActivityRouter.gotoUserCenter();
        });

        mBinding.igMe.setOnClickListener(v -> {
            ActivityRouter.gotoUserCenter();
        });

        mBinding.igLover.setOnClickListener(v -> ActivityRouter.gotoImgPreview(mViewModel.loverHeadUrl.get()));
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
    public void changeUserName(ChangeUserInfo bean) {
        if(bean.getName() != null) {
            mViewModel.myName.set(bean.getName());
        }
        if(bean.getHeadUrl() != null) {
            mViewModel.myHeadUrl.set(bean.getHeadUrl());
        }
    }
}
