package com.yazhi1992.moon.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.yazhi1992.moon.R;
import com.yazhi1992.moon.widget.calendarview.Calendarview;
import com.yazhi1992.moon.widget.calendarview.OnPagerChangeListener;

public class TestActivity extends AppCompatActivity {

    private TextView mViewById1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Calendarview viewById = findViewById(R.id.calendar);
        viewById.setPagerChangeListener(new OnPagerChangeListener() {
            @Override
            public void onPagerChanged(int[] date) {
                mViewById1.setText(date[0] + "-" + date[1]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewById1 = findViewById(R.id.tv);
//        viewById.setDateList(CalendarUtil.getMonthDate(2017, 2), CalendarUtil.getMonthDays(2017, 2));
    }
}
