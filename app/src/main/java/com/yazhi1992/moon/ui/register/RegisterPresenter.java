package com.yazhi1992.moon.ui.register;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;

/**
 * Created by zengyazhi on 2018/2/20.
 */

public class RegisterPresenter {
    public void register(String account, String pwd, String nickName, DataCallback<Boolean> callback) {
        Api.getInstance().register(account, pwd, nickName, callback);
    }
}
