package com.yazhi1992.moon.viewmodel;

import com.avos.avoscloud.AVObject;
import com.yazhi1992.moon.constant.TableConstant;

/**
 * Created by zengyazhi on 2018/1/29.
 */

public abstract class IHistoryBean<T extends IDataBean> extends IDataBean {
    private int mId;
    private String mUserName;
    private String mUserHeadUrl;
    private int mType;
    private T mData;

    public IHistoryBean(AVObject loveHistoryItemData) {
        setType(loveHistoryItemData.getInt(TableConstant.LoveHistory.TYPE));
        setId(loveHistoryItemData.getInt(TableConstant.LoveHistory.ID));
        setObjectId(loveHistoryItemData.getObjectId());
        setCreateTime(loveHistoryItemData.getCreatedAt());
        setUpdateTime(loveHistoryItemData.getUpdatedAt());
        AVObject user = loveHistoryItemData.getAVObject(TableConstant.LoveHistory.USER);
        if(user != null) {
            setUserName(user.getString(TableConstant.AVUserClass.USER_NAME));
            setUserHeadUrl(user.getString(TableConstant.AVUserClass.HEAD_URL));
        }
        mData = transformAvObject(loveHistoryItemData);
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
