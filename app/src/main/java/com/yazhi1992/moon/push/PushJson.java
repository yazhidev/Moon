package com.yazhi1992.moon.push;

import com.yazhi1992.moon.util.AppUtils;

/**
 * Created by zengyazhi on 2018/5/1.
 */

public class PushJson {
    String action;
    String userId;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return AppUtils.getGson().toJson(this);
    }
}
