package com.yazhi1992.moon.ui.setemail;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;

/**
 * Created by zengyazhi on 2018/2/20.
 */

public class SetEmailPresenter {
    public void checkEmail(String email, DataCallback<Boolean> callback) {
        Api.getInstance().checkEmail(email, callback);
    }

    public void checkEmailStatus(DataCallback<Boolean> callback) {
        Api.getInstance().checkEmailStatus(callback);
    }
}
