package com.yazhi1992.moon.util;

import com.yazhi1992.moon.api.DataCallback;

/**
 * Created by zengyazhi on 2018/2/19.
 */

public interface IUploader {

    /**
     * 上传
     * @param filePath
     * @param callback 返回上传成功后图片网络地址
     */
    void upload(String filePath, DataCallback<String> callback);
}
