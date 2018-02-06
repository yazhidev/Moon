package com.yazhi1992.moon.viewmodel;

import com.yazhi1992.moon.constant.ActionConstant;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class AddItemBean {

    private @ActionConstant.AddAction String mAction;
    private String mTitle;

    public AddItemBean(@ActionConstant.AddAction String action, String title) {
        this.mAction = action;
        this.mTitle = title;
    }

    public String getAction() {
        return mAction;
    }

    public void setAction(@ActionConstant.AddAction String action) {
        this.mAction = action;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }
}
