package com.yazhi1992.moon.util;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;
import com.yazhi1992.moon.BaseApplication;
import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.push.PushJson;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.ui.home.HomeActivity;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zengyazhi on 2018/2/4.
 * <p>
 * 推送消息给对方
 */

public class PushManager {

    private PushManager() {
    }

    private static class PushManagerHolder {
        private static PushManager INSTANCE = new PushManager();
    }

    public static PushManager getInstance() {
        return PushManagerHolder.INSTANCE;
    }

    public void register() {
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    MyLog.log("pushManager register " + installationId);
                    UserDaoUtil userDaoUtil = new UserDaoUtil();
                    User userDao = userDaoUtil.getUserDao();
                    String loverId = userDao.getLoverId();
                    if(LibUtils.notNullNorEmpty(loverId)) {
                        //同步pushiD
                        Api.getInstance().registerPushIdToUser(installationId, new DataCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean data) {
                            }

                            @Override
                            public void onFailed(int code, String msg) {

                            }
                        });
                    }
                } else {
                    // 保存失败，输出错误信息
                    MyLog.log("pushManager register error");
                }
            }
        });
        // 设置默认打开的 Activity
        PushService.setDefaultPushCallback(BaseApplication.getInstance(), HomeActivity.class);
        getPeerPushId(new DataCallback<String>() {
            @Override
            public void onSuccess(String data) {
                new UserDaoUtil().updatePeerPushId(data);
            }

            @Override
            public void onFailed(int code, String msg) {

            }
        });
    }

    public void getPeerPushId(DataCallback<String> callback) {
        Api.getInstance().getPeerPushId(callback);
    }

    public String getPeerPushId() {
        return new UserDaoUtil().getUserDao().getPushId();
    }

    public void pushAction(@ActionConstant.AddAction String action) {
        postMsg(action);
    }

    //发消息给对方
    private void postMsg(@ActionConstant.AddAction String action) {
        Log.e("zyz", "peerId " + getPeerPushId());
        String peerPushId = getPeerPushId();
        if(LibUtils.isNullOrEmpty(peerPushId)) return;
        try {
            AVPush push = new AVPush();
            JSONObject data = null;
            String userId = new UserDaoUtil().getUserDao().getObjectId();
            data = new JSONObject(
                    "{\"action\": \"com.yazhi1992.moon\", \"moonAction\": \"" + action + "\", \"moonUserId\": \"" + userId + "\", \"newsItem\": \"Man bites dog\"  }");
            push.setData(data);
            push.setCloudQuery("select * from _Installation where installationId ='" + peerPushId
                    + "'");
            push.sendInBackground(new SendCallback() {
                @Override
                public void done(AVException e) {
                    Log.e("zyz", "push ");
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
//
//        AVQuery pushQuery = AVInstallation.getQuery();
//        // 假设 THE_INSTALLATION_ID 是保存在用户表里的 installationId，
//        // 可以在应用启动的时候获取并保存到用户表
//        pushQuery.whereEqualTo("installationId", getPeerPushId());
//
//        AVPush.sendMessageInBackground(pushJson.toString(),  pushQuery, new SendCallback() {
//            @Override
//            public void done(AVException e) {
//
//            }
//        });
    }

}
