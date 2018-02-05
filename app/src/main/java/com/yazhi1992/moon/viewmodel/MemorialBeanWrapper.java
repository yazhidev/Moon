package com.yazhi1992.moon.viewmodel;

import com.avos.avoscloud.AVObject;
import com.yazhi1992.moon.constant.NameConstant;

/**
 * Created by zengyazhi on 2018/1/29.
 *
 * 首页数据包装类：纪念日
 */

public class MemorialBeanWrapper extends IHistoryBean<MemorialDayBean> {

    public MemorialBeanWrapper(AVObject loveHistoryItemData) {
        super(loveHistoryItemData);
    }

    @Override
    MemorialDayBean transformAvObject(AVObject loveHistoryItemData) {
        //纪念日类型
        AVObject memorialDayItemData = loveHistoryItemData.getAVObject(NameConstant.LoveHistory.MEMORIAL_DAY);
        MemorialDayBean memorialDayBean = new MemorialDayBean(memorialDayItemData.getString(NameConstant.MemorialDay.TITLE), memorialDayItemData.getLong(NameConstant.MemorialDay.TIME));
        memorialDayBean.setObjectId(memorialDayItemData.getObjectId());
        return memorialDayBean;
    }
}
