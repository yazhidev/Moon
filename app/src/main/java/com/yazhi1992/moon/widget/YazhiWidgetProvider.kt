package com.yazhi1992.moon.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.yazhi1992.moon.Mylog
import com.yazhi1992.moon.R

/**
 * Created by zengyazhi on 2018/1/2.
 *
 * https://developer.android.com/guide/topics/appwidgets/index.html
 */

class YazhiWidgetProvider : AppWidgetProvider() {

    companion object {
        //小部件点击事件的广播
        val CLICK_WIDGET = "com.yazhi1992.moon.click_widget"
    }

    /**
     * 小部件更新时会调用，在该方法里做点击事件
     * @param context
     * @param appWidgetManager
     * @param appWidgetIds
     */
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_layout)
        val intent = Intent(CLICK_WIDGET)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        remoteViews.setOnClickPendingIntent(R.id.widget_tv, pendingIntent)

        for (id in appWidgetIds) {
            appWidgetManager.updateAppWidget(id, remoteViews)
        }
    }

    /**
     * 接受小部件点击时发出的广播
     * @param context
     * @param intent
     */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == CLICK_WIDGET) {
            Toast.makeText(context, "test", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 小部件第一次添加到桌面时调用
     */
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        Mylog.d("onEnable")
    }
}
