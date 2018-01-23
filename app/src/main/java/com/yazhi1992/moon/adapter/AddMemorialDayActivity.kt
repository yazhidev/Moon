package com.yazhi1992.moon.adapter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.yazhi1992.moon.ActivityRouter
import com.yazhi1992.moon.R
import com.yazhi1992.yazhilib.utils.LibUtils
import kotlinx.android.synthetic.main.activity_add_memorial_day.*

@Route(path = ActivityRouter.ADD_MEMORIAL)
class AddMemorialDayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_memorial_day)

        btn_ok.setOnClickListener {
            val title = et_title.text.toString() + " " + datePicker.year + " " + datePicker.month + " " + datePicker.dayOfMonth
            LibUtils.showToast(this, title)
        }
    }
}
