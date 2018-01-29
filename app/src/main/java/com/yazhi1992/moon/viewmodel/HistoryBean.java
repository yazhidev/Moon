package com.yazhi1992.moon.viewmodel;

/**
 * Created by zengyazhi on 2018/1/25.
 */

public class HistoryBean {
    private int mId;
    private String mUserName;
    private String mUserHeadUrl;
    private int mType;

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
