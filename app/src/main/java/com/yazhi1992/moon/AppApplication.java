package com.yazhi1992.moon;

import android.support.multidex.MultiDexApplication;

import com.alibaba.android.arouter.launcher.ARouter;
import com.avos.avoscloud.AVOSCloud;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.upgrade.UpgradeListener;
import com.yazhi1992.moon.event.BuglyUpgrate;
import com.yazhi1992.yazhilib.utils.LibSPUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class AppApplication extends MultiDexApplication {

    public static AppApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        LibSPUtils.init(this);

        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();    // 打印日志
            ARouter.openDebug();  // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this);

        //logger
        Logger.addLogAdapter(new AndroidLogAdapter());

        //bugly
        Beta.initDelay = 1 * 1000;
        Beta.upgradeListener = new UpgradeListener() {
            @Override
            public void onUpgrade(int i, UpgradeInfo upgradeInfo, boolean b, boolean b1) {
                if (upgradeInfo != null) {
                    EventBus.getDefault().post(new BuglyUpgrate());
                }
            }
        };
        Bugly.init(getApplicationContext(), "5d768fb313", BuildConfig.DEBUG);

        //leancloud
        AVOSCloud.initialize(this, BuildConfig.LEAN_CLOUD_ID, BuildConfig.LEAN_CLOUD_KEY);
        // 放在 SDK 初始化语句 AVOSCloud.initialize() 后面，只需要调用一次即可
        AVOSCloud.setDebugLogEnabled(BuildConfig.DEBUG);
    }

    public static AppApplication getInstance() {
        return context;
    }

}
