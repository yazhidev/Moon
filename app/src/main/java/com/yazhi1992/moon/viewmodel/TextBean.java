package com.yazhi1992.moon.viewmodel;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class TextBean extends IDataBean{

    private String mContent;

    public TextBean(String content) {
        this.mContent = content;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }
}
