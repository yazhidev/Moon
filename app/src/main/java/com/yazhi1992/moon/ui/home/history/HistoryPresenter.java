package com.yazhi1992.moon.ui.home.history;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.viewmodel.CommentBean;
import com.yazhi1992.moon.viewmodel.HistoryItemDataFromApi;

import java.util.List;

/**
 * Created by zengyazhi on 2018/1/25.
 */

public class HistoryPresenter {

    public void getLoveHistory(int lastItemId, int size, final DataCallback<List<HistoryItemDataFromApi>> dataCallback) {
        Api.getInstance().getLoveHistory(lastItemId, size, dataCallback);
    }

    public void delete(String objId, int type, String dayObjId, DataCallback<Boolean> callback) {
        Api.getInstance().deleteHistoryData(objId, type, dayObjId, callback);
    }

    public void addComment(String content, String parentObjId,final DataCallback<CommentBean> dataCallback) {
        Api.getInstance().addComment(content, parentObjId, dataCallback);
    }

    public void replyComment(String content, String parentObjId, String peerId, String peerName, DataCallback<CommentBean> dataCallback) {
        Api.getInstance().replyComment(content, parentObjId, peerId, peerName, dataCallback);
    }
}