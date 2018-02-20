package com.yazhi1992.moon.ui.mc;

import com.yazhi1992.moon.api.Api;
import com.yazhi1992.moon.api.DataCallback;
import com.yazhi1992.moon.constant.TypeConstant;
import com.yazhi1992.moon.viewmodel.McBean;

/**
 * Created by zengyazhi on 2018/2/16.
 */

public class McDetailPresenter {

    public void getData(DataCallback<McData> callback) {
        Api.getInstance().getMcDetailInitData(callback);
    }

    public void getLastMcRecord(DataCallback<Boolean> callback) {
        Api.getInstance().getLastMcRecord(callback);
    }

    public void updateMcStatus(@TypeConstant.McStatus int status, long time, DataCallback<Boolean> callback) {
        Api.getInstance().updateMcStatus(status, time, callback);
    }
}
