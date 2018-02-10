package com.yazhi1992.moon.viewmodel;

import android.databinding.ObservableField;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class HopeItemDataBean extends IDataBean {

    public ObservableField<String> mTitle = new ObservableField<>();
    public ObservableField<Integer> mLevel = new ObservableField<>(); //等级
    public ObservableField<String> mUserName = new ObservableField<>();
    public ObservableField<String> mLink = new ObservableField<>(""); //链接
    public ObservableField<String> mUserHeadUrl = new ObservableField<>();
    public ObservableField<Integer> mStatus = new ObservableField<>(0); //0未完成，1已完成
    public ObservableField<String> mFinishContent = new ObservableField<>("");

    public HopeItemDataBean(String title, int level) {
        this.mTitle.set(title);
        this.mLevel.set(level);
    }

    public String getFinishContent() {
        return mFinishContent.get();
    }

    public void setFinishContent(String content) {
        this.mFinishContent.set(content == null ? "" : content);
    }

    public int getStatus() {
        return mStatus.get();
    }

    public void setStatus(int status) {
        this.mStatus.set(status);
    }

    public String getLink() {
        return mLink.get();
    }

    public void setLink(String mlink) {
        this.mLink.set(mlink == null ? "" : mlink);
    }

    public String getUserName() {
        return mUserName.get();
    }

    public void setUserName(String userName) {
        mUserName.set(userName);
    }

    public String getUserHeadUrl() {
        return mUserHeadUrl.get();
    }

    public void setUserHeadUrl(String userHeadUrl) {
        mUserHeadUrl.set(userHeadUrl);
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
