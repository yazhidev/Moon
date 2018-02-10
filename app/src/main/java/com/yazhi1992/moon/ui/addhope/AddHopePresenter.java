package com.yazhi1992.moon.ui.addhope;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;

/**
 * Created by zengyazhi on 2018/2/5.
 */

public class AddHopePresenter {

    public void addHope(String title, int level, String link, DataCallback<Boolean> callback) {
        Api.getInstance().addHope(title, level, link, callback);
    }

    public void edit(String objId, String title, int level, String link, DataCallback<Boolean> dataCallback) {
        Api.getInstance().editHope(objId, title, level, link, dataCallback);
    }

    public void delete(String objId, DataCallback<Boolean> callback) {
        Api.getInstance().deleteHopeData(objId, callback);
    }
}
