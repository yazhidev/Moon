package com.yazhi1992.moon.viewmodel;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class HopeItemDataBean extends IDataBean{

    private String title;
    private int level; //等级
    private String mUserName;
    private String mUserHeadUrl;
    private int status; //0未完成，1已完成

    public HopeItemDataBean(String title, int level) {
        this.title = title;
        this.level = level;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
