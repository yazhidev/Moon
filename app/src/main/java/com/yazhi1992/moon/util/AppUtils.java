package com.yazhi1992.moon.util;

import com.yazhi1992.yazhilib.utils.LibTimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class AppUtils {

    private static String[] dayInWeekTime = {"", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    public static SimpleDateFormat memorialDayYmdFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String getTimeStrForMemorialDay(Date date) {
        return memorialDayYmdFormat.format(date) + " " + LibTimeUtils.getDayInWeek(date, dayInWeekTime);
    }
}
