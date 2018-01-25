package com.yazhi1992.moon.ui.home.set;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avos.sns.SNS;
import com.avos.sns.SNSType;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.databinding.FragmentSetBinding;
import com.yazhi1992.moon.sql.DatabaseManager;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.widget.PageRouter;

/**
 * Created by zengyazhi on 2018/1/23.
 */

public class SetFragment extends Fragment {

    private FragmentSetBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_set, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        User users = DatabaseManager.getInstance().getUser();
        if(users != null) {
            mBinding.tvName.setText(users.getName());
        }

        mBinding.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mBinding.btnLogout.setOnClickListener(v -> {
            SNS.logout(AVUser.getCurrentUser(), SNSType.AVOSCloudSNSQQ, new SaveCallback() {
                @Override
                public void done(AVException e) {
                    DatabaseManager.getInstance().getDaoSession().getUserDao().deleteAll();
                    PageRouter.gotoLogin();
                    getActivity().finish();
                }
            });
        });
    }
}
