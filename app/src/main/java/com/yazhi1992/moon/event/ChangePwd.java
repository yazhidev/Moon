package com.yazhi1992.moon.event;

/**
 * Created by zengyazhi on 2018/2/20.
 *
 * 修改密码
 */

public class ChangePwd {
    String email;

    public ChangePwd(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
