package com.yazhi1992.moon.ui.addtext;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.BuildConfig;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.constant.SPKeyConstant;
import com.yazhi1992.moon.databinding.ActivityAddTextBinding;
import com.yazhi1992.moon.dialog.LoadingHelper;
import com.yazhi1992.moon.event.AddDataEvent;
import com.yazhi1992.moon.event.AddHomeImg;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.ui.ViewBindingUtils;
import com.yazhi1992.moon.ui.home.HomeActivity;
import com.yazhi1992.moon.util.IUploader;
import com.yazhi1992.moon.util.PushManager;
import com.yazhi1992.moon.util.StorageUtil;
import com.yazhi1992.moon.util.UploadPhotoHelper;
import com.yazhi1992.yazhilib.utils.LibFileUtils;
import com.yazhi1992.yazhilib.utils.LibSPUtils;
import com.yazhi1992.yazhilib.utils.LibUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

@Route(path = ActivityRouter.ADD_TEXT)
public class AddTextActivity extends BaseActivity {

    private ActivityAddTextBinding mBinding;
    private AddTextPresenter mPresenter = new AddTextPresenter();
    private String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_text);

        initToolBar(mBinding.toolbar);

        //显示软键盘
        LibUtils.showKeyoard(this, mBinding.etTitle);

        mBinding.igAdd.setVisibility(LibSPUtils.getBoolean(SPKeyConstant.PUSH_IMG_ENABLE, true) ? View.VISIBLE : View.GONE);

        mBinding.igAdd.setOnClickListener(v -> {
            //选择图片
            Matisse.from(this)
                    .choose(MimeType.ofImage())
                    .showSingleMediaType(true)
                    .capture(true)
                    .captureStrategy(new CaptureStrategy(true, BuildConfig.DEBUG ? "com.yazhi1992.moon.debug.provider" : "com.yazhi1992.moon.provider"))
                    .countable(true)
                    .maxSelectable(1)
                    .setForceRatio(1, 1, StorageUtil.getPath(StorageUtil.DirectoryName.IMAGE_DIRECTORY_NAME))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .forResult(10);
        });

        mBinding.btnAdd.setOnClickListener(v -> {
            String title = mBinding.etTitle.getText().toString();
            if (title.isEmpty()) {
                LibUtils.showToast(this, getString(R.string.add_text_empty));
                return;
            }
            mBinding.btnAdd.setLoading(true);
            //开始上传
            if (LibUtils.notNullNorEmpty(imgPath)) {
                UploadPhotoHelper.uploadPhoto(this, imgPath, new IUploader() {
                    @Override
                    public void upload(String filePath, DataCallback<String> callback) {
                        mPresenter.addText(title, filePath, new DataCallback<String>() {
                            @Override
                            public void onSuccess(String remoteImgUrl) {
                                callback.onSuccess(remoteImgUrl);
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

                    }

                    @Override
                    public void onSuc(String remoteImgUrl) {
                        mBinding.btnAdd.setLoading(false);
                        EventBus.getDefault().post(new AddDataEvent(ActionConstant.ADD_TEXT));
                        LibUtils.hideKeyboard(mBinding.etTitle);
                        finish();
                        PushManager.getInstance().pushAction(ActionConstant.ADD_TEXT);
                    }

                    @Override
                    public void onError(String msg) {
                        LibUtils.showToast(AddTextActivity.this, msg);
                        mBinding.btnAdd.setLoading(false);
                    }
                });
            } else {
                mPresenter.addText(title, null, new DataCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        EventBus.getDefault().post(new AddDataEvent(ActionConstant.ADD_TEXT));
                        LibUtils.hideKeyboard(mBinding.etTitle);
                        mBinding.btnAdd.setLoading(false);
                        finish();
                        PushManager.getInstance().pushAction(ActionConstant.ADD_TEXT);
                    }

                    @Override
                    public void onFailed(int code, String msg) {
                        LibUtils.showToast(AddTextActivity.this, msg);
                        mBinding.btnAdd.setLoading(false);
                    }
                });
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            List<String> uris = Matisse.obtainPathResult(data);
            imgPath = uris.get(0);
            ViewBindingUtils.imgUrl(mBinding.igAdd, imgPath);
        }
    }
}
