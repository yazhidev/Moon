package com.yazhi1992.moon.sql;

import java.util.List;

/**
 * Created by zengyazhi on 2018/1/26.
 */

public class UserDaoUtil extends BaseDao<User> {

    public void insert(User user) {
        insert(user, null);
    }

    public void clear() {
        deleteAll(User.class, null);
    }

    public void  getUserDaoAsyn(DaoCallback.QueryCallback<User> callback) {
        queryAllAsyn(User.class, null, result -> {
            if(result != null && result.size() > 0) {
                callback.onCallback(result.get(0));
            } else {
                callback.onCallback(null);
            }
        });
    }

    public User getUserDao() {
        List<User> result = queryAll(User.class, null);
        if(result != null && result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    public void updateLoveInfo(String loverId, String loverName, String loverHeadurl) {
        User userDao = getUserDao();
        userDao.setHaveLover(true);
        userDao.setLoverId(loverId);
        userDao.setLoverName(loverName);
        userDao.setLoverHeadUrl(loverHeadurl);
        update(userDao);
    }

    public void updateGender(int gender) {
        User userDao = getUserDao();
        userDao.setGender(gender);
        update(userDao);
    }

    public void update(User user) {
        updateSingle(user);
    }
}
