package com.yazhi1992.moon.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.yazhi1992.moon.ActivityRouter
import com.yazhi1992.moon.R
import com.yazhi1992.moon.dialog.DatePickerDialog
import com.yazhi1992.moon.viewmodel.MemorialDayBean
import com.yazhi1992.yazhilib.utils.LibTimeUtils
import com.yazhi1992.yazhilib.utils.LibUtils
import com.yazhi1992.yazhilib.utils.StatusBarUtils
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_add_memorial_day.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Route(path = ActivityRouter.ADD_MEMORIAL)
class AddMemorialDayActivity : AppCompatActivity() {

    private val dayInWeekTime = arrayOf("", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日")
    val ymdFormat = SimpleDateFormat("yyyy-MM-dd")
    var chooseDate = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_memorial_day)

        StatusBarUtils.with(this)
                .setColor(resources.getColor(R.color.colorAccent))
                .init()

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            Observable.timer(500, TimeUnit.MILLISECONDS)
                    .subscribe { finish() }
        }

        tv_date.setOnClickListener {
            var dialog = DatePickerDialog()
            dialog.show(fragmentManager, "date")
            dialog.setComfirmlistener { str ->
                chooseDate = ymdFormat.parse(str)
                tv_date.text = getTimeStr(ymdFormat.parse(str))
            }
        }

        //显示软键盘
        LibUtils.showKeyoard(this, et_title)

        tv_date.text = getTimeStr(Date())

        btn_ok.setOnClickListener {
            val title = et_title.text.toString()
            if(title.isEmpty()) {
                titleLayout.error = "标题不能为空"
                return@setOnClickListener
            }
            val memorialDayBean = MemorialDayBean()
            memorialDayBean.title = title
            memorialDayBean.timeDate = chooseDate.time
            memorialDayBean.timeStr = tv_date.text.toString()
            EventBus.getDefault().post(memorialDayBean)

            LibUtils.hideKeyboard(et_title)

            finish()
        }
    }

    private fun getTimeStr(date: Date): String {
        return ymdFormat.format(date) + LibTimeUtils.getDayInWeek(date, dayInWeekTime)
    }
}
