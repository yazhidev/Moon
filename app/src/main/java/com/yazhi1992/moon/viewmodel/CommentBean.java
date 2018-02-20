package com.yazhi1992.moon.viewmodel;

/**
 * Created by zengyazhi on 2018/2/6.
 */

public class CommentBean extends IDataBean{

    private String mContent;
    private long mId;
    private String mUserName = "";
    private String mUserId;
    private String mReplyName = "";
    private String mReplyId;
    private String mParentId;

    public static final String CONTENT = "content";
    public static final String USER_ID = "userId";
    public static final String REPLAY_ID = "replyId";
    public static final String PARENT_ID = "parentId";
    public static final String ID = "id";

    public CommentBean(String content) {
        mContent = content;
    }

    public String getParentId() {
        return mParentId;
    }

    public void setParentId(String parentId) {
        mParentId = parentId;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getReplyId() {
        return mReplyId;
    }

    public void setReplyId(String replyId) {
        mReplyId = replyId;
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
