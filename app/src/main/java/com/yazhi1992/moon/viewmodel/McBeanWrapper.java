package com.yazhi1992.moon.viewmodel;

import cn.leancloud.AVObject;
import com.yazhi1992.moon.BaseApplication;
import com.yazhi1992.moon.R;
import com.yazhi1992.moon.constant.TableConstant;
import com.yazhi1992.yazhilib.utils.LibUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by zengyazhi on 2018/2/12.
 *
 * 首页数据包装类：小姨妈
 */

public class McBeanWrapper extends IHistoryBean<McBean> {

    public McBeanWrapper(HistoryItemDataFromApi loveHistoryItemData) {
        super(loveHistoryItemData);
    }

    @Override
    McBean transformAvObject(HistoryItemDataFromApi loveHistoryItemData) {
        AVObject avObject = loveHistoryItemData.getAvObject();
        AVObject mcItemData = avObject.getAVObject(TableConstant.LoveHistory.MC);
        int action = mcItemData.getInt(TableConstant.MC.ACTION);
        McBean mcBean = new McBean(action);
        mcBean.setObjectId(mcItemData.getObjectId());
        return mcBean;
    }
}
