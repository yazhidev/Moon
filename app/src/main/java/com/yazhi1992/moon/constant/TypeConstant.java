package com.yazhi1992.moon.constant;

import android.support.annotation.IntDef;

/**
 * Created by zengyazhi on 2018/2/5.
 *
 * 回忆页消息类型
 */

public class TypeConstant {

    public static final int TYPE_NORMAL_TEXT = 0; //文本
    public static final int TYPE_MEMORIAL_DAY = 1; //纪念日
    public static final int TYPE_HOPE = 2; //心愿

    @IntDef({TYPE_NORMAL_TEXT, TYPE_MEMORIAL_DAY, TYPE_HOPE})
    public @interface DataTypeInHistory{}
}
