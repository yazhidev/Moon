package com.yazhi1992.moon.widget.calendarview;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.ui.mc.McDataFromApi;
import com.yazhi1992.moon.ui.mc.McDetailPresenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zengyazhi on 2018/4/5.
 */

public class CalendarInfoCache {

    //缓存数据
    private List<McDataFromApi> mMcDataFromApis;

    private Map<String, List<DateBean>> mNoInfoDataMaps = new HashMap<>(); //缓存每月数据，包含点击状态，但不包含mc信息

    private McDetailPresenter mPresenter = new McDetailPresenter();

    private CalendarInfoCache() {
    }

    private static class CalendarInfoCacheHolder {
        private static CalendarInfoCache INSTANCE = new CalendarInfoCache();
    }

    public static CalendarInfoCache getInstance() {
        return CalendarInfoCacheHolder.INSTANCE;
    }

    public List<McDataFromApi> getMcDataFromApis() {
        return mMcDataFromApis;
    }

    public synchronized void addSingleData(McDataFromApi addData, DataCallback<Boolean> callback) {
        getCalendarInfo(new DataCallback<List<McDataFromApi>>() {
            @Override
            public void onSuccess(List<McDataFromApi> data) {
                for (int i = 0; i < data.size(); i++) {
                    McDataFromApi mcDataFromApi = data.get(i);
                    int year = mcDataFromApi.getYear();
                    int month = mcDataFromApi.getMonth();
                    int day = mcDataFromApi.getDay();
                    if (addData.getTime() < CalendarUtil.getTime(year, month, day)) {
                        if(i > 0) {
                            McDataFromApi lastData = data.get(i - 1);
                            if(lastData.getTime() == addData.getTime()) {
                                //已有，不用添加
                                callback.onSuccess(true);
                                break;
                            } else {
                                data.add(i, addData);
                                callback.onSuccess(true);
                            }
                        } else {
                            data.add(0, addData);
                            callback.onSuccess(true);
                            break;
                        }
                    }
                }
                data.add(addData);
                callback.onSuccess(true);
            }

            @Override
            public void onFailed(int code, String msg) {

            }
        });
    }

    // TODO: 2018/4/8 ConcurrentModificationException
    public synchronized void removeSingleData(int year, int month, int day, DataCallback<Boolean> callback) {
        getCalendarInfo(new DataCallback<List<McDataFromApi>>() {
            @Override
            public void onSuccess(List<McDataFromApi> data) {
                for (McDataFromApi dataFromApi : data) {
                    if (dataFromApi.getYear() == year
                            && dataFromApi.getMonth() == month
                            && dataFromApi.getDay() == day) {
                        data.remove(dataFromApi);
                        callback.onSuccess(true);
                        break;
                    }
                    if(isAlreadyDone(dataFromApi, year, month)) {
                        callback.onSuccess(true);
                        break;
                    }
                }
            }

            @Override
            public void onFailed(int code, String msg) {

            }
        });
    }

    public List<DateBean> getDataBeanList(int year, int month) {
        String key = year + "" + month;
        if(mNoInfoDataMaps.containsKey(key)) {
            //遍历列表，移除mc状态
            List<DateBean> dateBeans = mNoInfoDataMaps.get(key);
            for(DateBean dateBean : dateBeans) {
                dateBean.setMcType(TypeConstant.MC_NORMAL);
            }
            return dateBeans;
        } else {
            List<DateBean> noInfoDatas = getNoInfoDatas(year, month);
            mNoInfoDataMaps.put(key, noInfoDatas);
            return noInfoDatas;
        }
    }

    public void getCalendarInfo(DataCallback<List<McDataFromApi>> callback) {
        if (mMcDataFromApis == null) {
            //获取数据
            Api.getInstance().getAllMcRecord(new DataCallback<List<McDataFromApi>>() {
                @Override
                public void onSuccess(List<McDataFromApi> data) {
                    mMcDataFromApis = data;
                    callback.onSuccess(mMcDataFromApis);
                }

                @Override
                public void onFailed(int code, String msg) {
                    callback.onFailed(code, msg);
                }
            });
        } else {
            callback.onSuccess(mMcDataFromApis);
        }
    }

    /**
     * 获取对应月份的mc数据
     *
     * @param year
     * @param month
     * @param callback
     */
    public void getData(int year, int month, DataCallback<List<McDataFromApi>> callback) {
        getCalendarInfo(new DataCallback<List<McDataFromApi>>() {
            @Override
            public void onSuccess(List<McDataFromApi> data) {
                getDataList(year, month, callback);
            }

            @Override
            public void onFailed(int code, String msg) {
                callback.onFailed(code, msg);
            }
        });
    }

    private void getDataList(int year, int month, DataCallback<List<McDataFromApi>> callback) {
        List<McDataFromApi> list = new ArrayList<>();
        if (mMcDataFromApis != null) {
            for (int i = 0; i < mMcDataFromApis.size(); i++) {
                McDataFromApi mcDataFromApi = mMcDataFromApis.get(i);
                int dataAction = mcDataFromApi.getAction();
                int dataYear = mcDataFromApi.getYear();
                int dataMonth = mcDataFromApi.getMonth();
                if (dataYear == year && month == dataMonth) {
                    if (list.isEmpty()) {
                        //本月第一个数据，如果是去，前一个如果是来，也加入到list中
                        if (dataAction == TypeConstant.MC_GO
                                && i > 0
                                && mMcDataFromApis.get(i - 1).getAction() == TypeConstant.MC_COME) {
                            list.add(mMcDataFromApis.get(i - 1));
                        }
                    }
                    list.add(mcDataFromApi);
                    continue;
                }
                if (!list.isEmpty()) {
                    McDataFromApi lastData = list.get(list.size() - 1);
                    //本月最后一个数据，如果是来，下一个如果是去，也加入到list中
                    if (lastData.getAction() == TypeConstant.MC_COME
                            && mcDataFromApi.getAction() == TypeConstant.MC_GO) {
                        list.add(mcDataFromApi);
                        break;
                    }
                }
                if (isAlreadyDone(mcDataFromApi, year, month)) {
                    break;
                }
            }
        }
        callback.onSuccess(list);
    }

    //是否已经遍历完当月所有数据
    private boolean isAlreadyDone(McDataFromApi mcDataFromApi, int year, int month) {
        long nextMonthTime;
        if (month == 12) {
            nextMonthTime = CalendarUtil.getTime(year + 1, 1, 1);
        } else {
            nextMonthTime = CalendarUtil.getTime(year, month + 1, 1);
        }
        if (mcDataFromApi.getTime() > nextMonthTime) {
            return true;
        } else {
            return false;
        }
    }

    public void reset() {
        mMcDataFromApis = null;
        mNoInfoDataMaps.clear();
    }

    //获取不带mc信息的日程数据
    private List<DateBean> getNoInfoDatas(int year, int month) {
        List<DateBean> datas = new ArrayList<>();
        int week = getFirstWeekOfMonth(year, month - 1);

        int lastYear;
        int lastMonth;
        if (month == 1) {
            lastMonth = 12;
            lastYear = year - 1;
        } else {
            lastMonth = month - 1;
            lastYear = year;
        }
        int lastMonthDays = getMonthDays(lastYear, lastMonth);//上个月总天数

        int currentMonthDays = getMonthDays(year, month);//当前月总天数

        int nextYear;
        int nextMonth;
        if (month == 12) {
            nextMonth = 1;
            nextYear = year + 1;
        } else {
            nextMonth = month + 1;
            nextYear = year;
        }

        int index = 0;//周一开始，1周日开始

        for (int i = 0; i < week; i++) {
            datas.add(initDateBean(lastYear, lastMonth, lastMonthDays - week + 1 + i, TypeConstant.CALENDAR_LAST_MONTH));
        }

        for (int i = 0; i < currentMonthDays; i++) {
            datas.add(initDateBean(year, month, i + 1, TypeConstant.CALENDAR_THIS_MONTH));
        }

//        for (int i = 0; i < 7 * getMonthRows(year, month) - currentMonthDays - week; i++) {
//            datas.add(initDateBean(nextYear, nextMonth, i + 1, TypeConstant.CALENDAR_NEXT_MONTH));
//        }
        for (int i = 0; i < 7 * 6 - currentMonthDays - week; i++) {
            datas.add(initDateBean(nextYear, nextMonth, i + 1, TypeConstant.CALENDAR_NEXT_MONTH));
        }

        return datas;
    }

    /**
     * 计算当月1号是周几
     *
     * @param year
     * @param month
     * @return
     */
    public static int getFirstWeekOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
//        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        if (i == 1) {
            return 6;
        } else {
            return i - 2;
        }
    }

    private DateBean initDateBean(int year, int month, int day, @TypeConstant.MONTH_TYPE int type) {
        DateBean dateBean = new DateBean();
        dateBean.setDate(year, month, day);
        dateBean.setType(type);
        return dateBean;
    }


    /**
     * 计算指定月份的天数
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDays(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1;
        }
    }

    /**
     * 计算当前月需要显示几行
     *
     * @param year
     * @param month
     * @return
     */
    public int getMonthRows(int year, int month) {
        int items = getFirstWeekOfMonth(year, month - 1) + getMonthDays(year, month);
        int rows = items % 7 == 0 ? items / 7 : (items / 7) + 1;
//        if (rows == 4) {
//            rows = 5;
//        }
        return rows;
    }
}
