package com.yazhi1992.moon.ui.home.set;

import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.sql.UserDaoUtil;

/**
 * Created by zengyazhi on 2018/1/26.
 */

public class SetPresenter {

    public void logout(DataCallback<Boolean> callback) {
        new UserDaoUtil().clear();
        callback.onSuccess(true);
    }

    public boolean checkLover() {
        new UserDaoUtil().getUserDao().getHaveLover();
        return false;
    }
}
