package com.yazhi1992.moon.ui.bindlover;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.api.bean.BindLoverBean;
import com.yazhi1992.moon.api.bean.CheckBindStateBean;
import com.yazhi1992.moon.constant.NameConstant;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.yazhilib.utils.LibUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zengyazhi on 2018/1/26.
 */

public class BindLoverPresenter {

    public void getInviteNum(DataCallback<String> callback) {
        //1. 生成6位随机数，插入云端
        //存入本地
        User user = new UserDaoUtil().getUserDao();
        String inviteNum;
        inviteNum = user.getInviteNumber();
        if(LibUtils.isNullOrEmpty(inviteNum)) {
            //查询云端是否已有邀请码
            AVUser currentUser = AVUser.getCurrentUser();
            currentUser.fetchInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    String number = currentUser.getString(NameConstant.AVUserClass.INVITE_NUMBER);
                    if(LibUtils.isNullOrEmpty(number)) {
                        number = getRandomNum(6);
                        //存入云端
                        AVUser currentUser = AVUser.getCurrentUser();
                        //存入用户表
                        currentUser.put(NameConstant.AVUserClass.INVITE_NUMBER, number);
                        //存入bind_lover表
                        AVObject bindLoverItemData = new AVObject(NameConstant.BindLover.CLAZZ_NAME);
                        bindLoverItemData.put(NameConstant.BindLover.INVITE_NUMBER, number);
                        bindLoverItemData.put(NameConstant.BindLover.USER_ID, currentUser.getObjectId());
                        bindLoverItemData.put(NameConstant.BindLover.USER_NAME, currentUser.getUsername());
                        List<AVObject> objects = new ArrayList<>();
                        objects.add(currentUser);
                        objects.add(bindLoverItemData);
                        AVObject.saveAllInBackground(objects);
                        //存入数据库
                        user.setInviteNumber(number);
                        new UserDaoUtil().update(user);
                        callback.onSuccess(number);

                        // TODO: 2018/1/27 推送通知对方
                    } else {
                        callback.onSuccess(number);
                    }
                }
            });
        } else {
            callback.onSuccess(inviteNum);
        }
    }

    private String getRandomNum(int length) {
        String base = "0123456789";
        int len = base.length();

        Random random = new Random();
        StringBuilder sBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sBuilder.append(base.charAt(random.nextInt(len)));
        }

        return sBuilder.toString();
    }

    public void invite(String inviteNum, String userObjId, DataCallback<BindLoverBean> callback) {
        Api.getInstance().invite(inviteNum, userObjId, callback);
    }

    public void checkState(DataCallback<Integer> callback) {
        Api.getInstance().checkBindState(AVUser.getCurrentUser().getObjectId(), new DataCallback<CheckBindStateBean>() {
            @Override
            public void onSuccess(CheckBindStateBean data) {
                if(data.getState() == 0) {
                    //你绑定了对方，对方也绑定你了，更新云端数据
                    saveLoverInfoInDBAndRemote(data.getPeerObjId(), new DataCallback<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            callback.onSuccess(data.getState());
                        }

                        @Override
                        public void onFailed(int code, String msg) {
                            callback.onFailed(code, msg);
                        }
                    });
                } else {
                    callback.onSuccess(data.getState());
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                callback.onFailed(code, msg);
            }
        });
    }

    /**
     * 更新云端、本地关于另一半的信息
     * @param peerId 对方Id
     * @param callback
     */
    public void saveLoverInfoInDBAndRemote(String peerId, DataCallback<Boolean> callback) {
        AVUser currentUser = AVUser.getCurrentUser();
        //更新云端数据
        Api.getInstance().updateUserLoverInfo(currentUser.getObjectId(), peerId, new DataCallback<BindLoverBean>() {
            @Override
            public void onSuccess(BindLoverBean data) {
                //更新本地数据
                if(data.isBindComplete()) {
                    UserDaoUtil userDaoUtil = new UserDaoUtil();
                    userDaoUtil.updateLoveInfo(data.getLoverId()
                            , data.getLoverName()
                            , data.getLoverHeadUrl());
                    callback.onSuccess(true);
                } else {
                    callback.onFailed(0, "bind lover not complete");
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                callback.onFailed(code, msg);
            }
        });
    }
}
