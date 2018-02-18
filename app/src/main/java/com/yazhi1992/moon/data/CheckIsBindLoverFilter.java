package com.yazhi1992.moon.data;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.yazhi1992.moon.ActivityRouter;
import com.yazhi1992.moon.constant.TableConstant;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.moon.util.PushManager;

/**
 * Created by zengyazhi on 2018/2/14.
 *
 * 是否绑定另一半
 */

public class CheckIsBindLoverFilter implements ICheckDataFilter {

    @Override
    public void check(ICheckDataCallBack callBack) {
        UserDaoUtil userDaoUtil = new UserDaoUtil();
        User userDao = userDaoUtil.getUserDao();
        if(userDao != null && userDao.getHaveLover()) {
            //通过，传给下一级继续检验
            callBack.doContinue();
        } else {
            //本地没有数据，同步远端，查看是否未拉取过数据
            // TODO: 2018/2/14 数据层设计：本地没有想要的数据，则查看是否远端数据未更新到本地
            AVUser.getCurrentUser().fetchInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(AVObject object, AVException e) {
                    if(e == null) {
                        if(object.getBoolean(TableConstant.AVUserClass.HAVE_LOVER)) {
                            userDaoUtil.updateLoveInfo(object.getString(TableConstant.AVUserClass.LOVER_ID)
                                    , object.getString(TableConstant.AVUserClass.LOVER_NAME)
                                    , object.getString(TableConstant.AVUserClass.LOVER_HEAD_URL));
                            //已绑定
                            callBack.doContinue();
                        } else {
                            //未绑定
                            ActivityRouter.gotoBindLover();
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
