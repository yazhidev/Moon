package com.yazhi1992.moon.ui.addhope;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;

/**
 * Created by zengyazhi on 2018/2/5.
 */

public class AddHopePresenter {
    public void addHope(String title, int level, DataCallback<Boolean> callback) {
        Api.getInstance().addHope(title, level, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                callback.onSuccess(data);
            }

            @Override
            public void onFailed(int code, String msg) {
                callback.onFailed(code, msg);
            }
        });
    }
}
