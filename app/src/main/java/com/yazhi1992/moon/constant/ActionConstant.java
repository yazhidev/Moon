package com.yazhi1992.moon.constant;

import android.support.annotation.StringDef;

/**
 * Created by zengyazhi on 2018/2/5.
 */

public class ActionConstant {
    public static final String ADD_TEXT = "add_text";
    public static final String ADD_MEMORIAL = "add_memorial";
    public static final String ADD_HOPE = "add_hope";
    public static final String UPDATE_MC = "update_mc";

    @StringDef({ActionConstant.ADD_MEMORIAL, ADD_HOPE, ADD_TEXT, UPDATE_MC})
    public @interface AddAction {}

    public static class Notification {
        public static final String ACTION_KEY = "action";
        //点击通知跳转回忆页
        public static final String ACTION_VALUE_HISTORY = "history";

    }
}
