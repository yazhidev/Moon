package com.yazhi1992.moon

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.yazhi1992.moon.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

@Route(path = ActivityRouter.MAIN)
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        btn.setOnClickListener { ActivityRouter.gotoMain2() }
    }
}
