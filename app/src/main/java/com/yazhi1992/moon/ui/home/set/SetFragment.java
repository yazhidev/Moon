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

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.yazhi1992.moon.BaseApplication;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.databinding.FragmentSetBinding;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.PageRouter;
import com.yazhi1992.yazhilib.utils.LibUtils;

import java.util.Arrays;

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

        mUserDaoUtil = new UserDaoUtil();
        User user = mUserDaoUtil.getUserDao();
        if(user != null) {
            mViewModel.myName.set(user.getName());
            mViewModel.myHeadUrl.set(user.getHeadUrl());
            mViewModel.loverHeadUrl.set(user.getLoverHeadUrl());
            mViewModel.loverName.set(user.getLoverName());
        }

        mBinding.igLover.setOnClickListener(v -> {
        });

        mBinding.tvName.setOnClickListener(v -> {

        });

        mBinding.btnSendMsg.setOnClickListener(v -> {
        });

        mBinding.btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setMessage(getString(R.string.logout))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setPositiveButton(getString(R.string.comfirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPresenter.logout(new DataCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean data) {
                                    PageRouter.gotoLogin();
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

        mBinding.btnAboutUs.setOnClickListener(v -> {
            PageRouter.gotoAboutUs();
        });

        mBinding.btnMemorialDay.setOnClickListener(v -> PageRouter.gotoMemorialList());

    }

}
