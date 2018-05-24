package com.yazhi1992.moon.ui.setuserinfo

import android.databinding.ObservableField
import com.yazhi1992.moon.constant.TypeConstant

/**
 * Created by zengyazhi on 2018/5/24.
 */
class SetUserInfoModel {
    var headUrl = ObservableField<String>("")
    var gender = ObservableField<Int>(TypeConstant.MEN)
    var userName = ObservableField<String>("")
}