package com.yazhi1992.moon.viewmodel;

import com.avos.avoscloud.AVObject;
import com.yazhi1992.moon.constant.TableConstant;

/**
 * Created by zengyazhi on 2018/2/5.
 *
 * 首页数据包装类：心愿
 */

public class HopeItemDataWrapper extends IHistoryBean<HopeItemDataBean> {

    public HopeItemDataWrapper(HistoryItemDataFromApi loveHistoryItemData) {
        super(loveHistoryItemData);
    }

    @Override
    HopeItemDataBean transformAvObject(HistoryItemDataFromApi loveHistoryItemData) {
        //纪念日类型
        AVObject avObject = loveHistoryItemData.getAvObject();
        AVObject hopeItemData = avObject.getAVObject(TableConstant.LoveHistory.HOPE);
        HopeItemDataBean hopeBean = new HopeItemDataBean(hopeItemData.getString(TableConstant.Hope.TITLE)
                , hopeItemData.getInt(TableConstant.Hope.LEVEL));
        hopeBean.setStatus(hopeItemData.getInt(TableConstant.Hope.STATUS));
        hopeBean.setLink(hopeItemData.getString(TableConstant.Hope.LINK));
        hopeBean.setObjectId(hopeItemData.getObjectId());
        hopeBean.setFinishContent(hopeItemData.getString(TableConstant.Hope.FINISH_WORD));
        return hopeBean;
    }
}
