package com.yazhi1992.moon.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.yazhi1992.moon.R
import com.yazhi1992.moon.viewmodel.MemorialDayBean
import me.drakeet.multitype.MultiTypeAdapter

class MemorialDayActivity : AppCompatActivity() {

    lateinit var mAdapter: MultiTypeAdapter
    var mDatas = ArrayList<MemorialDayBean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_memorial_day)
//
//        ry_memorial_day.layoutManager = LinearLayoutManager(this)
//        mAdapter = MultiTypeAdapter()
//        mAdapter.register(MemorialDayBean::class.java, MemorialDayViewBinder())
//        ry_memorial_day.adapter = mAdapter
//
//        mAdapter.items = mDatas
//        mAdapter.notifyDataSetChanged()
//
//        btn_add.setOnClickListener {
//            ActivityRouter.gotoAddMemorial(this)
//        }
    }
}
