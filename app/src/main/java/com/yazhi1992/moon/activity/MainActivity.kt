package com.yazhi1992.moon.activity

import android.animation.Animator
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewAnimationUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.yazhi1992.moon.ActivityRouter
import com.yazhi1992.moon.R
import com.yazhi1992.moon.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

@Route(path = ActivityRouter.MAIN)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        btn.setOnClickListener {
            outRl.setBackgroundColor(Color.BLUE)
            outRl.visibility = View.VISIBLE
            val centerX = btn.left + btn.width / 2
            val centerY = btn.top + btn.width / 2
            val createCircularReveal = ViewAnimationUtils.createCircularReveal(outRl, centerX, centerY, 0F, Math.sqrt(Math.pow(centerX.toDouble(), 2.0) + Math.pow(centerY.toDouble(), 2.0)).toFloat())
            createCircularReveal.setDuration(1000).start()
            createCircularReveal.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    ActivityRouter.gotoMain2(this@MainActivity)

                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }

            })
        }
    }

    override fun onStop() {
        super.onStop()
        outRl.visibility = View.GONE
        outRl.setBackgroundColor(Color.TRANSPARENT)
    }
}
