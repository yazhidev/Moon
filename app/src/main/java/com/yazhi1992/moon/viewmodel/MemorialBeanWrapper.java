package com.yazhi1992.moon.viewmodel;

import cn.leancloud.AVObject;
import com.yazhi1992.moon.constant.TableConstant;

/**
 * Created by zengyazhi on 2018/1/29.
 *
 * 首页数据包装类：纪念日
 */

public class MemorialBeanWrapper extends IHistoryBean<MemorialDayBean> {

    public MemorialBeanWrapper(HistoryItemDataFromApi loveHistoryItemData) {
        super(loveHistoryItemData);
    }

    @Override
    MemorialDayBean transformAvObject(HistoryItemDataFromApi loveHistoryItemData) {
        //纪念日类型
        AVObject avObject = loveHistoryItemData.getAvObject();
        AVObject memorialDayItemData = avObject.getAVObject(TableConstant.LoveHistory.MEMORIAL_DAY);
        MemorialDayBean memorialDayBean = new MemorialDayBean(memorialDayItemData.getString(TableConstant.MemorialDay.TITLE), memorialDayItemData.getLong(TableConstant.MemorialDay.TIME));
        memorialDayBean.setObjectId(memorialDayItemData.getObjectId());
        return memorialDayBean;
    }
}
