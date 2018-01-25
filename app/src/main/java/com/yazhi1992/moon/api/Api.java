package com.yazhi1992.moon.api;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.yazhi1992.moon.constant.NameContant;

import java.util.List;

/**
 * Created by zengyazhi on 2018/1/25.
 */

public class Api {
    private Api() {

    }

    private static class ApiHolder {
        private static Api INSTANCE = new Api();
    }

    public static Api getInstance() {
        return ApiHolder.INSTANCE;
    }

    private interface onResultSuc{
        void onSuc();
    }

    private void handleResult(AVException e, final DataCallback dataCallback, onResultSuc onResultSuc) {
        if (e == null) {
            onResultSuc.onSuc();
        } else {
            dataCallback.onFailed(e.getCode(), e.getMessage());
        }
    }

    /**
     * 获取历史列表数据
     * @param lastItemId 当前列表末尾数据 id
     * @param size 每页个数
     * @param dataCallback
     */
    public void getLoveHistory(int lastItemId, int size, final DataCallback<List<AVObject>> dataCallback) {
        AVQuery<AVObject> query = new AVQuery<>(NameContant.LoveHistory.CLAZZ_NAME);
        query.include(NameContant.MemorialDay.CLAZZ_NAME);
        query.orderByDescending(NameContant.LoveHistory.ID);
        query.limit(size);
        if(lastItemId != -1) {
            query.whereLessThan(NameContant.LoveHistory.ID, lastItemId);
        }
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(final List<AVObject> list, AVException e) {
                // TODO: 2018/1/25 多列表返回数据模型优化
                handleResult(e, dataCallback, () -> dataCallback.onSuccess(list));
            }
        });
    }

    /**
     * 添加纪念日
     * @param title 标题
     * @param time 时间
     * @param dataCallback
     */
    public void addMemorialDay(String title, long time, final DataCallback<Boolean> dataCallback) {
        //存到纪念日表 + 首页历史列表
        AVObject memorialDayObj = new AVObject(NameContant.MemorialDay.CLAZZ_NAME);
        memorialDayObj.put(NameContant.MemorialDay.TITLE, title);
        memorialDayObj.put(NameContant.MemorialDay.TIME, time);

        AVObject loveHistoryObj = new AVObject(NameContant.LoveHistory.CLAZZ_NAME);
        loveHistoryObj.put(NameContant.LoveHistory.MEMORIAL_DAY, memorialDayObj);
        loveHistoryObj.put(NameContant.LoveHistory.TYPE, NameContant.LoveHistory.TYPE_MEMORIAL_DAY);

        //保存关联对象的同时，被关联的对象也会随之被保存到云端。
        loveHistoryObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                handleResult(e, dataCallback, () -> dataCallback.onSuccess(true));
            }
        });
    }

}
