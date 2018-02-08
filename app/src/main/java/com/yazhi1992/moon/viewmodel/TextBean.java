package com.yazhi1992.moon.viewmodel;

import android.databinding.ObservableField;

/**
 * Created by zengyazhi on 2018/1/24.
 */

public class TextBean extends IDataBean{

    public ObservableField<String> mContent = new ObservableField<>();

    public TextBean(String content) {
        this.mContent.set(content);
    }

    public String getContent() {
        return mContent.get();
    }

    public void setContent(String content) {
        mContent.set(content);
    }
}
