package com.yazhi1992.moon.ui.addtext;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.BuildConfig;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.databinding.ActivityAddTextBinding;
import com.yazhi1992.moon.dialog.LoadingHelper;
import com.yazhi1992.moon.event.AddDataEvent;
import com.yazhi1992.moon.event.AddHomeImg;
import com.yazhi1992.moon.ui.BaseActivity;
import com.yazhi1992.moon.ui.ViewBindingUtils;
import com.yazhi1992.moon.ui.home.HomeActivity;
import com.yazhi1992.moon.util.PushManager;
import com.yazhi1992.moon.util.StorageUtil;
import com.yazhi1992.yazhilib.utils.LibFileUtils;
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
            if(title.isEmpty()) {
                LibUtils.showToast(this, getString(R.string.add_text_empty));
                return;
            }
            mBinding.btnAdd.setLoading(true);
            //开始上传
            if (LibUtils.notNullNorEmpty(imgPath)) {
                Observable.just(imgPath)
                        .observeOn(Schedulers.io())
                        .concatMap(new Function<String, ObservableSource<File>>() {
                            @Override
                            public ObservableSource<File> apply(String s) throws Exception {
                                return Observable.create(new ObservableOnSubscribe<File>() {
                                    @Override
                                    public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                                        Luban.with(AddTextActivity.this)
                                                .load(imgPath)                                   // 传人要压缩的图片列表
                                                .ignoreBy(100)                                  // 忽略不压缩图片的大小
                                                .setTargetDir(StorageUtil.getPath(StorageUtil.DirectoryName.IMAGE_DIRECTORY_NAME))   // 设置压缩后文件存储位置
                                                .setCompressListener(new OnCompressListener() { //设置回调
                                                    @Override
                                                    public void onStart() {
                                                    }

                                                    @Override
                                                    public void onSuccess(File file) {
                                                        LibFileUtils.deleteFile(imgPath);
                                                        emitter.onNext(file);
                                                    }

                                                    @Override
                                                    public void onError(Throwable e) {
                                                        emitter.onError(e);
                                                    }
                                                }).launch();    //启动压缩
                                    }
                                });
                            }
                        })
                        .observeOn(Schedulers.io())
                        .concatMap(new Function<File, ObservableSource<String>>() {
                            @Override
                            public ObservableSource<String> apply(File file) throws Exception {
                                return Observable.create(new ObservableOnSubscribe<String>() {
                                    @Override
                                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                                        mPresenter.addText(title, file.getPath(), new DataCallback<Boolean>() {
                                            @Override
                                            public void onSuccess(Boolean data) {
                                                //上传成功，删除本地图片
                                                LibFileUtils.deleteFile(file.getPath());
                                                emitter.onNext(file.getPath());
                                            }

                                            @Override
                                            public void onFailed(int code, String msg) {
                                                emitter.onError(new Throwable(code + msg));
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String path) throws Exception {
                                mBinding.btnAdd.setLoading(false);
                                EventBus.getDefault().post(new AddDataEvent(ActionConstant.ADD_TEXT));
                                LibUtils.hideKeyboard(mBinding.etTitle);
                                finish();
                                PushManager.getInstance().pushAction(ActionConstant.ADD_TEXT);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LibUtils.showToast(AddTextActivity.this, throwable.getMessage());
                                mBinding.btnAdd.setLoading(false);
                            }
                        });
            } else {
                mPresenter.addText(title, null,new DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        EventBus.getDefault().post(new AddDataEvent(ActionConstant.ADD_TEXT));
                        LibUtils.hideKeyboard(mBinding.etTitle);
                        mBinding.btnAdd.setLoading(false);
                        finish();
                        PushManager.getInstance().pushAction(ActionConstant.ADD_TEXT);
                    }

                    @Override
                    public void onFailed(int code, String msg) {
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
            String path = uris.get(0);
            imgPath = uris.get(0);
            ViewBindingUtils.imgUrl(mBinding.igAdd, imgPath);
        }
    }
}
