package com.yazhi1992.moon.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewAnimationUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.yazhi1992.moon.ActivityRouter
import com.yazhi1992.moon.R
import com.yazhi1992.yazhilib.utils.StatusBarUtils
import kotlinx.android.synthetic.main.activity_main3.*

@Route(path = ActivityRouter.MAIN3)
class Main3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        StatusBarUtils.with(this)
                .init()

        findViewById<View>(R.id.button).setOnClickListener { val main3Activity = this@Main3Activity }

        outview.post(object : Runnable {
            override fun run() {
                outview.visibility = View.VISIBLE
                val centerX = fab.left + fab.width / 2
                val centerY = fab.top + fab.width / 2
                val createCircularReveal = ViewAnimationUtils.createCircularReveal(outview, centerX, centerY, 0F, Math.sqrt(Math.pow(centerX.toDouble(), 2.0) + Math.pow(centerY.toDouble(), 2.0)).toFloat())
                createCircularReveal.setDuration(1000).start()
            }
        })
    }
}
