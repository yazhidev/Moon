package com.yazhi1992.moon.viewmodel;

import com.avos.avoscloud.AVObject;
import com.yazhi1992.moon.constant.TableConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengyazhi on 2018/1/29.
 *
 * 历史页外层布局，包含通用的消息发布者用户名、用户头像、该消息的评论列表
 */

public abstract class IHistoryBean<T extends IDataBean> extends IDataBean {
    private int mId;
    private String mUserName;
    private String mUserHeadUrl;
    private int mType;
    private T mData;
    private List<CommentBean> mCommentDatas;

    public IHistoryBean(HistoryBeanFromApi loveHistoryItemData) {
        AVObject avObj = loveHistoryItemData.getAvObject();
        setType(avObj.getInt(TableConstant.LoveHistory.TYPE));
        setId(avObj.getInt(TableConstant.LoveHistory.ID));
        setObjectId(avObj.getObjectId());
        setCreateTime(avObj.getCreatedAt());
        setUpdateTime(avObj.getUpdatedAt());
        AVObject user = avObj.getAVObject(TableConstant.LoveHistory.USER);
        if (user != null) {
            setUserName(user.getString(TableConstant.AVUserClass.USER_NAME));
            setUserHeadUrl(user.getString(TableConstant.AVUserClass.HEAD_URL));
        }
        List<AVObject> commentList = loveHistoryItemData.getCommentList();
        if(commentList != null && !commentList.isEmpty()) {
            //评论有数据，对网络返回的评论数据转型成我们需要的数据模型
            mCommentDatas = new ArrayList<>();
            for(AVObject object : commentList) {
                AVObject commentUser = object.getAVObject(TableConstant.Comment.USER);
                AVObject replyUser = object.getAVObject(TableConstant.Comment.REPLY_USER);
                CommentBean commentBean = new CommentBean(object.getString(TableConstant.Comment.CONTENT), commentUser.getString(TableConstant.AVUserClass.USER_NAME));
                if(replyUser != null) {
                    commentBean.setReplyName(replyUser.getString(TableConstant.AVUserClass.USER_NAME));
                }
                mCommentDatas.add(commentBean);
            }
        }
        mData = transformAvObject(loveHistoryItemData);
    }

    public void setData(T data) {
        mData = data;
    }

    public List<CommentBean> getCommentDatas() {
        return mCommentDatas;
    }

    public void setCommentDatas(List<CommentBean> commentDatas) {
        mCommentDatas = commentDatas;
    }

    //由子类继承并重写对应的转换规则，例如许愿有等级，纪念日有日期等
    abstract T transformAvObject(HistoryBeanFromApi loveHistoryItemData);

    public T getData() {
        return mData;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getUserHeadUrl() {
        return mUserHeadUrl;
    }

    public void setUserHeadUrl(String userHeadUrl) {
        mUserHeadUrl = userHeadUrl;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
