package com.yazhi1992.moon.data;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.SPKeyConstant;
import com.yazhi1992.moon.viewmodel.ConfigBean;
import com.yazhi1992.yazhilib.utils.LibSPUtils;

/**
 * Created by zengyazhi on 2018/2/19.
 * <p>
 * 获取配置，说说是否可以传图等
 */

public class CheckConfigFilter implements ICheckDataFilter {

    @Override
    public void check(ICheckDataCallBack callBack) {
        Api.getInstance().enterApp();
        Api.getInstance().getConfig(new DataCallback<ConfigBean>() {
            @Override
            public void onSuccess(ConfigBean data) {
                LibSPUtils.setBoolean(SPKeyConstant.PUSH_IMG_ENABLE, data.isCanPushImg());
                LibSPUtils.setBoolean(SPKeyConstant.NOTIFY_DINGDING, data.isNotifyDingDing());
                LibSPUtils.setBoolean(SPKeyConstant.NOTIFY_DD, data.isNotifyDD());
                LibSPUtils.setInt(SPKeyConstant.MC_GO_MAX_DAY, data.getMcGoMaxDay());
                LibSPUtils.setInt(SPKeyConstant.MC_GO_MIN_DAY, data.getMcGoMinDay());
                LibSPUtils.setInt(SPKeyConstant.MC_COME_MAX_DAY, data.getMcComeMaxDay());
                callBack.doContinue();
            }

            @Override
            public void onFailed(int code, String msg) {
                callBack.doContinue();
            }
        });
    }
}
