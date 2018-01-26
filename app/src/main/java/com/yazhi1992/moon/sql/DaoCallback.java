package com.yazhi1992.moon.sql;

/**
 * Created by zengyazhi on 2018/1/26.
 */

public interface DaoCallback {

    interface QueryCallback<T> {
        void onCallback(T t);
    }
}
