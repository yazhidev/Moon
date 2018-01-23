package com.yazhi1992.moon.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.yazhi1992.moon.ActivityRouter
import com.yazhi1992.moon.R
import com.yazhi1992.moon.dialog.DatePickerDialog
import com.yazhi1992.yazhilib.utils.LibUtils
import com.yazhi1992.yazhilib.utils.StatusBarUtils
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_add_memorial_day.*
import java.util.concurrent.TimeUnit

@Route(path = ActivityRouter.ADD_MEMORIAL)
class AddMemorialDayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_memorial_day)

        StatusBarUtils.with(this)
                .setColor(resources.getColor(R.color.colorAccent))
                .init()

        btn_ok.setOnClickListener {
            LibUtils.showToast(this, "")
        }

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            Observable.timer(500, TimeUnit.MILLISECONDS)
                    .subscribe { finish() }
        }

        tv_date.setOnClickListener {
            var dialog = DatePickerDialog()
            dialog.show(fragmentManager, "date")
        }
    }
}
