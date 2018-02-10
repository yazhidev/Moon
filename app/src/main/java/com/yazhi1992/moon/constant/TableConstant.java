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
        public static final String INVITE_NUMBER = "invite_number";
        public static final String HAVE_LOVER = "have_lover";
        public static final String LOVER_ID = "lover_id";
        public static final String LOVER_NAME = "lover_name";
        public static final String LOVER_HEAD_URL = "lover_head_url";
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
        public static final String HAVE_COMMENT = "have_comment";
        public static final String COMMENT_LIST = "comment_list";
    }

    //绑定表
    public static class BindLover {
        public static final String CLAZZ_NAME = "bind_lover";
        public static final String INVITE_NUMBER = "invite_number";
        public static final String USER_ID = "user_id";
        public static final String LOVER_ID = "lover_id";
        public static final String USER_NAME = "user_name";
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
    }
}
