package com.yazhi1992.moon

import android.app.Application
import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.avos.avoscloud.AVOSCloud
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.beta.upgrade.UpgradeListener
import com.yazhi1992.moon.event.BuglyUpgrate
import org.greenrobot.eventbus.EventBus

/**
 * Created by zengyazhi on 2017/12/27.
 */
class BaseApplication : Application() {

    companion object {
        lateinit var instance: BaseApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();    // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this)

        //logger
        Logger.addLogAdapter(AndroidLogAdapter())

        //bugly
        Beta.initDelay = 1 * 1000
        Beta.upgradeListener = UpgradeListener { ret, strategy, isManual, isSilence ->
            if (strategy != null) {
                EventBus.getDefault().post(BuglyUpgrate())
            }
        }
        Bugly.init(applicationContext, "5d768fb313", BuildConfig.DEBUG)

        //leancloud
        AVOSCloud.initialize(this, BuildConfig.LEAN_CLOUD_ID, BuildConfig.LEAN_CLOUD_KEY)
        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        AVOSCloud.setDebugLogEnabled(BuildConfig.DEBUG)
    }

    fun getContext() : Context{
        return applicationContext
    }
}