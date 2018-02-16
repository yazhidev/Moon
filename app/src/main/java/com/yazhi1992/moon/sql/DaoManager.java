package com.yazhi1992.moon.sql;

import com.yazhi1992.moon.BaseApplication;

import java.util.List;

/**
 * Created by zengyazhi on 2018/1/25.
 */

public class DaoManager {

    private DaoSession mDaoSession;

    private DaoManager() {
    }

    private static class DatabaseManagerHolder {
        private static DaoManager INSTANCE = new DaoManager();
    }

    protected static DaoManager getInstance() {
        return DatabaseManagerHolder.INSTANCE;
    }

    public DaoSession getDaoSession() {
        if(mDaoSession == null) {
            MySQLiteOpenHelper helper = new MySQLiteOpenHelper(BaseApplication.getInstance(), "greendao_moon.db",
                    null);
            mDaoSession = new DaoMaster(helper.getWritableDatabase()).newSession();
        }
        return mDaoSession;
    }

    public List<User> getUserDao() {
        return getDaoSession().getUserDao().loadAll();
    }

}
