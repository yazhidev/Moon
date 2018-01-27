package com.yazhi1992.moon.util;


import com.orhanobut.logger.Logger;

/**
 * Created by zengyazhi on 2018/1/27.
 */

public class MyLogger {

    public static void log(Object o) {
        Logger.t("zyz").d(o);
    }
}
