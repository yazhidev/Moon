package com.yazhi1992.moon.event;

import com.yazhi1992.moon.constant.ActionConstant;

/**
 * Created by zengyazhi on 2018/1/29.
 */

public class AddDataEvent {
    private String action;

    public AddDataEvent(@ActionConstant.AddAction String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}
