package com.yazhi1992.moon.util;

import com.google.gson.Gson;
import com.yazhi1992.moon.BaseApplication;
import com.yazhi1992.moon.R;
import com.yazhi1992.yazhilib.utils.LibTimeUtils;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class AppUtils {

    private static String[] dayInWeekTime = {""
            , BaseApplication.getInstance().getString(R.string.week_mon)
            , BaseApplication.getInstance().getString(R.string.week_tue)
            , BaseApplication.getInstance().getString(R.string.week_wed)
            , BaseApplication.getInstance().getString(R.string.week_thu)
            , BaseApplication.getInstance().getString(R.string.week_fri)
            , BaseApplication.getInstance().getString(R.string.week_sat)
            , BaseApplication.getInstance().getString(R.string.week_sun)};

    public static SimpleDateFormat memorialDayYmdFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat historyYmdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public static String getTimeStrForMemorialDay(Date date) {
        return memorialDayYmdFormat.format(date) + " " + LibTimeUtils.getDayInWeek(date, dayInWeekTime);
    }

    public static String getTimeForHistory(Date time) {
        return historyYmdFormat.format(time);
    }

    private static Gson gson = new Gson();

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    /**
     * josn转换实体类
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T parseJSON(String json, Class<T> clazz) {
        if (gson == null) {
            gson = new Gson();
        }
        return gson.fromJson(json, clazz);
    }


    /**
     * Type type = new
     * TypeToken&lt;ArrayList&lt;TypeInfo>>(){}.getType();
     * <br>Type所在的包：java.lang.reflect
     * <br>TypeToken所在的包：com.google.gson.reflect.TypeToken
     *
     * @param type
     * @return
     */
    public static <T> T parseJSONArray(String jsonArr, Type type) {
        if (gson == null) {
            gson = new Gson();
        }
        return gson.fromJson(jsonArr, type);
    }

}
