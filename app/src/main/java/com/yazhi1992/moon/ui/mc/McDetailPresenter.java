package com.yazhi1992.moon.ui.mc;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.widget.calendarview.CalendarInfoCache;

import java.util.List;

/**
 * Created by zengyazhi on 2018/2/16.
 */

public class McDetailPresenter {

    public void getData(DataCallback<McData> callback) {
        Api.getInstance().getMcDetailInitData(callback);
    }

    public void getLastMcRecord(DataCallback<Boolean> callback) {
        Api.getInstance().getLastMcRecord(callback);
    }

    public void updateMcStatus(@TypeConstant.McStatus int status, long time, DataCallback<Boolean> callback) {
        Api.getInstance().updateMcStatus(status, time, callback);
    }

    public void addMcAction(@TypeConstant.McAction int action, int year, int month, int day, long time, DataCallback<Boolean> callback) {
        Api.getInstance().addMcAction(action, year, month, day, time, new DataCallback<McDataFromApi>() {
            @Override
            public void onSuccess(McDataFromApi data) {
                CalendarInfoCache.getInstance().addSingleData(data, new DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        //添加到缓存中
                        callback.onSuccess(true);
                    }

                    @Override
                    public void onFailed(int code, String msg) {

                    }
                });
            }

            @Override
            public void onFailed(int code, String msg) {
                callback.onFailed(code, msg);
            }
        });
    }

    public void removeMcAction(int year, int month, int day, DataCallback<Boolean> callback) {
        Api.getInstance().deleteMcAction(year, month, day, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                CalendarInfoCache.getInstance().removeSingleData(year, month, day, new DataCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean data) {
                        callback.onSuccess(true);
                    }

                    @Override
                    public void onFailed(int code, String msg) {

                    }
                });
            }

            @Override
            public void onFailed(int code, String msg) {
                callback.onFailed(code, msg);
            }
        });
    }

    public void getMcRecord(int lastTime, int size, DataCallback<List<McDataFromApi>> callback) {
        Api.getInstance().getMcRecord(lastTime, size, callback);
    }

    public void getMonthMcRecord(int year, int month, DataCallback<List<McDataFromApi>> callback) {
        Api.getInstance().getMonthMcRecord(year, month, callback);
    }

    /**
     * 获取获取情侣中女性最近一条mc记录
     *
     * @param callback 如果没有记录返回null
     */
    public void getLatestMcRecord(DataCallback<McDataFromApi> callback) {
        Api.getInstance().getLatestMcRecord(callback);
    }

    public void getAllMcRecord(DataCallback<List<McDataFromApi>> callback) {
        Api.getInstance().getAllMcRecord(callback);
    }
}
