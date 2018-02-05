package com.yazhi1992.moon.viewmodel;

import com.avos.avoscloud.AVObject;
import com.yazhi1992.moon.constant.NameConstant;

/**
 * Created by zengyazhi on 2018/1/29.
 *
 * 首页数据包装类：纪念日
 */

public class HopeItemDataWrapper extends IHistoryBean<HopeItemDataBean> {

    public HopeItemDataWrapper(AVObject loveHistoryItemData) {
        super(loveHistoryItemData);
    }

    @Override
    HopeItemDataBean transformAvObject(AVObject loveHistoryItemData) {
        //纪念日类型
        AVObject hopeItemData = loveHistoryItemData.getAVObject(NameConstant.LoveHistory.HOPE);
        HopeItemDataBean hopeBean = new HopeItemDataBean(hopeItemData.getString(NameConstant.Hope.TITLE)
                , hopeItemData.getInt(NameConstant.Hope.LEVEL));
        hopeBean.setStatus(hopeItemData.getInt(NameConstant.Hope.STATUS));
        hopeBean.setObjectId(hopeItemData.getObjectId());
        return hopeBean;
    }
}
