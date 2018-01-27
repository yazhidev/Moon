package com.yazhi1992.moon.api.bean;

/**
 * Created by zengyazhi on 2018/1/27.
 */

public class BindLoverBean {
    private String loverId;
    private String loverHeadUrl;
    private String loverName;
    private boolean bindComplete;

    public String getLoverId() {
        return loverId;
    }

    public void setLoverId(String loverId) {
        this.loverId = loverId;
    }

    public String getLoverHeadUrl() {
        return loverHeadUrl;
    }

    public void setLoverHeadUrl(String loverHeadUrl) {
        this.loverHeadUrl = loverHeadUrl;
    }

    public String getLoverName() {
        return loverName;
    }

    public void setLoverName(String loverName) {
        this.loverName = loverName;
    }

    public boolean isBindComplete() {
        return bindComplete;
    }

    public void setBindComplete(boolean bindComplete) {
        this.bindComplete = bindComplete;
    }
}
