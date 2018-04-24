package com.yazhi1992.moon.widget.calendarview;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.TypeConstant;
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
    private List<McDataFromApi> mMcDataFromApis;

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

    public synchronized void addSingleData(McDataFromApi addData) {
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
                            if(lastData.getTime() == addData.getTime()) break;
                            data.add(i, addData);
                        } else {
                            data.add(0, addData);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailed(int code, String msg) {

            }
        });
    }

    // TODO: 2018/4/8 ConcurrentModificationException
    public synchronized void removeSingleData(int year, int month, int day) {
        getCalendarInfo(new DataCallback<List<McDataFromApi>>() {
            @Override
            public void onSuccess(List<McDataFromApi> data) {
                for (McDataFromApi dataFromApi : data) {
                    if (dataFromApi.getYear() == year
                            && dataFromApi.getMonth() == month
                            && dataFromApi.getDay() == day) {
                        data.remove(dataFromApi);
                        break;
                    }
                    if(isAlreadyDone(dataFromApi, year, month)) break;
                }
            }

            @Override
            public void onFailed(int code, String msg) {

            }
        });
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
    }
}
