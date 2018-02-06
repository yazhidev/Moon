package com.yazhi1992.moon.viewmodel;

/**
 * Created by zengyazhi on 2018/2/6.
 */

public class CommentBean extends IDataBean{

    private String mContent;
    private String mUserName;
    private String mReplyName;

    public CommentBean(String content, String userName) {
        mContent = content;
        mUserName = userName;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getReplyName() {
        return mReplyName;
    }

    public void setReplyName(String replyName) {
        mReplyName = replyName;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }
}
