package com.yazhi1992.moon.data;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.constant.TableConstant;
import com.yazhi1992.moon.sql.UserDaoUtil;

/**
 * Created by zengyazhi on 2018/2/14.
 *
 * 是否设置性别
 */

public class CheckIsSetGenderFilter implements ICheckDataFilter {

    @Override
    public void check(ICheckDataCallBack callBack) {
        UserDaoUtil userDaoUtil = new UserDaoUtil();
        if(userDaoUtil.getUserDao().getGender() != 0) {
            //通过，传给下一级继续检验
            callBack.doContinue();
        } else {
            //本地没有数据，同步远端，查看是否未拉取过数据
            // TODO: 2018/2/14 数据层设计：本地没有想要的数据，则查看是否远端数据未更新到本地
            AVUser.getCurrentUser().fetchInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    if(e == null) {
                        if(object.getInt(TableConstant.AVUserClass.GENDER) != 0) {
                            userDaoUtil.updateGender(object.getInt(TableConstant.AVUserClass.GENDER));
                            //已设置
                            callBack.doContinue();
                        } else {
                            //未设置
                            ActivityRouter.gotoSetGender();
                        }
                    } else {
                        //异常
                        callBack.onErr();
                    }
                }
            });
        }
    }
}
