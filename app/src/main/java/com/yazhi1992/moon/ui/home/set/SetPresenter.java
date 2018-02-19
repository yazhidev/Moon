package com.yazhi1992.moon.ui.home.set;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;

/**
 * Created by zengyazhi on 2018/1/26.
 */

public class SetPresenter {

    public void logout(DataCallback<Boolean> callback) {
        new UserDaoUtil().clear();
        callback.onSuccess(true);
    }

    public boolean checkLover() {
        new UserDaoUtil().getUserDao().getHaveLover();
        return false;
    }


    public void getLoverInfo(DataCallback<LoverInfo> callback) {
        UserDaoUtil userDaoUtil = new UserDaoUtil();
        User userDao = userDaoUtil.getUserDao();
        Api.getInstance().getLoverInfo(new DataCallback<LoverInfo>() {
            @Override
            public void onSuccess(LoverInfo data) {
                if(data != null) {
                    userDaoUtil.updateLoveInfo(data.name, data.imgurl);
                    callback.onSuccess(data);
                } else {
                    callback.onSuccess(new LoverInfo(userDao.getLoverName(), userDao.getLoverHeadUrl()));
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                //使用旧的本地数据
                callback.onSuccess(new LoverInfo(userDao.getLoverName(), userDao.getLoverHeadUrl()));
            }
        });
    }
}
