package com.yazhi1992.moon.ui.usercenter;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;

/**
 * Created by zengyazhi on 2018/2/11.
 */

public class UserCenterPresenter {

    public void updateHeadImg(String filePath, DataCallback<String> callback) {
        Api.getInstance().updateHeadImg(filePath, callback);
    }
}
