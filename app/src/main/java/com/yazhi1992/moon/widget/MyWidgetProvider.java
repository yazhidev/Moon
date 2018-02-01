package com.yazhi1992.moon.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.util.MyLog;

/**
 * Created by zengyazhi on 2018/1/31.
 */

public class MyWidgetProvider extends AppWidgetProvider {
    //小部件点击事件的广播
    public static final String CLICK_WIDGET = "com.yazhi1992.moon.click_widget";

    /**
     * 小部件更新时会调用，在该方法里做点击事件
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent intent = new Intent(CLICK_WIDGET);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_tv, pendingIntent);

        for (int i = 0; i < appWidgetIds.length; i++) {
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
    }

    /**
     * 接受小部件点击时发出的广播
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction() == CLICK_WIDGET) {
            Toast.makeText(context, "test", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 小部件第一次添加到桌面时调用
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        MyLog.log("onEnable");
    }
}
