package com.yazhi1992.moon.ui.home.history;

import com.avos.avoscloud.AVObject;
import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;

import java.util.List;

/**
 * Created by zengyazhi on 2018/1/25.
 */

public class HistoryPresenter {

    public void getLoveHistory(int lastItemId, int size, final DataCallback<List<AVObject>> dataCallback) {
        Api.getInstance().getLoveHistory(lastItemId, size, dataCallback);
    }

    public void delete(String objId, int type, String dayObjId, DataCallback<Boolean> callback) {
        Api.getInstance().deleteHistoryData(objId, type, dayObjId, callback);
    }

    public void addComment(String content, String parentObjId, String replyId, final DataCallback<Boolean> dataCallback) {
        Api.getInstance().addComment(content, parentObjId, replyId, dataCallback);
    }
}