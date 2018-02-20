package com.yazhi1992.moon.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.event.BuglyUpgrate;
import com.yazhi1992.moon.ui.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by zengyazhi on 2018/1/25.
 */

public class AbsUpgrateActivity extends BaseActivity {

    @Override
    protected void initStatusBar() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    //版本更新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void upgrade(BuglyUpgrate event) {
        UpgradeInfo upgradeInfo = Beta.getUpgradeInfo();
        new AlertDialog.Builder(this)
                .setTitle(upgradeInfo.title)
                .setMessage(upgradeInfo.newFeature)
                .setPositiveButton(getString(R.string.upgrade_comfirm), (dialog, which) -> {
                    Beta.startDownload();
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.upgrade_cancel), (dialog, which) -> {
                    dialog.dismiss();
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
