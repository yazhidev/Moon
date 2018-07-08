package com.yazhi1992.moon.data;

import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.sql.UserDaoUtil;

/**
 * Created by zengyazhi on 2018/2/12.
 */

public class CheckIsLoginFilter implements ICheckDataFilter {

    @Override
    public void check(ICheckDataCallBack callBack) {
        UserDaoUtil userDaoUtil = new UserDaoUtil();
        if(userDaoUtil.getUserDao() == null) {
            //未登录
            ActivityRouter.gotoNewLogin();
        } else {
            //通过，continue
            callBack.doContinue();
        }
    }
}
