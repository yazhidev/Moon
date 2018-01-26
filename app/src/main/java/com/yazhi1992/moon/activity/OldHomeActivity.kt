package com.yazhi1992.moon.activity

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.SaveCallback
import com.tencent.bugly.beta.Beta
import com.yazhi1992.moon.ActivityRouter
import com.yazhi1992.moon.R
import com.yazhi1992.moon.event.BuglyUpgrate
import com.yazhi1992.yazhilib.utils.StatusBarUtils
import kotlinx.android.synthetic.main.activity_home_old.*
import kotlinx.android.synthetic.main.app_bar_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import permissions.dispatcher.RuntimePermissions


@RuntimePermissions
class OldHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_old)
        EventBus.getDefault().register(this@OldHomeActivity)

        StatusBarUtils.with(this)
                .setDrawerLayoutContentId(true, R.id.content)
                .setColor(resources.getColor(R.color.colorAccent))
                .init()

        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
//            startActivity(Intent(this@OldHomeActivity, BindLoverActivity::class.java)
//                    , ActivityOptions.makeSceneTransitionAnimation(this@OldHomeActivity).toBundle())
//            ActivityRouter.gotoMain3(this@OldHomeActivity)

            // 测试 SDK 是否正常工作的代码
            val testObject = AVObject("TestObject")
            testObject.put("words", "Hello World!")
            testObject.saveInBackground(object : SaveCallback() {
                override fun done(e: AVException?) {
                    if (e == null) {
                        Log.d("saved", "success!")
                    }
                }
            })
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

    }

    //版本更新
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun upgrade(event: BuglyUpgrate) {
        val upgradeInfo = Beta.getUpgradeInfo()
        AlertDialog.Builder(this)
                .setTitle(upgradeInfo.title)
                .setMessage(upgradeInfo.newFeature)
                .setPositiveButton(getString(R.string.upgrade_comfirm), { dialog, which ->
                    Beta.startDownload()
                    dialog.dismiss()
                })
                .setNegativeButton(getString(R.string.upgrade_cancel)) { dialog, which -> dialog.dismiss()}.show()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this@OldHomeActivity)
    }

    override fun onStop() {
        super.onStop()
        outRl.visibility = View.GONE
        outRl.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                ActivityRouter.gotoMemorialDay(this@OldHomeActivity)
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
