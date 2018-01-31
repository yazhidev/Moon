package com.yazhi1992.moon;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * Created by zengyazhi on 2018/1/23.
 */

public class PageRouter {
    //主页
    public static final String HOME_PAGE = "/app/home_page";
    //添加纪念日
    public static final String ADD_MEMORIAL = "/app/add_memorial";
    //纪念日列表
    public static final String MEMORIAL_LIST = "/app/memorial_list";
    //登录
    public static final String LOGIN = "/app/login";
    //绑定另一半
    public static final String BIND_LOVER = "/app/bind_lover";
    //关于我们
    public static final String ABOUT_US = "/app/about_us";

    public static void gotoAddMemorial() {
        ARouter.getInstance()
                .build(ADD_MEMORIAL)
                .navigation();
    }

    public static void gotoHomePage() {
        ARouter.getInstance()
                .build(HOME_PAGE)
                .navigation();
    }

    public static void gotoLogin() {
        ARouter.getInstance()
                .build(LOGIN)
                .navigation();
    }

    public static void gotoBindLover() {
        ARouter.getInstance()
                .build(BIND_LOVER)
                .navigation();
    }

    public static void gotoMemorialList() {
        ARouter.getInstance()
                .build(MEMORIAL_LIST)
                .navigation();
    }

    public static void gotoAboutUs() {
        ARouter.getInstance()
                .build(ABOUT_US)
                .navigation();
    }

}