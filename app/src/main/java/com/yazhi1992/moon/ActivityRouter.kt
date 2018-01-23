package com.yazhi1992.moon

import android.content.Context
import android.support.v4.app.ActivityOptionsCompat
import com.alibaba.android.arouter.launcher.ARouter


/**
 * Created by zengyazhi on 2017/12/27.
 */

class ActivityRouter {

    companion object {
        const val MAIN = "/app/main"
        const val MAIN2 = "/app/main2"
        const val MAIN3 = "/app/main3"
        //纪念日
        const val MEMORIAL_DAY = "/app/memorial"
        const val ADD_MEMORIAL = "/app/add_memorial"

        fun gotoMain2(context: Context) {
            val compat = ActivityOptionsCompat.makeCustomAnimation(context, R.anim.empty, R.anim.empty)
            ARouter.getInstance()
                    .build(MAIN2)
                    .withOptionsCompat(compat)
                    .navigation()
        }

        fun gotoMain3(context: Context) {
            val compat = ActivityOptionsCompat.makeCustomAnimation(context, R.anim.empty, R.anim.empty)
            ARouter.getInstance()
                    .build(MAIN3)
                    .withOptionsCompat(compat)
                    .navigation()
        }

        fun gotoMemorialDay(context: Context) {
            ARouter.getInstance()
                    .build(MEMORIAL_DAY)
                    .navigation()
        }

        fun gotoAddMemorial(context: Context) {
            ARouter.getInstance()
                    .build(ADD_MEMORIAL)
                    .navigation()
        }
    }
}
