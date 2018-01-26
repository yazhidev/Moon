package com.yazhi1992.moon.sql;

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

    public void  getUserDao(DaoCallback.QueryCallback<User> callback) {
        queryAll(User.class, null, result -> {
            if(result != null && result.size() > 0) {
                callback.onCallback(result.get(0));
            }
        });
    }
}
