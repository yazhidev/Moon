package com.yazhi1992.moon.ui.usercenter;

import android.databinding.ObservableField;

/**
 * Created by zengyazhi on 2018/2/19.
 */

public class UserCenterViewModel {
    public ObservableField<String> headUrl = new ObservableField<>();
    public ObservableField<Integer> gender = new ObservableField<>();
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableField<String> email = new ObservableField<>();
    public ObservableField<Boolean> emailValid = new ObservableField<>();
}
