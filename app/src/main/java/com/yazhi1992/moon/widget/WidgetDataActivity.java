package com.yazhi1992.moon.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.databinding.ActivityWidgetDataBinding;

public class WidgetDataActivity extends Activity {

    int mAppWidgetId;
    private ActivityWidgetDataBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_widget_data);
        setContentView(R.layout.activity_widget_data);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        mBinding.tv1.setOnClickListener(v -> {
            updateWidget("111");
        });

        mBinding.tv2.setOnClickListener(v -> {
            updateWidget("222");
        });

    }

    private void updateWidget(String text) {
        AppWidgetManager instance = AppWidgetManager.getInstance(this);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_layout);
        remoteViews.setTextViewText(R.id.widget_tv, text);

        instance.updateAppWidget(mAppWidgetId, remoteViews);

        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
