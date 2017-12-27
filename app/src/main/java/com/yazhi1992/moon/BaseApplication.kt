package com.yazhi1992.moon

import android.app.Application
import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter

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
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this)
    }



    fun getContext() : Context{
        return applicationContext
    }
}