package com.yazhi1992.moon.viewmodel;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by zengyazhi on 2018/10/8.
 */

public class TravelListItemDataBean extends BaseObservable implements Serializable {

    public String mObjectId;
    public ObservableField<String> mDes = new ObservableField<>();
    public ObservableBoolean mComplete = new ObservableBoolean();

    public TravelListItemDataBean(String itemDes, boolean itemComplete, String objId) {
        mDes.set(itemDes);
        mComplete.set(itemComplete);
        mObjectId = objId;
    }

    public TravelListItemDataBean deepClone() {
        TravelListItemDataBean result = null;
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(this);
            ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi = new ObjectInputStream(bi);
            result =  (TravelListItemDataBean) oi.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}
