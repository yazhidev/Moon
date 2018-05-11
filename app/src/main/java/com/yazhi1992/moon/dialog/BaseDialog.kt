package com.yazhi1992.moon.dialog

import android.app.DialogFragment
import android.app.FragmentManager

/**
 * Created by zengyazhi on 2018/5/11.
 */
open class BaseDialog : DialogFragment() {

    override fun onStart() {
        super.onStart()
        //设置 dialog 的背景为 null
        dialog.window!!.setBackgroundDrawable(null)
        dialog.setCanceledOnTouchOutside(true)
    }

    fun show(manager: FragmentManager) {
        manager.executePendingTransactions()
        if (!isAdded) {
            show(manager, "dialog")
        }
    }
}