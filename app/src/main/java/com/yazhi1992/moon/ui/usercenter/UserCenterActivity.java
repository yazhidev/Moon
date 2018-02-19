package com.yazhi1992.moon.ui.usercenter;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.CodeConstant;
import com.yazhi1992.moon.databinding.ActivityUserCenterBinding;
import com.yazhi1992.moon.dialog.LoadingHelper;
import com.yazhi1992.moon.event.AddHomeImg;
import com.yazhi1992.moon.event.ChangeUserInfo;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.ui.home.HomeActivity;
import com.yazhi1992.moon.util.IUploader;
import com.yazhi1992.moon.util.UploadPhotoHelper;
import com.yazhi1992.yazhilib.utils.LibUtils;
import com.zhihu.matisse.Matisse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

@Route(path = ActivityRouter.USER_CENTER)
public class UserCenterActivity extends BaseActivity {

    private ActivityUserCenterBinding mBinding;
    private UserCenterViewModel mViewModel = new UserCenterViewModel();
    private UserCenterPresenter mPresenter = new UserCenterPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_user_center);

        initToolBar(mBinding.toolbar);

        User userDao = new UserDaoUtil().getUserDao();
        if(userDao != null) {
            mViewModel.gender.set(userDao.getGender());
            mViewModel.headUrl.set(userDao.getHeadUrl());
            mViewModel.userName.set(userDao.getName());
        }

        mBinding.setItem(mViewModel);

        mBinding.rlHead.setOnClickListener(v -> UploadPhotoHelper.pickPhoto(this, CodeConstant.PICK_PHOTO_FOR_HEAD));
        mBinding.rlName.setOnClickListener(v -> ActivityRouter.gotoSetUserName(mViewModel.userName.get()));
        mBinding.rlGender.setOnClickListener(v -> ActivityRouter.gotoSetGender(false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CodeConstant.PICK_PHOTO_FOR_HEAD && resultCode == RESULT_OK) {
            //上传头像
            List<String> uris = Matisse.obtainPathResult(data);
            String path = uris.get(0);
            //开始上传
            if (LibUtils.notNullNorEmpty(path)) {
                UploadPhotoHelper.uploadPhoto(this, path, new IUploader() {
                    @Override
                    public void upload(String filePath, DataCallback<String> callback) {
                        mPresenter.updateHeadImg(filePath, new DataCallback<String>() {
                            @Override
                            public void onSuccess(String data) {
                                callback.onSuccess(data);
                            }

                            @Override
                            public void onFailed(int code, String msg) {
                                callback.onFailed(code, msg);
                            }
                        });
                    }
                }, new UploadPhotoHelper.onUploadPhotoListener() {
                    @Override
                    public void onStart() {
                        LoadingHelper.getInstance().showLoading(UserCenterActivity.this);
                    }

                    @Override
                    public void onSuc(String remoteImgUrl) {
                        new UserDaoUtil().updateUserHeadUrl(remoteImgUrl);
                        ChangeUserInfo changeUserInfo = new ChangeUserInfo();
                        changeUserInfo.setHeadUrl(remoteImgUrl);
                        EventBus.getDefault().post(changeUserInfo);
                        LoadingHelper.getInstance().closeLoading();
                    }

                    @Override
                    public void onError(String msg) {
                        LibUtils.showToast(UserCenterActivity.this, msg);
                        LoadingHelper.getInstance().closeLoading();
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeUserName(ChangeUserInfo bean) {
        if(bean.getName() != null) {
            mViewModel.userName.set(bean.getName());
        }
        if(bean.getHeadUrl() != null) {
            mViewModel.headUrl.set(bean.getHeadUrl());
        }
        if(bean.getGender() != 0) {
            mViewModel.gender.set(bean.getGender());
        }
    }

}
