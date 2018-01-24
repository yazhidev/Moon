package com.yazhi1992.moon.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.SaveCallback
import com.yazhi1992.moon.ActivityRouter
import com.yazhi1992.moon.R
import com.yazhi1992.moon.constant.NameContant
import com.yazhi1992.moon.dialog.DatePickerDialog
import com.yazhi1992.moon.util.AppUtils
import com.yazhi1992.moon.viewmodel.MemorialDayBean
import com.yazhi1992.yazhilib.utils.LibUtils
import com.yazhi1992.yazhilib.utils.StatusBarUtils
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_add_memorial_day.*
import org.greenrobot.eventbus.EventBus
import java.util.*
import java.util.concurrent.TimeUnit

@Route(path = ActivityRouter.ADD_MEMORIAL)
class AddMemorialDayActivity : AppCompatActivity() {

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
                chooseDate = AppUtils.memorialDayYmdFormat.parse(str)
                tv_date.text = AppUtils.getTimeStrForMemorialDay(chooseDate)
            }
        }

        //显示软键盘
        LibUtils.showKeyoard(this, et_title)

        tv_date.text = AppUtils.getTimeStrForMemorialDay(Date())

        btn_comfirm.setOnClickListener {
            val title = et_title.text.toString()
            if (title.isEmpty()) {
                titleLayout.error = "标题不能为空"
                return@setOnClickListener
            }
            val memorialDayBean = MemorialDayBean(title, chooseDate.time)


            btn_comfirm.isLoading = true

            //存到纪念日表 + 首页历史列表
            val memorialDayObj = AVObject(NameContant.MemorialDay.CLAZZ_NAME)
            memorialDayObj.put(NameContant.MemorialDay.TITLE, memorialDayBean.title)
            memorialDayObj.put(NameContant.MemorialDay.TIME, memorialDayBean.time)

            val loveHistoryObj = AVObject(NameContant.LoveHistory.CLAZZ_NAME)
            loveHistoryObj.put(NameContant.LoveHistory.MEMORIAL_DAY, memorialDayObj)
            loveHistoryObj.put(NameContant.LoveHistory.TYPE, NameContant.LoveHistory.TYPE_MEMORIAL_DAY)

            //保存关联对象的同时，被关联的对象也会随之被保存到云端。
            loveHistoryObj.saveInBackground(object : SaveCallback() {
                override fun done(e: AVException?) {
                    btn_comfirm.isLoading = false
                    if (e == null) {
                        EventBus.getDefault().post(memorialDayBean)
                        LibUtils.hideKeyboard(et_title)
                        finish()
                    } else {
                        LibUtils.showToast(this@AddMemorialDayActivity, e.message)
                    }
                }
            })

        }
    }
}
