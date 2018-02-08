package com.yazhi1992.moon.viewmodel;

import android.databinding.ObservableField;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class HopeItemDataBean extends IDataBean{

    public ObservableField<String> mTitle = new ObservableField<>();
    public ObservableField<Integer> mLevel = new ObservableField<>(); //等级
    private String mUserName;
    private String mUserHeadUrl;
    private int mStatus; //0未完成，1已完成

    public HopeItemDataBean(String title, int level) {
        this.mTitle.set(title);
        this.mLevel.set(level);
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
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

    public String getTitle() {
        return mTitle.get();
    }

    public void setTitle(String title) {
        this.mTitle.set(title);
    }

    public int getLevel() {
        return mLevel.get();
    }

    public void setLevel(int level) {
        this.mLevel.set(level);
    }
}
