package com.yazhi1992.moon.api;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.AVException;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.AVUser;
import cn.leancloud.callback.DeleteCallback;
import cn.leancloud.callback.FindCallback;
import cn.leancloud.callback.GetCallback;
import cn.leancloud.callback.LogInCallback;
import cn.leancloud.callback.RequestEmailVerifyCallback;
import cn.leancloud.callback.RequestPasswordResetCallback;
import cn.leancloud.callback.SaveCallback;
import cn.leancloud.callback.SignUpCallback;
import cn.leancloud.convertor.ObserverBuilder;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class ApiWrapper {

    public static void saveAllInBackground(ArrayList<AVObject> list, SaveCallback callback) {
        AVObject.saveAllInBackground(list).subscribe(new Consumer<JSONArray>() {
            @Override
            public void accept(com.alibaba.fastjson.JSONArray objects) throws Exception {
                callback.done(null);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                AVException exception = new AVException(throwable);
                callback.internalDone(exception);
            }
        });
    }

    public static void saveInBackground(AVObject data) {
        data.saveInBackground().subscribe();
    }

    public static void saveInBackground(AVObject data, SaveCallback callback) {
        data.saveInBackground().subscribe(ObserverBuilder.buildSingleObserver(callback));
    }

    public static void deleteInBackground(AVObject data, DeleteCallback callback) {
        data.deleteInBackground().subscribe(ObserverBuilder.buildSingleObserver(callback));
    }

    public static void deleteInBackground(AVObject data) {
        data.deleteInBackground().subscribe();
    }

    public static void deleteAllInBackground(AVQuery<AVObject> datas, DeleteCallback callback) {
        datas.deleteAllInBackground().subscribe(ObserverBuilder.buildSingleObserver(callback));
    }

    public static void deleteAllInBackground(List<AVObject> datas, DeleteCallback callback) {
        AVObject.deleteAllInBackground(datas).subscribe(ObserverBuilder.buildSingleObserver(callback));
    }

    public static <T extends AVObject> void findInBackground(AVQuery<T> data, FindCallback<T> callback) {
        data.findInBackground().subscribe(ObserverBuilder.buildSingleObserver(callback));
    }

    public static void fetchInBackground(AVObject data, GetCallback<AVObject> callback) {
        data.fetchInBackground().subscribe(ObserverBuilder.buildSingleObserver(callback));
    }

    public static void getFirstInBackground(AVQuery<AVObject> data, GetCallback<AVObject> callback) {
        data.getFirstInBackground().subscribe(ObserverBuilder.buildSingleObserver(callback));
    }

    public static void signUpInBackground(AVUser data, SignUpCallback callback) {
        data.signUpInBackground().subscribe(ObserverBuilder.buildSingleObserver(callback));
    }

    public static void logInInBackground(String email, String pwd, LogInCallback<AVUser> callback) {
        AVUser.loginByEmail(email, pwd).subscribe(ObserverBuilder.buildSingleObserver(callback));
    }

    public static void requestPasswordResetInBackground(String email, RequestPasswordResetCallback callback) {
        AVUser.requestPasswordResetInBackground(email).subscribe(ObserverBuilder.buildSingleObserver(callback));
    }

    public static void requestEmailVerifyInBackground(String email, RequestEmailVerifyCallback callback) {
        AVUser.requestPasswordResetInBackground(email).subscribe(ObserverBuilder.buildSingleObserver(callback));
    }



}
