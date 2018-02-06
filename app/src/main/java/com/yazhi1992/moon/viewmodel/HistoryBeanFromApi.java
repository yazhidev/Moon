package com.yazhi1992.moon.viewmodel;

import com.avos.avoscloud.AVObject;

import java.util.List;

/**
 * Created by zengyazhi on 2018/2/6.
 */

public class HistoryBeanFromApi {
    private int type;
    private AVObject mData;
    private List<AVObject> mCommentList;

    public HistoryBeanFromApi(int type, AVObject data) {
        this.type = type;
        mData = data;
    }

    public AVObject getAvObject() {
        return mData;
    }

    public void setData(AVObject data) {
        mData = data;
    }

    public List<AVObject> getCommentList() {
        return mCommentList;
    }

    public void setCommentList(List<AVObject> commentList) {
        mCommentList = commentList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
