package com.yazhi1992.moon.viewmodel;

import android.support.annotation.StringDef;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class DeleteItemBean {

    private String acction;
    private String title;

    public static final String DELETE = "delete";
    public static final String CANCEL = "CANCEL";

    @StringDef({DELETE, CANCEL})
    public @interface DeleteDialogAction {
    }

    public DeleteItemBean(@DeleteDialogAction String action, String title) {
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
