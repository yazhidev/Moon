package com.yazhi1992.moon.ui.addmemorialday;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;

/**
 * Created by zengyazhi on 2018/2/5.
 */

public class AddMemorialDayPresenter {

    public void addMemorialDay(String title, long time, final DataCallback<Boolean> dataCallback) {
        Api.getInstance().addMemorialDay(title, time, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                dataCallback.onSuccess(data);
            }

            @Override
            public void onFailed(int code, String msg) {
                dataCallback.onFailed(code, msg);
            }
        });
    }
}
