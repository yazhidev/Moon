package com.yazhi1992.moon.viewmodel;

import android.databinding.ObservableField;

import com.avos.avoscloud.AVObject;
import com.yazhi1992.moon.constant.TableConstant;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.yazhilib.utils.LibUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengyazhi on 2018/1/29.
 * <p>
 * 历史页外层布局，包含通用的消息发布者用户名、用户头像、该消息的评论列表
 */

public abstract class IHistoryBean<T extends IDataBean> extends IDataBean {
    private int mId;
    public ObservableField<String> mUserName = new ObservableField<>();
    public ObservableField<String> mUserHeadUrl = new ObservableField<>();
    private int mType;
    public ObservableField<T> mData = new ObservableField<>();
    private List<CommentBean> mCommentDatas;

    public IHistoryBean(HistoryItemDataFromApi loveHistoryItemData) {
        AVObject avObj = loveHistoryItemData.getAvObject();
        setType(avObj.getInt(TableConstant.LoveHistory.TYPE));
        setId(avObj.getInt(TableConstant.LoveHistory.ID));
        setObjectId(avObj.getObjectId());
        setCreateTime(avObj.getCreatedAt());
        setUpdateTime(avObj.getUpdatedAt());
        AVObject user = avObj.getAVObject(TableConstant.LoveHistory.USER);
        if (user != null) {
            setUserName(user.getString(TableConstant.AVUserClass.NICK_NAME));
            setUserHeadUrl(user.getAVFile(TableConstant.AVUserClass.HEAD_IMG_FILE).getUrl());
        }
        JSONArray jsonArray = avObj.getJSONArray(TableConstant.LoveHistory.COMMENT_LIST);
        if (jsonArray != null && jsonArray.length() > 0) {
            //评论有数据
            mCommentDatas = new ArrayList<>();
            User userDao = new UserDaoUtil().getUserDao();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CommentBean commentBean = new CommentBean(jsonObject.getString(CommentBean.CONTENT));
                    String userId = jsonObject.getString(CommentBean.USER_ID);
                    String replyId = jsonObject.getString(CommentBean.REPLAY_ID);
                    commentBean.setUserId(userId);
                    commentBean.setUserName(userId.equals(userDao.getObjectId()) ? userDao.getName() : userDao.getLoverName());
                    if (LibUtils.notNullNorEmpty(replyId) && !"null".equals(replyId)) {
                        commentBean.setReplyName(replyId.equals(userDao.getObjectId()) ? userDao.getName() : userDao.getLoverName());
                    }
                    commentBean.setReplyId(replyId);
                    commentBean.setId(jsonObject.getLong(CommentBean.ID));
                    commentBean.setParentId(jsonObject.getString(CommentBean.PARENT_ID));
                    mCommentDatas.add(commentBean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        setData(transformAvObject(loveHistoryItemData));
    }

    public void setData(T data) {
        mData.set(data);
    }

    public List<CommentBean> getCommentDatas() {
        if (mCommentDatas == null) {
            mCommentDatas = new ArrayList<>();
        }
        return mCommentDatas;
    }

    public void setCommentDatas(List<CommentBean> commentDatas) {
        mCommentDatas = commentDatas;
    }

    //由子类继承并重写对应的转换规则，例如许愿有等级，纪念日有日期等
    abstract T transformAvObject(HistoryItemDataFromApi loveHistoryItemData);

    public T getData() {
        return mData.get();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getUserName() {
        return mUserName.get();
    }

    public void setUserName(String userName) {
        mUserName.set(userName);
    }

    public String getUserHeadUrl() {
        return mUserHeadUrl.get();
    }

    public void setUserHeadUrl(String userHeadUrl) {
        mUserHeadUrl.set(userHeadUrl);
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
