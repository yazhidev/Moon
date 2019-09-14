package com.yazhi1992.moon.viewmodel;

import cn.leancloud.AVObject;

/**
 * Created by zengyazhi on 2018/2/6.
 */

public class ITransformBean {
    AVObject mAVObject;

    public ITransformBean(AVObject AVObject) {
        mAVObject = AVObject;
    }

    public AVObject getAVObject() {
        return mAVObject;
    }

    public void setAVObject(AVObject AVObject) {
        mAVObject = AVObject;
    }
}
