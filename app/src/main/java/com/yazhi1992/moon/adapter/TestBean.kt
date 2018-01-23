package com.yazhi1992.moon.adapter

import android.databinding.ObservableField

/**
 * Created by zengyazhi on 2018/1/23.
 */

class TestBean {
    var text = ObservableField<String>("test")
    var number = ObservableField<Int>()
}
