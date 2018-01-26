package com.yazhi1992.moon.ui.bindlover;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.yazhi1992.moon.constant.NameContant;
import com.yazhi1992.moon.sql.User;
import com.yazhi1992.moon.sql.UserDaoUtil;
import com.yazhi1992.yazhilib.utils.LibUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zengyazhi on 2018/1/26.
 */

public class BindLoverPresenter {

    public String getInviteNum() {
        //1. 生成6位随机数，插入云端
        //存入本地
        User user = new UserDaoUtil().getUserDao();
        String inviteNum;
        inviteNum = user.getInviteNumber();
        if(LibUtils.isNullOrEmpty(inviteNum)) {
            //查询云端是否已有邀请码
            AVUser currentUser = AVUser.getCurrentUser();
            inviteNum = currentUser.getString(NameContant.AVUserClass.INVITE_NUMBER);
        }
        if(LibUtils.isNullOrEmpty(inviteNum)) {
            inviteNum = getRandomNum(6);
            //存入云端
            AVUser currentUser = AVUser.getCurrentUser();
            currentUser.put(NameContant.AVUserClass.INVITE_NUMBER, inviteNum);
            AVObject avObject = new AVObject(NameContant.BindLover.CLAZZ_NAME);
            avObject.put(NameContant.BindLover.INVITE_NUMBER, inviteNum);
            avObject.put(NameContant.BindLover.USER_ID, currentUser.getObjectId());
            List<AVObject> objects = new ArrayList<>();
            objects.add(currentUser);
            objects.add(avObject);
            AVObject.saveAllInBackground(objects);
            //存入数据库
            user.setInviteNumber(inviteNum);
            new UserDaoUtil().update(user);
        }
        return inviteNum;
    }

    private String getRandomNum(int length) {
        String base = "0123456789";
        int len = base.length();

        Random random = new Random();
        StringBuilder sBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sBuilder.append(base.charAt(random.nextInt(len)));
        }

        return sBuilder.toString();
    }
}
