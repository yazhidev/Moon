package com.yazhi1992.moon.ui.forgetpwd;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.sql.UserDaoUtil;

/**
 * Created by zengyazhi on 2018/2/20.
 */

public class ForgetPwdPresenter {

    public void findPwd(String email, DataCallback<Boolean> callback) {
        Api.getInstance().findPwd(email, callback);
    }
}
