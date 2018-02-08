package com.yazhi1992.moon;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * Created by zengyazhi on 2018/1/23.
 */

public class ActivityRouter {
    //主页
    public static final String HOME_PAGE = "/app/home_page";
    //添加纪念日
    public static final String ADD_MEMORIAL = "/app/add_memorial";
    //纪念日列表
    public static final String MEMORIAL_LIST = "/app/memorial_list";
    //纪念日详情
    public static final String MEMORIAL_DAY_DETAIL = "/app/memorial_day_detail";
    //登录
    public static final String LOGIN = "/app/login";
    //绑定另一半
    public static final String BIND_LOVER = "/app/bind_lover";
    //关于我们
    public static final String ABOUT_US = "/app/about_us";
    //添加愿望
    public static final String ADD_HOPE = "/app/add_hope";
    //愿望列表
    public static final String HOPE_LIST = "/app/hope_list";
    //添加文本
    public static final String ADD_TEXT = "/app/add_text";

    public static class KeyName {
        public static final String OBJECT_ID_KEY = "objectid";
        public static final String TITLE_KEY = "title";
        public static final String TIME_KEY = "time";

    }

    public static void gotoAddMemorial() {
        ARouter.getInstance()
                .build(ADD_MEMORIAL)
                .navigation();
    }

    public static void gotoEditMemorial() {
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

    public static void gotoAddHope() {
        ARouter.getInstance()
                .build(ADD_HOPE)
                .navigation();
    }

    public static void gotoHopeList() {
        ARouter.getInstance()
                .build(HOPE_LIST)
                .navigation();
    }

    public static void gotoAddText() {
        ARouter.getInstance()
                .build(ADD_TEXT)
                .navigation();
    }

    public static void gotoMemorialDayDetail(String title, long time) {
        ARouter.getInstance()
                .build(MEMORIAL_DAY_DETAIL)
                .withString(KeyName.TITLE_KEY, title)
                .withLong(KeyName.TIME_KEY, time)
                .navigation();
    }

}
