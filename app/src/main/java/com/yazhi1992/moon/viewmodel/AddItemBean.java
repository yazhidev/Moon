package com.yazhi1992.moon.viewmodel;

import com.yazhi1992.moon.constant.ActionConstant;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class AddItemBean {

    private String acction;
    private String title;

    public AddItemBean(@ActionConstant.AddAction String action, String title) {
        this.acction = action;
        this.title = title;
    }

    public String getAcction() {
        return acction;
    }

    public void setAcction(String acction) {
        this.acction = acction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
