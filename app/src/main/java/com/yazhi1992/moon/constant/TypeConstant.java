package com.yazhi1992.moon.constant;

import android.support.annotation.IntDef;

/**
 * Created by zengyazhi on 2018/2/5.
 *
 * 回忆页消息类型
 */

public class TypeConstant {

    public static final int TYPE_TEXT = 0; //文本
    public static final int TYPE_MEMORIAL_DAY = 1; //纪念日
    public static final int TYPE_HOPE = 2; //新增心愿
    public static final int TYPE_HOPE_FINISHED = 3; //完成心愿
    public static final int TYPE_MC = 4; //mc

    @IntDef({TYPE_TEXT, TYPE_MEMORIAL_DAY, TYPE_HOPE, TYPE_HOPE_FINISHED, TYPE_MC})
    public @interface DataTypeInHistory{}


    public static final int HOPE_UNFINISH = 0; //未完成愿望
    public static final int HOPE_DONE = 1; //已完成愿望

    @IntDef({HOPE_UNFINISH, HOPE_DONE})
    public @interface HopeType {}

    public static final int MEN = 1; //男
    public static final int WOMEN = 2; //女

    @IntDef({MEN, WOMEN})
    public @interface Gender {}

    public static final int MC_NORMAL = 0; //0 正常状态，1 正在来例假
    public static final int MC_LIVING = 1;

    @IntDef({MC_NORMAL, MC_LIVING})
    public @interface McStatus {}

    public static final int MC_COME = 1; //来
    public static final int MC_GO = 2; //走

    @IntDef({MC_COME, MC_GO})
    public @interface McAction {}

    public static final int MC_MIDDLE = 3;
    public static final int CLICKED = 4;
    public static final int TODAY = 5; //今天

    @IntDef({MC_COME, MC_GO, MC_MIDDLE, MC_NORMAL, CLICKED, TODAY})
    public @interface MC_VIEW_TYPE {
    }

    //0上月，1本月，2下月
    public static final int CALENDAR_LAST_MONTH = 0;
    public static final int CALENDAR_THIS_MONTH = 1;
    public static final int CALENDAR_NEXT_MONTH = 2;

    @IntDef({CALENDAR_LAST_MONTH, CALENDAR_THIS_MONTH, CALENDAR_NEXT_MONTH})
    public @interface MONTH_TYPE {
    }
}
