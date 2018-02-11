package com.yazhi1992.moon.ui.home.home;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;

/**
 * Created by zengyazhi on 2018/2/11.
 */

public class HomeFragmentPresenter {
    public void getImgUrl(DataCallback<String> callback) {
        Api.getInstance().getHomeImg(callback);
    }
}
