package com.yazhi1992.moon.ui.addtext;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;

/**
 * Created by zengyazhi on 2018/2/6.
 */

public class AddTextPresenter {

    public void addText(String content, DataCallback<Boolean> callback) {
        Api.getInstance().addText(content, new DataCallback<Boolean>() {
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
