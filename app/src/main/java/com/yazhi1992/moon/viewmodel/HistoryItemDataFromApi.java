package com.yazhi1992.moon.viewmodel;


import java.util.List;

import cn.leancloud.AVObject;

/**
 * Created by zengyazhi on 2018/2/6.
 */

public class HistoryItemDataFromApi {
    private int type;
    private AVObject mData;

    public HistoryItemDataFromApi(int type, AVObject data) {
        this.type = type;
        mData = data;
    }

    public AVObject getAvObject() {
        return mData;
    }

    public void setData(AVObject data) {
        mData = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
