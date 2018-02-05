package com.yazhi1992.moon.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.yazhi1992.moon.BaseApplication;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.ui.addmemorialday.AddMemorialActivity;
import com.yazhi1992.moon.ui.home.HomeActivity;
import com.yazhi1992.yazhilib.utils.LibUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zengyazhi on 2018/2/4.
 *
 * 推送消息给对方
 */

public class PushManager {
    private AVIMClient mClient;
    private String mLoverObjid;

    private PushManager(){
    }

    private static class PushManagerHolder {
        private static PushManager INSTANCE= new PushManager();
    }

    public static PushManager getInstance() {
        return PushManagerHolder.INSTANCE;
    }

    public void init() {
        //聊天登录
        AVIMClient jerry = AVIMClient.getInstance(AVUser.getCurrentUser().getObjectId());
        jerry.open(new AVIMClientCallback(){
            @Override
            public void done(AVIMClient client,AVIMException e){
                if(e==null){
                    mClient = client;
                } else {
                    LibUtils.showToast(BaseApplication.getInstance(), BaseApplication.getInstance().getString(R.string.pushmanager_init_failed));
                }
            }
        });

        AVIMMessageManager.registerDefaultMessageHandler(new CustomMessageHandler());
    }

    //创建推送
    public void createNotification(String title, String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(BaseApplication.getInstance().getTopActivity())
                        .setSmallIcon(R.mipmap.app_icon)
                        .setContentTitle(title)
                        .setContentText(text);

        Intent resultIntent = new Intent(BaseApplication.getInstance().getTopActivity(), HomeActivity.class);
        resultIntent.putExtra(ActionConstant.Notification.ACTION_KEY, ActionConstant.Notification.ACTION_VALUE_HISTORY);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(BaseApplication.getInstance());
        stackBuilder.addParentStack(AddMemorialActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) BaseApplication.getInstance().getTopActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    public static class CustomMessageHandler extends AVIMMessageHandler {
        //接收到消息后的处理逻辑
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation conversation, AVIMClient client){
            if(new UserDaoUtil().getUserDao() != null) {
                if(message instanceof AVIMTextMessage){
                    String text = ((AVIMTextMessage) message).getText();
                    switch (text) {
                        case ActionConstant.ADD_MEMORIAL:
                            //对方新增纪念日
                            PushManager.getInstance().createNotification(BaseApplication.getInstance().getString(R.string.notification_add_memorial_title)
                                    , BaseApplication.getInstance().getString(R.string.notification_add_memorial_content));
                            break;
                        case ActionConstant.ADD_HOPE:
                            //对方新增愿望
                            PushManager.getInstance().createNotification(BaseApplication.getInstance().getString(R.string.notification_add_hope_title)
                                    , BaseApplication.getInstance().getString(R.string.notification_add_memorial_content));
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        public void onMessageReceipt(AVIMMessage message,AVIMConversation conversation,AVIMClient client){

        }
    }

    public List<String> getComversationList() {
        if(mLoverObjid == null) {
            mLoverObjid = new UserDaoUtil().getUserDao().getLoverId();
        }
        if(mLoverObjid != null) {
            return Arrays.asList(mLoverObjid);
        } else {
            return null;
        }
    }

    public AVIMClient getClient() {
        return mClient;
    }

    public void pushAction(@ActionConstant.AddAction String action) {
        switch (action) {
        case ActionConstant.ADD_MEMORIAL:
            postMsg(action);
            break;
        default:
            break;
        }
    }

    //发消息给对方
    private void postMsg(@ActionConstant.AddAction String action) {
        getClient().createConversation(getComversationList(), action, null,
                new AVIMConversationCreatedCallback() {

                    @Override
                    public void done(AVIMConversation conversation, AVIMException e) {
                        if (e == null) {
                            AVIMTextMessage msg = new AVIMTextMessage();
                            msg.setText(action);
                            // 发送消息
                            conversation.sendMessage(msg, new AVIMConversationCallback() {

                                @Override
                                public void done(AVIMException e) {
                                    if (e == null) {
                                    }
                                }
                            });
                        }
                    }
                });
    }

}
