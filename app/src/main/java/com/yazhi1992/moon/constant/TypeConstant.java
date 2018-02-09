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
    public static final int TYPE_HOPE = 2; //心愿

    @IntDef({TYPE_TEXT, TYPE_MEMORIAL_DAY, TYPE_HOPE})
    public @interface DataTypeInHistory{}


    public static final int HOPE_UNFINISH = 0; //未完成愿望
    public static final int HOPE_DONE = 1; //已完成愿望

    @IntDef({HOPE_UNFINISH, HOPE_DONE})
    public @interface HopeType {}
}
