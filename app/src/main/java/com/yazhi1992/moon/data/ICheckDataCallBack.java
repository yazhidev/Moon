package com.yazhi1992.moon.data;

/**
 * Created by zengyazhi on 2018/2/12.
 *
 * 检验数据回调，例如监测是否绑定，如果未绑定，则跳转相应的绑定页面。如果已通过，则 doContinue 传给下一级检验
 */

public interface ICheckDataCallBack {
    void doContinue();

    void onErr();
}
