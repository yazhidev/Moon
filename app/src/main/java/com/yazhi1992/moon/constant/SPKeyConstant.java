package com.yazhi1992.moon.constant;

/**
 * Created by zengyazhi on 2018/2/16.
 */

public class SPKeyConstant {
    public static final String MC_ENABLE = "mc_enable";
    public static final String TIP_BAD_MOOD_TIME = "tip_bad_mood_time"; //提示最近可能情绪不好的时间，一天只记录一次
    public static final String TIP_BAD_MOOD_ENABLE = "tip_bad_mood_enable"; //是否需要提示最近可能情绪不好
    public static final String LOGIN_ACCOUNT = "login_account"; //账号
    public static final String LOGIN_PWD = "login_pwd"; //密码
    public static final String LOGIN_REMEMBER_PWD_ENABLE = "LOGIN_REMEMBER_PWD_ENABLE"; //是否记住账号

    //云参数
    public static final String PUSH_IMG_ENABLE = "push_img_enable"; //说说是否可以传图
    public static final String NOTIFY_DINGDING = TableConstant.CONFIGURATION.NOTIFY_DINGDING; //娜娜发动态时通知我
    public static final String NOTIFY_DD = TableConstant.CONFIGURATION.NOTIFY_DD; //我发动态时通知钉钉
    public static final String MC_GO_MIN_DAY = TableConstant.CONFIGURATION.MC_GO_MIN_DAY; //mc 没来开始预警天数
    public static final String MC_GO_MAX_DAY = TableConstant.CONFIGURATION.MC_GO_MAX_DAY; //mc 没来结束预警天数
    public static final String MC_COME_MAX_DAY = TableConstant.CONFIGURATION.MC_COME_MAX_DAY; //mc 来了预警天数
}
