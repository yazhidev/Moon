package com.yazhi1992.moon.viewmodel;

import com.avos.avoscloud.AVObject;
import com.yazhi1992.moon.constant.NameContant;

/**
 * Created by zengyazhi on 2018/1/29.
 */

public abstract class IHistoryBean<T> {
    private int mId;
    private String mUserName;
    private String mUserHeadUrl;
    private int mType;
    private T mData;
    private String objectId;

    public IHistoryBean(AVObject loveHistoryItemData) {
        setType(loveHistoryItemData.getInt(NameContant.LoveHistory.TYPE));
        setId(loveHistoryItemData.getInt(NameContant.LoveHistory.ID));
        setUserName(loveHistoryItemData.getString(NameContant.LoveHistory.USER_NAME));
        setUserHeadUrl(loveHistoryItemData.getString(NameContant.LoveHistory.USER_HEAD_URL));
        setObjectId(loveHistoryItemData.getObjectId());
        mData = transformAvObject(loveHistoryItemData);
    }

    public void setData(T data) {
        mData = data;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    abstract T transformAvObject(AVObject loveHistoryItemData);

    public T getData() {
        return mData;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getUserHeadUrl() {
        return mUserHeadUrl;
    }

    public void setUserHeadUrl(String userHeadUrl) {
        mUserHeadUrl = userHeadUrl;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
