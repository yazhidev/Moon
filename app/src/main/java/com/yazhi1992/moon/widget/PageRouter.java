package com.yazhi1992.moon.widget;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;

import com.alibaba.android.arouter.launcher.ARouter;
import com.yazhi1992.moon.R;

/**
 * Created by zengyazhi on 2018/1/23.
 */

public class PageRouter {
    //主页
    public static final String HOME_PAGE = "/app/home_page";
    //添加纪念日
    public static final String ADD_MEMORIAL = "/app/add_memorial";
    //登录
    public static final String LOGIN = "/app/login";

    public static void gotoAddMemorial() {
        ARouter.getInstance()
                .build(ADD_MEMORIAL)
                .navigation();
    }

    public static void gotoHomePage(Context context) {
        ARouter.getInstance()
                .build(HOME_PAGE)
                .navigation(context);
    }

    public static void gotoLogin() {
        ARouter.getInstance()
                .build(LOGIN)
                .navigation();
    }

}
