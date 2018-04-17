package com.yazhi1992.moon.widget.calendarview;

import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.ui.mc.McDataFromApi;
import com.yazhi1992.moon.ui.mc.McDetailPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyazhi on 2018/4/5.
 */

public class CalendarInfoCache {

    //缓存数据
    private Map<Integer, Map<Integer, List<McDataFromApi>>> mYearMaps = new HashMap<>();

    private McDetailPresenter mPresenter = new McDetailPresenter();

    private CalendarInfoCache() {
    }

    private static class CalendarInfoCacheHolder {
        private static CalendarInfoCache INSTANCE = new CalendarInfoCache();
    }

    public static CalendarInfoCache getInstance() {
        return CalendarInfoCacheHolder.INSTANCE;
    }

    /**
     * 从网络获取数据后，添加到缓存中
     *
     * @param year
     * @param month
     * @param datas
     */
    public void addData(int year, int month, List<McDataFromApi> datas) {
        if (mYearMaps.containsKey(month)) {
            mYearMaps.get(year).put(month, datas);
        } else {
            Map<Integer, List<McDataFromApi>> monthMaps = new HashMap<>();
            monthMaps.put(month, datas);
            mYearMaps.put(year, monthMaps);
        }
    }

    public void addSingleData(McDataFromApi data) {
        int year = data.getYear();
        int month = data.getMonth();
        if (mYearMaps.containsKey(year) && mYearMaps.get(year).containsKey(month)) {
            List<McDataFromApi> mcDataFromApis = mYearMaps.get(year).get(month);
            mcDataFromApis.add(data);
            //列表按照时间排序

        } else {
            List<McDataFromApi> datas = new ArrayList<>();
            datas.add(data);
            addData(year, month, datas);
        }
    }

    // TODO: 2018/4/8 ConcurrentModificationException
    public synchronized void removeSingleData(int year, int month, int day) {
        if (mYearMaps.containsKey(year) && mYearMaps.get(year).containsKey(month)) {
            List<McDataFromApi> mcDataFromApis = mYearMaps.get(year).get(month);
            for(McDataFromApi api : mcDataFromApis) {
                if(api.getDay() == day) mcDataFromApis.remove(api);
            }
        }
    }

    // TODO: 2018/4/5 参考设计模式书，图片框架
    // TODO: 2018/4/6 缓存到本地

    /**
     * 如果缓存中没有则从网络获取
     *
     * @param year
     * @param month
     * @param callback
     */
    public void getData(int year, int month, DataCallback<List<McDataFromApi>> callback) {
        if (mYearMaps.containsKey(year) && mYearMaps.get(year).containsKey(month)) {
            //从缓冲中获取
            callback.onSuccess(mYearMaps.get(year).get(month));
        } else {
            //从网络获取
            mPresenter.getMonthMcRecord(year, month, new DataCallback<List<McDataFromApi>>() {
                @Override
                public void onSuccess(List<McDataFromApi> data) {
                    addData(year, month, data);
                    callback.onSuccess(data);
                }

                @Override
                public void onFailed(int code, String msg) {
                    callback.onFailed(code, msg);
                }
            });
        }
    }
}
