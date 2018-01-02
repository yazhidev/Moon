package com.yazhi1992.moon.widget

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.yazhi1992.moon.R
import android.appwidget.AppWidgetManager
import android.support.v4.app.NotificationCompat.getExtras
import android.content.Intent
import android.widget.RemoteViews
import kotlinx.android.synthetic.main.activity_choose_data_for_widget.*

/**
 * 桌面小部件配置页面，清单文件中该页面需要添加
 *
 *   <intent-filter>
 *      <action android:name="android.appwidget.action.APPWIDGET_CONFIGURE"/>
 *   </intent-filter>
 *
 */
class ChooseDataForWidgetActivity : Activity() {

    var mAppWidgetId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_data_for_widget)

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        tv1.setOnClickListener { updateWidget("111") }

        tv2.setOnClickListener { updateWidget("222") }
    }

    fun updateWidget(text:String) {
        val instance = AppWidgetManager.getInstance(this@ChooseDataForWidgetActivity)
        val remoteViews = RemoteViews(packageName, R.layout.widget_layout)
        remoteViews.setTextViewText(R.id.widget_tv, text)

        instance.updateAppWidget(mAppWidgetId, remoteViews)

        val intent = Intent()
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
