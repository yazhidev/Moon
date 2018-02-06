package com.yazhi1992.moon.viewmodel;

import android.support.annotation.StringDef;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class DeleteItemBean {

    private String mAction;
    private String mTitle;

    public static final String DELETE = "delete";
    public static final String CANCEL = "CANCEL";

    @StringDef({DELETE, CANCEL})
    public @interface DeleteDialogAction {
    }

    public DeleteItemBean(@DeleteDialogAction String action, String title) {
        this.mAction = action;
        this.mTitle = title;
    }

    public String getAction() {
        return mAction;
    }

    public void setAction(String action) {
        this.mAction = action;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }
}
