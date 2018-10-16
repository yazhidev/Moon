package com.yazhi1992.moon.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;

import com.yazhi1992.moon.BuildConfig;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.yazhilib.utils.LibFileUtils;
import com.yazhi1992.yazhilib.utils.LibUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;

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

/**
 * Created by zengyazhi on 2018/2/19.
 */

public class UploadPhotoHelper {

    public static void pickPhoto(Activity context, int requestCode) {
        Matisse.from(context)
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
                .forResult(requestCode);
    }

    public interface onUploadPhotoListener {
        void onStart();
        void onSuc(String remoteImgUrl);
        void onError(String msg);
    }

    /**
     * 上传图片
     * @param path
     */
    public static void uploadPhoto(Context context, String path, IUploader uploader, onUploadPhotoListener listener) {
        if (LibUtils.notNullNorEmpty(path)) {
            Observable.just(path)
                    .observeOn(Schedulers.io())
                    .concatMap(new Function<String, ObservableSource<File>>() {
                        @Override
                        public ObservableSource<File> apply(String s) throws Exception {
                            return Observable.create(new ObservableOnSubscribe<File>() {
                                @Override
                                public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                                    Luban.with(context)
                                            .load(path)                                   // 传人要压缩的图片列表
                                            .ignoreBy(100)                                  // 忽略不压缩图片的大小
                                            .setTargetDir(StorageUtil.getPath(StorageUtil.DirectoryName.IMAGE_DIRECTORY_NAME))   // 设置压缩后文件存储位置
                                            .setCompressListener(new OnCompressListener() { //设置回调
                                                @Override
                                                public void onStart() {
                                                    listener.onStart();
                                                }

                                                @Override
                                                public void onSuccess(File file) {
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
                                    uploader.upload(file.getPath(), new DataCallback<String>() {
                                        @Override
                                        public void onSuccess(String data) {
                                            //上传成功，删除本地图片
                                            if(!file.getPath().equals(path)) {
                                                //如果是原图则不删除
                                                LibFileUtils.deleteFile(file.getPath());
                                            }
                                            emitter.onNext(data);
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
                            listener.onSuc(path);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            listener.onError(throwable.getMessage());
                        }
                    });
        }
    }

}
