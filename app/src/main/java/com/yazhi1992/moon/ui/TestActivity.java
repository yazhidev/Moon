package com.yazhi1992.moon.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yazhi1992.moon.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

//        MonthView viewById = findViewById(R.id.monthView);
//        viewById.setDateList(CalendarUtil.getMonthDate(2017, 2), CalendarUtil.getMonthDays(2017, 2));
    }
}
