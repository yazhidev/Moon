package com.yazhi1992.moon.ui.addtext;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;

/**
 * Created by zengyazhi on 2018/2/6.
 */

public class AddTextPresenter {

    public void addText(String content, String imgPath, DataCallback<String> callback) {
        Api.getInstance().addText(content, imgPath, new DataCallback<String>() {
            @Override
            public void onSuccess(String remoteImgUrl) {
                callback.onSuccess(remoteImgUrl);
            }

            @Override
            public void onFailed(int code, String msg) {
                callback.onFailed(code, msg);
            }
        });
    }
}
