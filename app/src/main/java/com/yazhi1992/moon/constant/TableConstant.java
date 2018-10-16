package com.yazhi1992.moon.constant;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class TableConstant {

    public static class Common {
        public static final String CREATE_TIME = "createdAt";
        public static final String UPDATE_TIME = "updatedAt";
        public static final String ID = "id";
        public static final String OBJECT_ID = "objectId";
    }

    //用户表
    public static class AVUserClass {
        public static final String CLAZZ_NAME = "_User";
        public static final String HEAD_URL = "head_img_url";
        public static final String USER_NAME = "username";
        public static final String NICK_NAME = "nickname";
        public static final String INVITE_NUMBER = "invite_number";
        public static final String HAVE_LOVER = "have_lover";
        public static final String LOVER_ID = "lover_id";
        public static final String GENDER = "gender"; //0 未设置，1男性，2女性
        public static final String HEAD_IMG_FILE = "head_img_file";
        public static final String DEFAULT_MAN_HEAD = "default_man_head";
        public static final String DEFAULT_WOMAN_HEAD = "default_woman_head";
        public static final String EMAIL_VERIFIED = "emailVerified";
        public static final String PUSH_TOKEN = "pushToken";
    }

    //纪念日表
    public static class MemorialDay {
        public static final String CLAZZ_NAME = "memorial_day";
        public static final String TITLE = "title";
        public static final String TIME = "time";
        public static final String USER_ID = "user_id";
        public static final String LOOP = "loop"; //0 不循环，1 年循环
    }

    //首页列表
    public static class LoveHistory {
        public static final String CLAZZ_NAME = "love_history";
        public static final String MEMORIAL_DAY = "memorial_day";
        public static final String TEXT = "single_text";
        public static final String TYPE = "type"; //类型，1 纪念日，2 心愿
        public static final String ID = "id";
        public static final String HOPE = "hope";
        public static final String USER = "user";
        public static final String COMMENT_LIST = "comment_list";
        public static final String MC = "menstrual_cycle";
        public static final String LOVER = "lover";
    }

    //绑定表
    public static class BindLover {
        public static final String CLAZZ_NAME = "bind_lover";
        public static final String INVITE_NUMBER = "invite_number";
        public static final String USER_NAME = "user_name";
        public static final String USER = "user";
        public static final String LOVER = "lover";
    }

    //心愿表
    public static class Hope {
        public static final String CLAZZ_NAME = "hope";
        public static final String TITLE = "title";
        public static final String STATUS = "status"; //状态，0 未完成
        public static final String LEVEL = "level";
        public static final String ID = "id";
        public static final String USER = "user";
        public static final String FINISH_WORD = "finish_word";
        public static final String LINK = "link";
    }

    //首页文本表
    public static class Text {
        public static final String CLAZZ_NAME = "single_text";
        public static final String CONTENT = "content";
        public static final String ID = "id";
        public static final String USER = "user";
        public static final String IMG_FILE = "img_file";
    }

    //首页信息
    public static class Home {
        public static final String CLAZZ_NAME = "lover_home";
        public static final String HOME_IMG = "home_img_url";
        public static final String HOME_IMG_FILE = "home_img_file";
        public static final String UPLOADER = "latest_uploader";
    }

    //首页mc表
    public static class MC {
        public static final String CLAZZ_NAME = "menstrual_cycle";
        public static final String STATUS = "status"; //0 正常状态（姨妈走了），1 例假中（姨妈来了）
        public static final String ACTION = "action"; //默认0，1来，2走
        public static final String ID = "id";
        public static final String USER = "user";
        public static final String TIME = "time";
        public static final String YEAR = "year";
        public static final String MONTH = "month";
        public static final String DAY = "day";

    }

    //配置表
    public static class CONFIGURATION {
        public static final String CLAZZ_NAME = "configuration";
        public static final String ADD_IMG_ENABLE = "add_img_enable"; //说说是否可以传图
        public static final String MC_GO_MIN_DAY = "mc_go_min_day"; //mc 没来开始预警天数
        public static final String MC_GO_MAX_DAY = "mc_go_max_day"; //mc 没来结束预警天数
        public static final String MC_COME_MAX_DAY = "mc_come_max_day"; //mc 来了预警天数
        public static final String NOTIFY_DINGDING = "notifyDingDing"; //钉钉通知
        public static final String NOTIFY_DD = "notifyDD"; //我发动态也钉钉通知
    }

    public static class TRAVEL_LIST {
        public static final String CLAZZ_NAME = "travellist";
        public static final String DES = "des";
        public static final String COMPLETE = "complete";
        public static final String USER = "user";
        public static final String TABLE = "table";
    }

    public static class TRAVEL_LIST_TABLE {
        public static final String CLAZZ_NAME = "travellisttable";
        public static final String NAME = "name";
        public static final String USER = "user";
        public static final String UPDATE = "update";
    }

    public static class ENTER_APP {
        public static final String CLAZZ_NAME = "enterApp";
    }
}
