package com.yazhi1992.moon.ui.home.set;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.databinding.FragmentSetBinding;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.widget.PageRouter;

/**
 * Created by zengyazhi on 2018/1/23.
 */

public class SetFragment extends Fragment {

    private FragmentSetBinding mBinding;
    private UserDaoUtil mUserDaoUtil;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_set, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserDaoUtil = new UserDaoUtil();
        mUserDaoUtil.getUserDao(user -> {
            if(user != null) {
                mBinding.tvName.setText(user.getName());
            }
        });


        mBinding.tvName.setOnClickListener(v -> {

        });

        mBinding.btnLogout.setOnClickListener(v -> {
            mUserDaoUtil.clear();
            PageRouter.gotoLogin();
            getActivity().finish();
        });
    }
}
