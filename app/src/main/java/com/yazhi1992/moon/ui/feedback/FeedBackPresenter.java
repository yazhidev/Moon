package com.yazhi1992.moon.ui.feedback;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;

/**
 * Created by zengyazhi on 2018/5/11.
 */

public class FeedBackPresenter {

    public void addFeedback(String content, String number,  String imgPath, DataCallback<Boolean> callback) {
        Api.getInstance().addFeedBack(content, number, imgPath, new DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean isSuc) {
                callback.onSuccess(isSuc);
            }

            @Override
            public void onFailed(int code, String msg) {
                callback.onFailed(code, msg);
            }
        });
    }
}
