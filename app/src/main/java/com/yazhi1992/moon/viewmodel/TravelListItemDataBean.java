package com.yazhi1992.moon.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

/**
 * Created by zengyazhi on 2018/10/8.
 */

public class TravelListItemDataBean extends BaseObservable {

    public String mObjectId;
    public ObservableField<String> mDes = new ObservableField<>();
    public ObservableBoolean mComplete = new ObservableBoolean();

    public TravelListItemDataBean(String itemDes, boolean itemComplete, String objId) {
        mDes.set(itemDes);
        mComplete.set(itemComplete);
        mObjectId = objId;
    }

//    @Bindable
//    public Boolean getRememberMe() {
//        return data.;
//    }
//
//    public void setRememberMe(Boolean value) {
//        // Avoids infinite loops.
//        if (data.rememberMe != value) {
//            data.rememberMe = value;
//
//            // React to the change.
//            saveData();
//
//            // Notify observers of a new value.
//            notifyPropertyChanged(BR.remember_me);
//        }
//    }
}
