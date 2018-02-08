package com.yazhi1992.moon.viewmodel;

import android.databinding.ObservableField;

import java.util.Date;

/**
 * Created by zengyazhi on 2018/2/5.
 *
 * 与网络相关的数据模型的基类，都包含唯一id，创建时间、修改时间
 */

public class IDataBean {
    private String mObjectId;
    public ObservableField<Date> mCreateTime = new ObservableField<>();;
    private Date mUpdateTime;

    public String getObjectId() {
        return mObjectId;
    }

    public void setObjectId(String objectId) {
        mObjectId = objectId;
    }

    public Date getCreateTime() {
        return mCreateTime.get();
    }

    public void setCreateTime(Date createTime) {
        mCreateTime.set(createTime);
    }

    public Date getUpdateTime() {
        return mUpdateTime;
    }

    public void setUpdateTime(Date updateTime) {
        mUpdateTime = updateTime;
    }
}
