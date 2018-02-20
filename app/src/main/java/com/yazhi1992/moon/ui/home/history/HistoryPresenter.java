package com.yazhi1992.moon.ui.home.history;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.viewmodel.CommentBean;
import com.yazhi1992.moon.viewmodel.HistoryItemDataFromApi;
import com.yazhi1992.yazhilib.utils.LibUtils;

import java.util.List;

/**
 * Created by zengyazhi on 2018/1/25.
 */

public class HistoryPresenter {

    User userDao = new UserDaoUtil().getUserDao();

    public void getLoveHistory(int lastItemId, int size, final DataCallback<List<HistoryItemDataFromApi>> dataCallback) {
        Api.getInstance().getLoveHistory(lastItemId, size, dataCallback);
    }

    public void delete(int type, String dayObjId, DataCallback<Boolean> callback) {
        Api.getInstance().deleteHistoryData(type, dayObjId, callback);
    }

    public void addComment(String content, String parentObjId,final DataCallback<CommentBean> dataCallback) {
        Api.getInstance().addComment(content, parentObjId, new DataCallback<CommentBean>() {
            @Override
            public void onSuccess(CommentBean data) {
                data.setUserName(userDao.getName());
                dataCallback.onSuccess(data);
            }

            @Override
            public void onFailed(int code, String msg) {
                dataCallback.onFailed(code, msg);
            }
        });
    }

    public void replyComment(String content, String parentObjId, String peerId, DataCallback<CommentBean> dataCallback) {
        Api.getInstance().replyComment(content, parentObjId, peerId, new DataCallback<CommentBean>() {
            @Override
            public void onSuccess(CommentBean data) {
                data.setUserName(userDao.getName());
                data.setReplyName(userDao.getLoverName());
                dataCallback.onSuccess(data);
            }

            @Override
            public void onFailed(int code, String msg) {
                dataCallback.onFailed(code, msg);
            }
        });
    }
}