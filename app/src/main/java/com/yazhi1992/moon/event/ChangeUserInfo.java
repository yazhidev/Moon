package com.yazhi1992.moon.event;

/**
 * Created by zengyazhi on 2018/2/19.
 *
 * 修改用户头像、昵称
 */

public class ChangeUserInfo {
    String name;
    String headUrl;

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
