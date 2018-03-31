package com.yazhi1992.moon;

import android.content.Intent;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.yazhi1992.moon.constant.SPKeyConstant;

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
    //愿望列表
    public static final String HOPE_TAB_LIST = "/app/hope_tab_list";
    //愿望详情页
    public static final String HOPE_DETAIL = "/app/hope_detail";
    //文本详情页
    public static final String TEXT_DETAIL = "/app/text_detail";
    //小姨妈设置页面
    public static final String MC_DETAIL = "/app/mc_detail";
    public static final String NEW_MC_DETAIL = "/app/new_mc_detail";
    //性别设置页面
    public static final String SET_GENDER = "/app/set_gender";
    //配置页面
    public static final String CONFIGURATION = "/app/configuration";
    //预览图片
    public static final String IMG_PREVIEW = "/app/img_preview";
    //设置昵称
    public static final String SET_USER_NAME = "/app/set_user_name";
    //个人中心
    public static final String USER_CENTER = "/app/user_center";
    //账号密码登录页
    public static final String NEW_LOGIN = "/app/new_login";
    //注册页
    public static final String REGISTER_STEP_ONE = "/app/register_step_one";
    //设置邮箱
    public static final String SET_EMAIL = "/app/set_email";
    //找回密码
    public static final String FORGET_PWD = "/app/forget_pwd";

    public static class KeyName {
        public static final String OBJECT_ID_KEY = "objectid";
        public static final String TITLE_KEY = "title";
        public static final String TIME_KEY = "time";
        public static final String EDIT_MODE = "edit_mode";
        public static final String CLEAR_TASK = "clear_task";
        public static final String USER_NAME = "user_name";
        public static final String IMG_URL = "img_url";
        public static final String EMAIL = "email";
    }

    public static void gotoAddMemorial(boolean isAdd) {
        ARouter.getInstance()
                .build(ADD_MEMORIAL)
                .withBoolean(KeyName.EDIT_MODE, !isAdd)
                .navigation();
    }

    public static void gotoHomePage() {
        ARouter.getInstance()
                .build(HOME_PAGE)
                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                .navigation();
    }

    public static void gotoNewLogin() {
        ARouter.getInstance()
                .build(NEW_LOGIN)
                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                .navigation();
    }

    public static void gotoLogin() {
        ARouter.getInstance()
                .build(LOGIN)
                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                .navigation();
    }

    public static void gotoSetEmail(boolean clearTask) {
        Postcard build = ARouter.getInstance()
                .build(SET_EMAIL);
        if (clearTask) {
            build.withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        build.navigation();
    }

    public static void gotoSetGender(boolean clearTask) {
        Postcard build = ARouter.getInstance()
                .build(SET_GENDER);
        if (clearTask) {
            build.withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        build.withBoolean(KeyName.CLEAR_TASK, clearTask);
        build.navigation();
    }

    public static void gotoSetUserName(String userName) {
        ARouter.getInstance()
                .build(SET_USER_NAME)
                .withString(KeyName.USER_NAME, userName)
                .navigation();
    }

    public static void gotoBindLover() {
        ARouter.getInstance()
                .build(BIND_LOVER)
                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
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

    public static void gotoAddHope(boolean isAdd) {
        ARouter.getInstance()
                .build(ADD_HOPE)
                .withBoolean(KeyName.EDIT_MODE, !isAdd)
                .navigation();
    }

    public static void gotoHopeList() {
        ARouter.getInstance()
                .build(HOPE_TAB_LIST)
                .navigation();
    }

    public static void gotoAddText(boolean isAdd) {
        ARouter.getInstance()
                .build(ADD_TEXT)
                .withBoolean(KeyName.EDIT_MODE, !isAdd)
                .navigation();
    }

    public static void gotoMemorialDayDetail() {
        ARouter.getInstance()
                .build(MEMORIAL_DAY_DETAIL)
                .navigation();
    }

    public static void gotoHopeDetail() {
        ARouter.getInstance()
                .build(HOPE_DETAIL)
                .navigation();
    }

    public static void gotoTextDetail() {
        ARouter.getInstance()
                .build(TEXT_DETAIL)
                .navigation();
    }

    public static void gotoMcDetail() {
        ARouter.getInstance()
                .build(NEW_MC_DETAIL)
                .navigation();
    }

    public static void gotoConfiguration() {
        ARouter.getInstance()
                .build(CONFIGURATION)
                .navigation();
    }

    public static void gotoImgPreview(String url) {
        ARouter.getInstance()
                .build(IMG_PREVIEW)
                .withString(KeyName.IMG_URL, url)
                .navigation();
    }

    public static void gotoUserCenter() {
        ARouter.getInstance()
                .build(USER_CENTER)
                .navigation();
    }

    public static void gotRegister1() {
        ARouter.getInstance()
                .build(REGISTER_STEP_ONE)
                .navigation();
    }

    public static void gotoFindPwd(String email) {
        ARouter.getInstance()
                .build(FORGET_PWD)
                .withString(KeyName.EMAIL, email)
                .navigation();
    }

}
