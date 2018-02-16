package com.yazhi1992.moon.ui.setgender;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.TypeConstant;

/**
 * Created by zengyazhi on 2018/2/12.
 */

public class SetGenderPresenter {

    public void setGender(@TypeConstant.Gender int gender, DataCallback<Integer> callback) {
        Api.getInstance().setGender(gender, callback);
    }
}
