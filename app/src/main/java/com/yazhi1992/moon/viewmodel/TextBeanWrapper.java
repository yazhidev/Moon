package com.yazhi1992.moon.viewmodel;

import com.avos.avoscloud.AVObject;
import com.yazhi1992.moon.BaseApplication;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.constant.TableConstant;
import com.yazhi1992.yazhilib.utils.LibUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by zengyazhi on 2018/2/6.
 *
 * 首页数据包装类：文本
 */

public class TextBeanWrapper extends IHistoryBean<TextBean> {

    public TextBeanWrapper(HistoryItemDataFromApi loveHistoryItemData) {
        super(loveHistoryItemData);
    }

    @Override
    TextBean transformAvObject(HistoryItemDataFromApi loveHistoryItemData) {
        AVObject avObject = loveHistoryItemData.getAvObject();
        AVObject textItemData = avObject.getAVObject(TableConstant.LoveHistory.TEXT);
        TextBean textBean = new TextBean(textItemData.getString(TableConstant.Text.CONTENT));
        textBean.setObjectId(textItemData.getObjectId());
        AVObject user = avObject.getAVObject(TableConstant.LoveHistory.USER);
        textBean.setUserName(user.getString(TableConstant.AVUserClass.USER_NAME));
        Date createdAt = avObject.getCreatedAt();
        Calendar instance = Calendar.getInstance();
        instance.setTime(createdAt);
        int month = instance.get(Calendar.MONTH) + 1;
        int day = instance.get(Calendar.DATE);
        textBean.setTimeStr(String.format(BaseApplication.getInstance().getString(R.string.create_text_time), LibUtils.numberToChinese(month), LibUtils.numberToChinese(day)));
        return textBean;
    }
}
