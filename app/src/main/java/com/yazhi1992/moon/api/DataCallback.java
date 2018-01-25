package com.yazhi1992.moon.api;

/**
 * Created by zengyazhi on 2018/1/25.
 */

public interface DataCallback<T> {
    void onSuccess(T data);

    void onFailed(int code, String msg);
}
