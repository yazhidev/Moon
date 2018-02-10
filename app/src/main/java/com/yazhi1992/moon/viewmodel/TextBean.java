package com.yazhi1992.moon.viewmodel;

import android.databinding.ObservableField;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class TextBean extends IDataBean{

    public ObservableField<String> mContent = new ObservableField<>();
    public ObservableField<String> mUserName = new ObservableField<>();
    public ObservableField<String> mTimeStr = new ObservableField<>(); //例如五月十二日

    public TextBean(String content) {
        this.mContent.set(content);
    }

    public String getUserName() {
        return mUserName.get();
    }

    public void setUserName(String userName) {
        mUserName.set(userName);
    }

    public void setTimeStr(String timeStr) {
        mTimeStr.set(timeStr);
    }

    public String getContent() {
        return mContent.get();
    }

    public void setContent(String content) {
        mContent.set(content);
    }
}
