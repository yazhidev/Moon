package com.yazhi1992.moon.ui.newlogin;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;

/**
 * Created by zengyazhi on 2018/2/20.
 */

public class LoginPresenter {

    public void login(String account, String pwd,DataCallback<Boolean> callback) {
        Api.getInstance().login(account, pwd, callback);
    }
}
