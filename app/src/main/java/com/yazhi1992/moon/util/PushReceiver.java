package com.yazhi1992.moon.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.yazhi1992.moon.BaseApplication;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.constant.ActionConstant;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.ui.addmemorialday.AddMemorialActivity;
import com.yazhi1992.moon.ui.home.HomeActivity;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zengyazhi on 2018/3/31.
 */

public class PushReceiver extends BroadcastReceiver {

    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        try {
            //获取消息内容
            Bundle extras = intent.getExtras();
            if(extras == null) return;
            String string = extras.getString("com.avos.avoscloud.Data");
            if(LibUtils.isNullOrEmpty(string)) return;
            JSONObject json = new JSONObject(string);
            String userId = json.getString("moonUserId");
            String moonAction = json.getString("moonAction");
            Log.e("zyz", "receive " + moonAction);
            User userDao = new UserDaoUtil().getUserDao();
            String loverId = userDao.getLoverId();
            if(LibUtils.notNullNorEmpty(loverId) && loverId.equals(userId)) {
                handlAction(moonAction);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //创建推送
    public void createNotification(String title, String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.mipmap.app_icon)
                        .setContentTitle(title)
                        .setContentText(text);

        Intent resultIntent = new Intent(mContext, HomeActivity.class);
        resultIntent.putExtra(ActionConstant.Notification.ACTION_KEY, ActionConstant.Notification.ACTION_VALUE_HISTORY);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(AddMemorialActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private void handlAction(String action) {
        switch (action) {
            case ActionConstant.ADD_MEMORIAL:
                //对方新增纪念日
                createNotification(BaseApplication.getInstance().getString(R.string.notification_add_memorial_title)
                        , BaseApplication.getInstance().getString(R.string.notification_add_memorial_content));
                break;
            case ActionConstant.ADD_HOPE:
                //对方新增愿望
                createNotification(BaseApplication.getInstance().getString(R.string.notification_add_hope_title)
                        , BaseApplication.getInstance().getString(R.string.notification_add_memorial_content));
                break;
            case ActionConstant.ADD_TEXT:
                //对方新增文本
                createNotification(BaseApplication.getInstance().getString(R.string.notification_add_text_title)
                        , BaseApplication.getInstance().getString(R.string.notification_add_memorial_content));
                break;
            case ActionConstant.UPDATE_MC:
                //对方更新了mc信息
                createNotification(BaseApplication.getInstance().getString(R.string.notification_update_mc_title)
                        , BaseApplication.getInstance().getString(R.string.notification_add_memorial_content));
                break;
            default:
                break;
        }
    }

}