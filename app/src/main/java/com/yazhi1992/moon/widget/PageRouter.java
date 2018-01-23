package com.yazhi1992.moon.widget;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * Created by zengyazhi on 2018/1/23.
 */

public class PageRouter {
    public static final String ADD_MEMORIAL = "/app/add_memorial";

    public static void gotoAddMemorial() {
        ARouter.getInstance()
                .build(ADD_MEMORIAL)
                .navigation();
    }

}
