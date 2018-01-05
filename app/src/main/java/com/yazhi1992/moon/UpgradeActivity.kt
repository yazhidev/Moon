package com.yazhi1992.moon

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.tencent.bugly.beta.Beta

class UpgradeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade)

        val upgradeInfo = Beta.getUpgradeInfo()
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.upgrade_title))
                .setMessage(upgradeInfo.title)
                .setPositiveButton(getString(R.string.upgrade_comfirm), {dialog, which ->
                    Beta.startDownload()
                    dialog.dismiss()
                })
                .setNegativeButton(getString(R.string.upgrade_cancel)) { dialog, which -> dialog.dismiss()}.show()
    }
}
