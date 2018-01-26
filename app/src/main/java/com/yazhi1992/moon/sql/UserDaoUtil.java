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

    public void update(User user) {
        updateSingle(user);
    }
}
