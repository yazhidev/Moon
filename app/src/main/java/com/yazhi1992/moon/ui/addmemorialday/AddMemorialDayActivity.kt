package com.yazhi1992.moon.ui.addmemorialday

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.yazhi1992.moon.ActivityRouter
import com.yazhi1992.moon.R
import com.yazhi1992.moon.api.Api
import com.yazhi1992.moon.api.DataCallback
import com.yazhi1992.moon.dialog.DatePickerDialog
import com.yazhi1992.moon.event.AddHistoryData
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

            Api.getInstance().addMemorialDay(memorialDayBean.title, memorialDayBean.time, object : DataCallback<Boolean> {
                override fun onSuccess(data: Boolean?) {
                    EventBus.getDefault().post(AddHistoryData())
                    LibUtils.hideKeyboard(et_title)
                    btn_comfirm.isLoading = false
                    finish()
                }

                override fun onFailed(code: Int, msg: String?) {
                    LibUtils.showToast(this@AddMemorialDayActivity, msg)
                    btn_comfirm.isLoading = false
                }

            })
        }
    }
}
