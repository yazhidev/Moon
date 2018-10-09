package com.yazhi1992.moon.api.bean;

/**
 * Created by zengyazhi on 2018/10/9.
 */

public class DingDingBean {

    public String msgtype = "text";
    public Text text = new Text();
    public At at = new At();

    public static class Text{
        String content = "小本本更新啦~";
    }

    public static class At{
        boolean isAtAll = true;
    }
}


