package com.yazhi1992.moon

import com.alibaba.android.arouter.launcher.ARouter

/**
 * Created by zengyazhi on 2017/12/27.
 */

class ActivityRouter {

    companion object {
        const val MAIN = "/app/main"
        const val MAIN2 = "/app/main2"

        fun gotoMain2() {
            ARouter.getInstance().build(MAIN2).navigation()
        }
    }
}
