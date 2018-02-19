package com.yazhi1992.moon.ui.setname;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;

/**
 * Created by zengyazhi on 2018/2/19.
 */

public class SetUserNamePresenter {

    public void setUserName(String userName, DataCallback<String> callback) {
        Api.getInstance().setUserName(userName, callback);
    }
}
