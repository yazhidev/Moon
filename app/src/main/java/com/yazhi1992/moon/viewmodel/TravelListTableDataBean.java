package com.yazhi1992.moon.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

import java.io.Serializable;

/**
 * Created by zengyazhi on 2018/10/8.
 */

public class TravelListTableDataBean extends BaseObservable implements Serializable {

    public String mObjectId;
    public ObservableField<String> mName = new ObservableField<>();

    public TravelListTableDataBean(String name, String objId) {
        mName.set(name);
        mObjectId = objId;
    }

}
