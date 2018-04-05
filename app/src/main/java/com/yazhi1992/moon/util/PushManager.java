package com.yazhi1992.moon.util;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SendCallback;
import com.yazhi1992.moon.BaseApplication;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.ui.startup.StartActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zengyazhi on 2018/2/4.
 * <p>
 * 推送消息给对方
 */

public class PushManager {
    private String mPeerId;

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
                    // 关联  installationId 到用户表等操作……
                } else {
                    // 保存失败，输出错误信息
                    MyLog.log("pushManager register error");
                }
            }
        });
        // 设置默认打开的 Activity
        PushService.setDefaultPushCallback(BaseApplication.getInstance(), StartActivity.class);
    }

    public String getPeerId() {
        if (mPeerId == null) {
            mPeerId = new UserDaoUtil().getUserDao().getLoverId();
        }
        return mPeerId;
    }

    public void pushAction(@ActionConstant.AddAction String action) {
        postMsg(action);
    }

    //发消息给对方
    private void postMsg(@ActionConstant.AddAction String action) {
        if(getPeerId() == null) return;
        try {
            AVPush push = new AVPush();
            JSONObject data =
                    null;
            data = new JSONObject(
                    "{\"action\": \"com.yazhi1992.moon\", \"moonAction\": \"" + action + "\", \"newsItem\": \"Man bites dog\"  }");

//            839f33b894d062586e64c97136e2aa86

            push.setData(data);
            push.setCloudQuery("select * from _Installation where installationId ='" + getPeerId()
                    + "'");
            push.sendInBackground(new SendCallback() {

                @Override
                public void done(AVException e) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
