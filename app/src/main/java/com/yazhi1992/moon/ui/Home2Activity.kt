package com.yazhi1992.moon.ui

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.yazhi1992.moon.R
import com.yazhi1992.yazhilib.utils.LibUtils
import kotlinx.android.synthetic.main.activity_home.*

class Home2Activity : AppCompatActivity() {

    var mFragments = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_old_old)

        bottomNavigation.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId) {
                    R.id.item_main -> LibUtils.showToast(this@Home2Activity, "item_main")
                    R.id.item_history -> LibUtils.showToast(this@Home2Activity, "item_history")
                    R.id.item_more -> LibUtils.showToast(this@Home2Activity, "more")
                }
                return true
            }
        })

    }

}
