package com.yazhi1992.moon.constant;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class NameContant {

    //纪念日
    public static class MemorialDay {
        public static final String CLAZZ_NAME = "memorial_day";
        public static final String TITLE = "title";
        public static final String TIME = "time";
    }

    //首页列表
    public static class LoveHistory {
        public static final int TYPE_NORMAL_TEXT = 0;
        public static final int TYPE_MEMORIAL_DAY = 1;

        public static final String CLAZZ_NAME = "love_history";
        public static final String MEMORIAL_DAY = "memorial_day";
        public static final String TYPE = "type";
        public static final String ID = "id";
    }

    public static class AVUserClass {
        public static final String HEAD_URL = "head_img_url";
        public static final String INVITE_NUMBER = "invite_number";
        public static final String LOVER = "lover";
    }

    public static class BindLover {
        public static final String CLAZZ_NAME = "bind_lover";
        public static final String INVITE_NUMBER = "invite_number";
        public static final String USER_ID = "user_id";

    }
}
