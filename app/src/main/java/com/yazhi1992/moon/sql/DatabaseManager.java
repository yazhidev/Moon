package com.yazhi1992.moon.sql;

import android.content.Context;

import java.util.List;

/**
 * Created by zengyazhi on 2018/1/25.
 */

public class DatabaseManager {

    private DaoSession mDaoSession;

    private DatabaseManager() {
    }

    private static class DatabaseManagerHolder {
        private static DatabaseManager INSTANCE = new DatabaseManager();
    }

    public static DatabaseManager getInstance() {
        return DatabaseManagerHolder.INSTANCE;
    }

    public void init(Context context) {
        mDaoSession = new DaoMaster(new DaoMaster.DevOpenHelper(context, "greendao_moon.db").getWritableDb()).newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public User getUser() {
        List<User> users = getDaoSession().getUserDao().loadAll();
        if(users.size() > 0) {
            return users.get(0);
        }
        return null;
    }

    public List<User> getUserDao() {
        return getDaoSession().getUserDao().loadAll();
    }

//    public void insert
}
