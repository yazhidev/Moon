package com.yazhi1992.moon.ui.addmemorialday;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;

/**
 * Created by zengyazhi on 2018/2/5.
 */

public class AddMemorialDayPresenter {

    public void addMemorialDay(String title, long time, final DataCallback<Boolean> dataCallback) {
        Api.getInstance().addMemorialDay(title, time, dataCallback);
    }

    public void edit(String id, String title, long time, final DataCallback<Boolean> dataCallback) {
        Api.getInstance().editMemorialDay(id, title, time, dataCallback);
    }

    public void delete(String objId, DataCallback<Boolean> callback) {
        Api.getInstance().deleteMemorialDayData(objId, callback);
    }
}
