package com.yazhi1992.moon.event;

/**
 * Created by zengyazhi on 2018/2/11.
 */

public class AddHomeImg {
    String url;

    public AddHomeImg(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
