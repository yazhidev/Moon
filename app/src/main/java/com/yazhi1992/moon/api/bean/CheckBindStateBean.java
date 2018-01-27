package com.yazhi1992.moon.api.bean;

/**
 * Created by zengyazhi on 2018/1/27.
 */

public class CheckBindStateBean {
    private Integer state; //0 互相绑定成功，1 已绑定对方，等待对方绑定你, 2 你未绑定任何人，或未生成配对码
    private String peerObjId; //对方objectId

    public CheckBindStateBean(Integer state) {
        this.state = state;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getPeerObjId() {
        return peerObjId;
    }

    public void setPeerObjId(String peerObjId) {
        this.peerObjId = peerObjId;
    }
}
